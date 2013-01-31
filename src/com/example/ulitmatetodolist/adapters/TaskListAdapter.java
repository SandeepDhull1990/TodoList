package com.example.ulitmatetodolist.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ulitmatetodolist.R;
import com.example.ulitmatetodolist.model.Task;

public class TaskListAdapter extends BaseAdapter {

	private List<Task> mTaskList;
	private Context mContext;
	
	public TaskListAdapter(Context context, List<Task> taskList) {
		this.mContext = context;
		this.mTaskList = taskList;
	}
	
	@Override
	public int getCount() {
		return this.mTaskList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mTaskList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.task_list_item, null);
		}
		TextView contentTextView = (TextView) convertView.findViewById(R.id.task_list_item_content);
		
		Task task = mTaskList.get(position);
		contentTextView.setText(task.getTaskText());
		return convertView;
	}

}
