package com.example.jin.mydiary;

class Diary {
	private String title;
	private String content;
	private long date;
	private String photoUrl;

	Diary() {
	}

	Diary(String title, String content, long date) {
		this.title = title;
		this.content = content;
		this.date = date;
	}

	Diary(String title, String content, long date, String photoUrl) {
		this.title = title;
		this.content = content;
		this.date = date;
		this.photoUrl = photoUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
}
