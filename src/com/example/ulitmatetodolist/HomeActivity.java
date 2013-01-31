package com.example.ulitmatetodolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appacitive.android.callbacks.AppacitiveCallback;
import com.appacitive.android.callbacks.AppacitiveFetchCallback;
import com.appacitive.android.model.AppacitiveConnection;
import com.appacitive.android.model.AppacitiveError;
import com.appacitive.android.model.AppacitiveObject;
import com.appacitive.android.model.AppacitiveUser;
import com.example.ulitmatetodolist.adapters.MenuAdapter;
import com.example.ulitmatetodolist.adapters.TaskListAdapter;
import com.example.ulitmatetodolist.model.Task;
import com.example.ulitmatetodolist.model.TodoList;

public class HomeActivity extends HomePageActivity {

	private static final int DELETE_TASK = 0;
	private static final int CLOSE_TASK = 1;
	private static final int DELETE_TODOLIST = 2;
	private static final int CLOSE_TODOLIST = 3;

	private View mLoadingStatusView;
	private TextView mLoadingStatusMessageView;

	private View mAddTaskView;
	private EditText mTaskTextEditText;
	private Button mAddTaskButton;

	private ListView mTaskListView;

	private ArrayList<TodoList> mTodosList;
	private HashMap<String, List<Task>> mTaskMap;
	private ArrayList<Task> mTaskList;
	private TaskListAdapter mTaskListAdapter;

	private int mSelectedIndex = -1;

	private static final String BUNDLE_TODO_LIST_KEY = "todo_list";
	private static final String BUNDLE_TODO_MAP_KEY = "todo_list_map";
	private static final String BUNDLE_SELECTED_INDEX_KEY = "todo_list_selected_index";

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ProgressBar progressIndicator = (ProgressBar) findViewById(R.id.home_list_progressView);
		progressIndicator.getIndeterminateDrawable().setColorFilter(0xFF000000,
				android.graphics.PorterDuff.Mode.MULTIPLY);

		initViews();

		if (savedInstanceState != null) {
			mSelectedIndex = savedInstanceState
					.getInt(BUNDLE_SELECTED_INDEX_KEY);
			mTodosList = savedInstanceState
					.getParcelableArrayList(BUNDLE_TODO_LIST_KEY);
			Object taskMap = savedInstanceState
					.getSerializable(BUNDLE_TODO_MAP_KEY);
			if (taskMap != null) {
				mTaskMap = (HashMap<String, List<Task>>) taskMap;
			}
		}

		if (mTodosList == null) {
			fetchTodoList();
		} else {
			updateMenu();
		}

		if (mSelectedIndex != -1) {
			onItemSelected(mSelectedIndex);
		}

	}

	private void initViews() {

		mLoadingStatusView = findViewById(R.id.home_activity_status);
		mLoadingStatusMessageView = (TextView) findViewById(R.id.home_list_status_message);
		mTaskListView = (ListView) findViewById(R.id.home_list_tasks_list);

		mAddTaskView = findViewById(R.id.home_activity_add_task_container);
		mTaskTextEditText = (EditText) findViewById(R.id.home_activity_add_task_editText);
		mAddTaskButton = (Button) findViewById(R.id.home_activity_add_taskButton);

		mAddTaskButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSelectedIndex == -1) {
					return;
				}
				if (mLoadingStatusView.getVisibility() != View.VISIBLE) {
					mLoadingStatusView.bringToFront();
					mLoadingStatusView.setVisibility(View.VISIBLE);
					mAddTaskView.setVisibility(View.GONE);
				}
				mLoadingStatusMessageView.setText("Adding Task...");

				String taskTitle = mTaskTextEditText.getText().toString();
				long todoListId = mTodosList.get(mSelectedIndex).getObjectId();
				saveTask(todoListId, taskTitle);
			}
		});

		mAddTodoListButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mMenuStatusMessageView.setText("Adding Task List");
				final String taskListTitle = mTodoListNameEditText.getText()
						.toString().trim();
				if (taskListTitle.equals("")) {
					return;
				}
				mMenuStatusView.bringToFront();
				mMenuStatusView.setVisibility(View.VISIBLE);
				saveTodoList(taskListTitle);
			}
		});

		registerForContextMenu(mTaskListView);
		registerForContextMenu(mMenuListView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		menu.setHeaderTitle("Options");
		switch (v.getId()) {
		case R.id.menu_listView:
			menu.add(0, DELETE_TODOLIST, 0, "Delete TodoList");
			menu.add(0, CLOSE_TODOLIST, 0, "Close TodoList");
			break;
		case R.id.home_list_tasks_list:
			menu.add(0, DELETE_TASK, 0, "Delete Task");
			menu.add(0, CLOSE_TASK, 0, "Close Task");
			break;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case DELETE_TASK:
			Task task = mTaskList.get(info.position);
			mLoadingStatusMessageView.setText("Deleting Task");
			mLoadingStatusView.setVisibility(View.VISIBLE);
			deleteTask(task);
			break;
		case DELETE_TODOLIST:
			TodoList todoList = mTodosList.get(info.position);
			mMenuStatusView.bringToFront();
			mMenuStatusMessageView.setText("Deleting Todo List");
			mMenuStatusView.setVisibility(View.VISIBLE);
			deleteTodoList(todoList);
			break;
		case CLOSE_TASK:
			task = mTaskList.get(info.position);
			mLoadingStatusMessageView.setText("Closing Task");
			mLoadingStatusView.setVisibility(View.VISIBLE);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String date = df.format(new Date());
			closeTask(task, date);
			break;
		case CLOSE_TODOLIST:
			Log.d("TAG", "Reached here 4");
			todoList = mTodosList.get(info.position);
			mMenuStatusView.bringToFront();
			mMenuStatusMessageView.setText("Closing Todo List");
			mMenuStatusView.setVisibility(View.VISIBLE);
			df = new SimpleDateFormat("yyyy-MM-dd");
			date = df.format(new Date());
			closeTodoList(todoList, date);
			break;
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(BUNDLE_SELECTED_INDEX_KEY, mSelectedIndex);
		if (mTodosList != null) {
			outState.putParcelableArrayList(BUNDLE_TODO_LIST_KEY, mTodosList);
		}
		if (mTaskMap != null) {
			outState.putSerializable(BUNDLE_TODO_MAP_KEY, mTaskMap);
		}
	}

	@Override
	public void onItemSelected(final int index) {
		if (mLoadingStatusView.getVisibility() != View.VISIBLE) {
			mLoadingStatusView.bringToFront();
			mLoadingStatusView.setVisibility(View.VISIBLE);
			mAddTaskView.setVisibility(View.GONE);
		}
		mLoadingStatusMessageView.setText("Loading Tasks...");

		mSelectedIndex = index;
		if (mTaskMap != null && mTaskMap.containsKey(index + "")) {
			mTaskList = (ArrayList<Task>) mTaskMap.get(index + "");
			updateTaskListView();
			return;
		}

		final TodoList todoList = mTodosList.get(index);
		fetchTasks(todoList.getObjectId());
	}

	private void updateTaskListView() {
		mLoadingStatusView.setVisibility(View.GONE);
		mAddTaskView.setVisibility(View.VISIBLE);
		mTaskListAdapter = new TaskListAdapter(getBaseContext(), mTaskList);
		mTaskListView.setAdapter(mTaskListAdapter);
	}

	private void updateMenu() {
		if(mTodosList!= null && mTodosList.size() == 0) {
			mAddTaskView.setVisibility(View.GONE);
		}
		MenuAdapter adapter = new MenuAdapter(getBaseContext(), mTodosList);
		mMenuListView.setAdapter(adapter);
		mMenuStatusView.setVisibility(View.GONE);
	}

	private void fetchTodoList() {
		AppacitiveConnection.searchForConnectedArticles("user_lists",
				AppacitiveUser.currentUser.getObjectId(),
				new AppacitiveFetchCallback() {

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(Map<String, Object> response) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mAddTaskView.setVisibility(View.VISIBLE);
								mLoadingStatusView.setVisibility(View.GONE);
							}
						});
						List<Object> connections = (List<Object>) response
								.get("connections");
						if (connections != null) {
							createTodoList(connections);
						}
					}

					@Override
					public void onFailure(AppacitiveError error) {
						Log.d("TAG", "Reached Here in onFailure");
					}

					@SuppressWarnings("unchecked")
					private void createTodoList(final List<Object> connections) {
						if (mTodosList == null) {
							mTodosList = new ArrayList<TodoList>();
						}
						for (int i = 0; i < connections.size(); i++) {
							Map<String, Object> connection = (Map<String, Object>) connections
									.get(i);
							Map<String, Object> article = (Map<String, Object>) ((Map<String, Object>) connection
									.get("__endpointb")).get("article");
							String listTitle = (String) article
									.get("list_name");
							long id = Long.parseLong((String) article
									.get("__id"));
							TodoList todoList = new TodoList();
							todoList.setListTitle(listTitle);
							todoList.setObjectId(id);
							mTodosList.add(todoList);
						}
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								updateMenu();
								if(mTodosList.size() > 0) {
									onItemSelected(0);
								}
							}
						});
					}
				});
	}

	private void fetchTasks(final long objectId) {
		AppacitiveConnection.searchForConnectedArticles("list_items", objectId,
				new AppacitiveFetchCallback() {

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(Map<String, Object> response) {
						List<Object> connections = (List<Object>) response
								.get("connections");
						if (connections != null) {
							updateTaskList(connections);
						}
					}

					@Override
					public void onFailure(AppacitiveError error) {
						Log.d("TAG", "Error is " + error.toString());
					}

					@SuppressWarnings("unchecked")
					private void updateTaskList(final List<Object> connections) {

						mTaskList = new ArrayList<Task>();
						for (int i = 0; i < connections.size(); i++) {
							Map<String, Object> connection = (Map<String, Object>) connections
									.get(i);
							Map<String, Object> article = (Map<String, Object>) ((Map<String, Object>) connection
									.get("__endpointb")).get("article");
							String taskText = (String) article.get("text");
							long id = Long.parseLong((String) article
									.get("__id"));
							Task todoTask = new Task();
							todoTask.setTaskText(taskText);
							todoTask.setTaskId(id);
							mTaskList.add(todoTask);
						}
						if (mTaskMap == null) {
							mTaskMap = new HashMap<String, List<Task>>();
						}
						mTaskMap.put(objectId + "", mTaskList);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								updateTaskListView();
							}
						});
					}
				});

	}

	private void saveTask(final long todoListId, final String taskTitle) {
		final AppacitiveObject object = new AppacitiveObject("tasks");
		object.addProperty("text", taskTitle);
		object.saveObject(new AppacitiveCallback() {

			@Override
			public void onSuccess() {
				AppacitiveConnection connection = new AppacitiveConnection(
						"list_items");
				connection.setArticleAId(todoListId);
				connection.setLabelA("todolists");
				connection.setArticleBId(object.getObjectId());
				connection.setLabelB("tasks");

				connection.createConnection(new AppacitiveCallback() {

					@Override
					public void onSuccess() {
						Task task = new Task();
						task.setTaskId(object.getObjectId());
						task.setTaskText(taskTitle);
						if (mTaskList == null) {
							mTaskList = new ArrayList<Task>();
							mTaskMap.put(todoListId + "", mTaskList);
						}
						mTaskList.add(0, task);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mTaskTextEditText.setText("");
								updateTaskListView();
							}
						});

					}

					@Override
					public void onFailure(AppacitiveError error) {
						Log.d("TAG", "Error is " + error.toString());
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mTaskTextEditText
										.setError("Error in adding task");
								mLoadingStatusView.setVisibility(View.GONE);
							}
						});
					}
				});

			}

			@Override
			public void onFailure(AppacitiveError error) {
				Log.d("TAG", "Error is " + error.toString());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mTaskTextEditText.setError("Error in adding task");
						mLoadingStatusView.setVisibility(View.GONE);
					}
				});
			}
		});
	}

	private void saveTodoList(final String taskListTitle) {
		final AppacitiveObject object = new AppacitiveObject("todolists");
		object.addProperty("list_name", taskListTitle);
		object.saveObject(new AppacitiveCallback() {

			@Override
			public void onSuccess() {

				AppacitiveConnection connection = new AppacitiveConnection(
						"user_lists");
				connection.setArticleAId(AppacitiveUser.currentUser
						.getObjectId());
				connection.setLabelA("user");

				connection.setArticleBId(object.getObjectId());
				connection.setLabelB("todolists");

				connection.createConnection(new AppacitiveCallback() {

					@Override
					public void onSuccess() {
						TodoList taskList = new TodoList();
						taskList.setListTitle(taskListTitle);
						taskList.setObjectId(object.getObjectId());
						mTodosList.add(0, taskList);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mMenuStatusView.setVisibility(View.GONE);
								mTodoListNameEditText.setText("");
								updateMenu();
								mMenuListView.requestFocus();
							}
						});
					}

					@Override
					public void onFailure(AppacitiveError error) {
						Log.w("TAG", "Error Saving the list");
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mMenuStatusMessageView
										.setText("Error !! Saving the list");
								new Handler().postDelayed(new Runnable() {

									@Override
									public void run() {
										mMenuStatusView
												.setVisibility(View.GONE);
									}
								}, 3000);
							}
						});

					}
				});

			}

			@Override
			public void onFailure(AppacitiveError error) {
				Log.w("TAG", "Error Saving the list");
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mMenuStatusMessageView
								.setText("Error !! Saving the list");
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								mMenuStatusView.setVisibility(View.GONE);
							}
						}, 3000);
					}
				});
			}
		});
	}

	private void deleteTask(final Task task) {
		AppacitiveObject object = new AppacitiveObject("tasks");
		object.setObjectId(task.getTaskId());
		object.deleteObjectWithConnections(true, new AppacitiveCallback() {

			@Override
			public void onSuccess() {
				mTaskList.remove(task);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getBaseContext(), "Task Deleted",
								Toast.LENGTH_SHORT).show();
						mLoadingStatusView.setVisibility(View.GONE);
						updateTaskListView();
					}
				});
			}

			@Override
			public void onFailure(AppacitiveError error) {
				Log.d("TAG", "Error is " + error.toString());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getBaseContext(), "Task Not deleted",
								Toast.LENGTH_SHORT).show();
						mLoadingStatusView.setVisibility(View.GONE);
						updateTaskListView();
					}
				});
			}
		});
	}

	private void deleteTodoList(final TodoList list) {
		AppacitiveObject object = new AppacitiveObject("todolists");
		object.setObjectId(list.getObjectId());
		object.deleteObjectWithConnections(true, new AppacitiveCallback() {

			@Override
			public void onSuccess() {
				mTodosList.remove(list);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getBaseContext(), "Todo List Deleted",
								Toast.LENGTH_SHORT).show();
						mMenuStatusView.setVisibility(View.GONE);
						updateTaskListView();
					}
				});
			}

			@Override
			public void onFailure(AppacitiveError error) {
				Log.d("TAG", "Error is " + error.toString());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getBaseContext(),
								"Todo List Not deleted ! Try again",
								Toast.LENGTH_SHORT).show();
						mMenuStatusView.setVisibility(View.GONE);
						updateTaskListView();
					}
				});
			}
		});
	}

	private void closeTask(final Task task, String date) {
		AppacitiveObject object = new AppacitiveObject("tasks");
		object.setObjectId(task.getTaskId());
		object.addProperty("completed_at", date);
		object.saveObject(new AppacitiveCallback() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mLoadingStatusView.setVisibility(View.GONE);
						Toast.makeText(getBaseContext(), "Task closed",
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onFailure(AppacitiveError error) {
				Log.d("TAG", "Error is " + error.toString());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mLoadingStatusView.setVisibility(View.GONE);
						Toast.makeText(getBaseContext(), "Error. Try Again !",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void closeTodoList(final TodoList todoList, String date) {
		AppacitiveObject object = new AppacitiveObject("todolists");
		object.setObjectId(todoList.getObjectId());
		object.addProperty("completed_at", date);
		object.updateObject(new AppacitiveCallback() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mMenuStatusView.setVisibility(View.GONE);
						Toast.makeText(getBaseContext(), "Task closed",
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onFailure(AppacitiveError error) {
				Log.d("TAG", "Error is " + error.toString());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mMenuStatusView.setVisibility(View.GONE);
						Toast.makeText(getBaseContext(), "Error. Try Again !",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

}
