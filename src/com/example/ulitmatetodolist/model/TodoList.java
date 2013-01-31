package com.example.ulitmatetodolist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TodoList implements Parcelable{

	private long mObjectId;
	private String mListTitle;
	
	public void setListTitle(String listTitle) {
		this.mListTitle = listTitle;
	}
	
	public String getListTitle() {
		return this.mListTitle;
	}

	public void setObjectId(long objectId) {
		this.mObjectId = objectId;
	}
	
	public long getObjectId() {
		return this.mObjectId;
	}

	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mObjectId);
		dest.writeString(mListTitle);
	}
	
	public static final Parcelable.Creator<TodoList> CREATOR = new Parcelable.Creator<TodoList>() {

				@Override
				public TodoList createFromParcel(Parcel source) {
					TodoList todos = new TodoList();
					todos.setObjectId(source.readLong());
					todos.setListTitle(source.readString());
					return todos;
				}

				@Override
				public TodoList[] newArray(int size) {
					return null;
				}
	};
	
}
