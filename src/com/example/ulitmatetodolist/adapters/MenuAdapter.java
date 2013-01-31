package com.example.ulitmatetodolist.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ulitmatetodolist.R;
import com.example.ulitmatetodolist.model.TodoList;

public class MenuAdapter extends BaseAdapter {
	
	private List<TodoList> mList;
	private Context mContext;
	
	public MenuAdapter(Context context, List<TodoList> list) {
		this.mContext = context;
		this.mList = list;
	}
	
	@Override
	public int getCount() {
		if(mList == null) {
			return 0;
		}
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.menu_list_item, null);
		}
		TextView menuTitleView = (TextView) convertView.findViewById(R.id.menu_item_text);
		menuTitleView.setText(mList.get(position).getListTitle());
		return convertView;
	}

}
