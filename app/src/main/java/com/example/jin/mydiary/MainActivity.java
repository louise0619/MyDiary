package com.example.jin.mydiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.BatteryManager;
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
 * 3. 음악 플레이어 삽입 (노래 하나 넣어서 재생/일시정지)
 * 4. 브로드캐스트 구현 (배터리 상태 알림)
 * ---- 20180530 여기까지 완료
 * 5. 지도 삽입 (약속 장소 몇 개 보여주기, 장소는 내가 미리 입력해둔 장소만 표시)
 * 6. 다이어리 저장 방법 (SQLite 등 DB 연동)
 * 7. 스티커 삽입
 * ---- 20180601 여기까지 완료!
 */

public class MainActivity extends AppCompatActivity {
	public static final int ADD_DIARY = 1001;
	public static final int MODIFY_DIARY = 1002;

	private CalendarView cal;
	private ListView lis;
	private ArrayAdapter<Diary> adapter;
	private ArrayList<Diary> objects = new ArrayList<>();
	private MediaPlayer mPlayer;
	private MyDBHelper myHelper;
	private SQLiteDatabase sqlDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		myHelper = new MyDBHelper(this);

		cal = findViewById(R.id.cal);
		cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
				objects = getObjectsFromDB(year, month, dayOfMonth);
				adapter = new DiaryAdapter(MainActivity.this, android.R.layout.simple_list_item_2, objects);
				lis.setAdapter(adapter);
			}
		});
		lis = findViewById(R.id.lis);
		objects = getObjectsFromDB();
		adapter = new DiaryAdapter(this, android.R.layout.simple_list_item_2, objects);
		lis.setAdapter(adapter);
		//일기를 추가해 리스트목록에서 선택했을 때
		lis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int iPosition, long lPosition) {
				Diary diary = objects.get(iPosition);
				if (diary != null) {
					Intent intent = new Intent(getApplicationContext(), AddDiaryActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("_id", diary.get_id());
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
		//fab을 클릭하면, 일기를 추가하는 클래스 호출
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(new Intent(MainActivity.this, AddDiaryActivity.class), ADD_DIARY);
			}
		});

		mPlayer = MediaPlayer.create(this, R.raw.song1);
	}

	//브로드 캐스트 :실행중
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(br, iFilter);
	}

	//브로드 캐스트 :앱 중지
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(br);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	//메뉴 선택시 음악재생 및 지도
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_music:

				if (mPlayer != null) {
					if (mPlayer.isPlaying()) {
						mPlayer.pause();
						item.setTitle("음악 재생");
					} else {
						mPlayer.start();
						item.setTitle("음악 일시정지");
					}
				}
				return true;
			case R.id.action_map:
				startActivity(new Intent(MainActivity.this, MapActivity.class));
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	// startActivityForResult()로 실행한 액티비티가 종료되었을 때 호출됨
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 결과가 OK(저장)이 아니면 아무 동작도 하지 않음
		if (resultCode != RESULT_OK) {
			return;
		}
		// 결과가 OK(저장)일 때 requestCode에 따른 작업 수행
		switch (requestCode) {
			case ADD_DIARY: // 다이어리 추가 시
				if (data != null && data.getExtras() != null) {
					Bundle bundle = data.getExtras();
					Diary diary;
					if (bundle.containsKey("photoUrl")) {
						diary = new Diary(bundle.getString("title"), bundle.getString("content"), bundle.getLong("date", System.currentTimeMillis()), bundle.getString("photoUrl"));
					} else {
						diary = new Diary(bundle.getString("title"), bundle.getString("content"), bundle.getLong("date", System.currentTimeMillis()));
					}
					sqlDB = myHelper.getWritableDatabase();
					if (TextUtils.isEmpty(diary.getPhotoUrl())) {
						// String query = String.format(Locale.getDefault(), "INSERT INTO diaryDB (title, content, date, photo) VALUES ('%s', '%s', '%s');", diary.getTitle(), diary.getContent(), diary.getDate());
						sqlDB.execSQL("INSERT INTO diaryDB (title, content, date) VALUES ( '"
								+ diary.getTitle() + "' , '"
								+ diary.getContent() + "' , "
								+ diary.getDate() + ");");
					} else {
						sqlDB.execSQL("INSERT INTO diaryDB (title, content, date, photo) VALUES ( '"
								+ diary.getTitle() + "' , '"
								+ diary.getContent() + "' , "
								+ diary.getDate() + " , '"
								+ diary.getPhotoUrl() + "');");
					}
					sqlDB.close();

					objects = getObjectsFromDB();
					adapter = new DiaryAdapter(this, android.R.layout.simple_list_item_2, objects);
					lis.setAdapter(adapter);
					Toast.makeText(this, "다이어리를 추가했어요!", Toast.LENGTH_SHORT).show();
				}
				break;
			case MODIFY_DIARY: // 다이어리 수정 시
				if (data != null && data.getExtras() != null) {
					Bundle bundle = data.getExtras();
					Diary diary;
					if (bundle.containsKey("photoUrl")) {
						diary = new Diary(bundle.getString("title"), bundle.getString("content"), bundle.getLong("date", System.currentTimeMillis()), bundle.getString("photoUrl"));
					} else {
						diary = new Diary(bundle.getString("title"), bundle.getString("content"), bundle.getLong("date", System.currentTimeMillis()));
					}
					diary.set_id(bundle.getInt("_id"));

					sqlDB = myHelper.getWritableDatabase();
					if (TextUtils.isEmpty(diary.getPhotoUrl())) {
						sqlDB.execSQL("UPDATE diaryDB SET title = '"
								+ diary.getTitle() + "' , content = '"
								+ diary.getContent() + "' , date = "
								+ diary.getDate() + " WHERE _id = '"
								+ diary.get_id() + "';");
					} else {
						sqlDB.execSQL("UPDATE diaryDB SET title = '"
								+ diary.getTitle() + "' , content = '"
								+ diary.getContent() + "' , date = "
								+ diary.getDate() + " , photo = '"
								+ diary.getPhotoUrl() + "' WHERE _id = '"
								+ diary.get_id() + "';");
					}
					sqlDB.close();

					objects = getObjectsFromDB();
					adapter = new DiaryAdapter(this, android.R.layout.simple_list_item_2, objects);
					lis.setAdapter(adapter);
					Toast.makeText(this, "다이어리를 수정했어요!", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}

	private ArrayList<Diary> getObjectsFromDB() {
		ArrayList<Diary> objects = new ArrayList<>();
		sqlDB = myHelper.getReadableDatabase();
		Cursor cursor = sqlDB.rawQuery("SELECT * FROM diaryDB;", null);

		while (cursor.moveToNext()) {
			Diary diary = new Diary();
			diary.set_id(cursor.getInt(0));
			diary.setTitle(cursor.getString(1));
			diary.setContent(cursor.getString(2));
			diary.setDate(cursor.getLong(3));
			diary.setPhotoUrl(cursor.getString(4));
			objects.add(diary);
		}
		cursor.close();
		sqlDB.close();

		return objects;
	}

	private ArrayList<Diary> getObjectsFromDB(int year, int month, int dayOfMonth) {
		ArrayList<Diary> objects = new ArrayList<>();
		sqlDB = myHelper.getReadableDatabase();
		long startMillis;
		long endMillis;
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, dayOfMonth, 0, 0, 0);
		startMillis = calendar.getTimeInMillis();
		calendar.set(year, month, dayOfMonth, 23, 59, 59);
		endMillis = calendar.getTimeInMillis();
		Cursor cursor = sqlDB.rawQuery("SELECT * FROM diaryDB WHERE date >= " + startMillis + " AND date <= " + endMillis + ";", null);

		while (cursor.moveToNext()) {
			Diary diary = new Diary();
			diary.set_id(cursor.getInt(0));
			diary.setTitle(cursor.getString(1));
			diary.setContent(cursor.getString(2));
			diary.setDate(cursor.getLong(3));
			diary.setPhotoUrl(cursor.getString(4));
			objects.add(diary);
		}
		cursor.close();
		sqlDB.close();

		return objects;
	}

	//브로드 캐스트
	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				// 배터리 상태 출력
				int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
				switch (status) {
					case BatteryManager.BATTERY_STATUS_CHARGING:
						Toast.makeText(MainActivity.this, "배터리 상태: 현재 충전중임", Toast.LENGTH_SHORT).show();
						break;
					case BatteryManager.BATTERY_STATUS_DISCHARGING:
						Toast.makeText(MainActivity.this, "배터리 상태: 현재 충전중 아님", Toast.LENGTH_SHORT).show();
						break;
					case BatteryManager.BATTERY_STATUS_FULL:
						Toast.makeText(MainActivity.this, "배터리 상태: 충전 100% 완료", Toast.LENGTH_SHORT).show();
						break;
					case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
						Toast.makeText(MainActivity.this, "배터리 상태: 방전됨", Toast.LENGTH_SHORT).show();
						break;
					default:
						Toast.makeText(MainActivity.this, "배터리 상태: 상태 알 수 없음", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		}
	};
}
