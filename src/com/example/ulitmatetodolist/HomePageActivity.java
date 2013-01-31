package com.example.ulitmatetodolist;

import net.simonvt.widget.MenuDrawer;
import net.simonvt.widget.MenuDrawerManager;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public abstract class HomePageActivity extends FragmentActivity implements
		OnItemClickListener {

	private static final String STATE_MENUDRAWER = "net.simonvt.menudrawer.samples.WindowSample.menuDrawer";
	private static final String STATE_ACTIVE_VIEW_ID = "net.simonvt.menudrawer.samples.WindowSample.activeViewId";

	private MenuDrawerManager mMenuDrawer;
	private int mActiveViewId;

	protected MenuListView mMenuListView;
	protected TextView mMenuStatusMessageView;
	protected View mMenuStatusView;

	protected View mAddTaskListView;
	protected EditText mTodoListNameEditText;
	protected Button mAddTodoListButton;

	private View mSelectedView;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle inState) {
		super.onCreate(inState);
		mMenuDrawer = new MenuDrawerManager(this, MenuDrawer.MENU_DRAG_CONTENT);
		mMenuDrawer.setContentView(R.layout.activity_main);
		mMenuDrawer.setMenuView(R.layout.menu_list);

		initViews();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		// This will animate the drawer open and closed until the user manually
		// drags it. Usually this would only be
		// called on first launch.
		mMenuDrawer.getMenuDrawer().peekDrawer();
	}

	private void initViews() {
		View menuView = mMenuDrawer.getMenuView();
		mMenuListView = (MenuListView) menuView
				.findViewById(R.id.menu_listView);
		mMenuListView
				.setOnScrollChangedListener(new MenuListView.OnScrollChangedListener() {

					@Override
					public void onScrollChanged() {
						mMenuDrawer.getMenuDrawer().invalidate();
					}
				});

		mMenuListView.setOnItemClickListener(this);
		mMenuStatusView = menuView.findViewById(R.id.menu_list_status_view);
		mMenuStatusMessageView = (TextView) menuView
				.findViewById(R.id.menu_list_status_message);

		mAddTaskListView = menuView
				.findViewById(R.id.menu_list_add_todo_list_container);
		mTodoListNameEditText = (EditText) menuView
				.findViewById(R.id.menu_list_todo_list_editText);
		mAddTodoListButton = (Button) menuView
				.findViewById(R.id.menu_list_todo_list_addButton);

	}

	@Override
	protected void onRestoreInstanceState(Bundle inState) {
		super.onRestoreInstanceState(inState);
		mMenuDrawer.onRestoreDrawerState(inState
				.getParcelable(STATE_MENUDRAWER));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(STATE_MENUDRAWER,
				mMenuDrawer.onSaveDrawerState());
		outState.putInt(STATE_ACTIVE_VIEW_ID, mActiveViewId);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN
				|| drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return;
		}
		super.onBackPressed();
	}

	public abstract void onItemSelected(int index);

	@Override
	public void onItemClick(AdapterView<?> v, View view, int index, long id) {
		mMenuDrawer.setActiveView(v);
		mMenuDrawer.closeMenu();
		if (mSelectedView != null) {
			mSelectedView.setBackgroundColor(Color.TRANSPARENT);
		}
		setSelectedMenuItem(view);
		onItemSelected(index);
	}

	protected void setSelectedMenuItem(View v) {
		mSelectedView = v;
		mSelectedView.setBackgroundColor(Color.YELLOW);
	}
}
