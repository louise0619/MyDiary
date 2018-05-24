package com.example.jin.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
/*
 * 1. 다이어리 입력 화면 (액티비티)
 * 1-1. 날짜 클릭 시 datePickerDialog 호출
 * 1-2. 일정 입력 후 저장버튼 눌렀을 때 MainActivity로 다이어리 던져주기
 * 2. 다이어리 수정/삭제 화면 (1번 항목 재활용 가능)
 * ---- 20180524 여기까지 완료 (수정이랑 삭제는 DB랑 함께 할 예정)
 * TODO
 * 3. 다이어리 저장 방법 (SQLite 등 DB 연동)
 * 4. 사진 전송 기능 (Firebase Storage 이용)
 */

public class MainActivity extends AppCompatActivity {
	public static final int ADD_DIARY = 1001;
	public static final int MODIFY_DIARY = 1002;

	private CalendarView cal;
	private ListView lis;
	private ArrayAdapter<Diary> adapter;
	private long selectedDate = -1L;
	private ArrayList<Diary> objects = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		selectedDate = System.currentTimeMillis();

		cal = findViewById(R.id.cal);
		cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
				Calendar selectedCalendar = Calendar.getInstance();
				selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0);
				selectedDate = selectedCalendar.getTimeInMillis();
			}
		});
		lis = findViewById(R.id.lis);
		adapter = new DiaryAdapter(this, android.R.layout.simple_list_item_2, objects);
		lis.setAdapter(adapter);

		lis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int iPosition, long lPosition) {
				Diary diary = objects.get(iPosition);
				if (diary != null) {
					Intent intent = new Intent(getApplicationContext(), AddDiaryActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("title", diary.getTitle());
					bundle.putString("content", diary.getContent());
					bundle.putLong("date", diary.getDate());
					if (!TextUtils.isEmpty(diary.getPhotoUrl())) {
						bundle.putString("photoUrl", diary.getPhotoUrl());
					}
					intent.putExtras(bundle);
					startActivityForResult(intent, MODIFY_DIARY);
				}
			}
		});

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				addDiary();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
			case ADD_DIARY:
				if (data != null && data.getExtras() != null) {
					Bundle bundle = data.getExtras();
					Diary diary;
					if (bundle.containsKey("photoUrl")) {
						diary = new Diary(bundle.getString("title"), bundle.getString("content"), bundle.getLong("date", System.currentTimeMillis()), bundle.getString("photoUrl"));
					} else {
						diary = new Diary(bundle.getString("title"), bundle.getString("content"), bundle.getLong("date", System.currentTimeMillis()));
					}
					objects.add(diary);
					adapter = new DiaryAdapter(this, android.R.layout.simple_list_item_2, objects);
					lis.setAdapter(adapter);
					Toast.makeText(this, "다이어리를 추가했어요!", Toast.LENGTH_SHORT).show();
				}
				break;
			case MODIFY_DIARY:
				if (data != null && data.getExtras() != null) {
					Bundle bundle = data.getExtras();
					Diary diary;
					if (bundle.containsKey("photoUrl")) {
						diary = new Diary(bundle.getString("title"), bundle.getString("content"), bundle.getLong("date", System.currentTimeMillis()), bundle.getString("photoUrl"));
					} else {
						diary = new Diary(bundle.getString("title"), bundle.getString("content"), bundle.getLong("date", System.currentTimeMillis()));
					}
					objects.add(diary);
					adapter = new DiaryAdapter(this, android.R.layout.simple_list_item_2, objects);
					lis.setAdapter(adapter);
					Toast.makeText(this, "다이어리를 수정했어요!", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}

	private void addDiary() {
		Intent intent = new Intent(MainActivity.this, AddDiaryActivity.class);
		startActivityForResult(intent, ADD_DIARY);
	}
}
