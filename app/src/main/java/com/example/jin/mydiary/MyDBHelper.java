package com.example.jin.mydiary;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
	public MyDBHelper(Context context) {
		super(context, "diaryDB", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE diaryDB ( _id INTEGER PRIMARY KEY AUTOINCREMENT, title CHAR(40), content CHAR(400), date INTEGER, photo CHAR(200));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS diaryDB");
		onCreate(db);
	}
}