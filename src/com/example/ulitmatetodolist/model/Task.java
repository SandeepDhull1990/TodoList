package com.example.ulitmatetodolist.model;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mTaskText;
	private Date mCompletionDate;
	private long mTaskId;
	public final static String mSchemaType = "tasks";

	public String getTaskText() {
		return mTaskText;
	}

	public void setTaskText(String mTaskText) {
		this.mTaskText = mTaskText;
	}

	public long getTaskId() {
		return mTaskId;
	}

	public void setTaskId(long mTaskId) {
		this.mTaskId = mTaskId;
	}

	public Date getCompletionDate() {
		return mCompletionDate;
	}

	public void setCompletionDate(Date mCompletionDate) {
		this.mCompletionDate = mCompletionDate;
	}

	@Override
	public String toString() {
		return "Task [mTaskText=" + mTaskText + ", mCompletionDate="
				+ mCompletionDate + ", mTaskId=" + mTaskId + "]";
	}

}
