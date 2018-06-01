package com.example.jin.mydiary;

class Diary {
	private int _id; // SQLite는 id가 아니라 _id를 관용적으로 사용해
	private String title;
	private String content;
	private long date;
	private String photoUrl;
	private double lat;
	private double lng;

	Diary() {
	}

	//사진이 없을경우
	Diary(String title, String content, long date) {
		this.title = title;
		this.content = content;
		this.date = date;
	}

	//사진이 있을 경우
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

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}
}
