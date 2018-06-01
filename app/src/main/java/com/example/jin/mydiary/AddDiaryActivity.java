package com.example.jin.mydiary;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class AddDiaryActivity extends AppCompatActivity {
	private EditText edtTitle;
	private EditText edtContent;
	private EditText edtDate;
	private Spinner spiEmo;
	private ImageView img;

	private Calendar selectedDate;
	private String photoUrl = "";
	private boolean isAdd = true; // 다이어리가 추가 상태인지, 수정 상태인지 판별하기 위한 변수
	private int _id;

	private final String[] emoticons = {"즐거움", "기쁨", "수줍음", "심각함", "슬픔", "화남"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_diary);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle("새 다이어리 추가");
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}

		// 뷰 초기화
		edtTitle = findViewById(R.id.edt_title);
		edtContent = findViewById(R.id.edt_content);
		edtDate = findViewById(R.id.edt_date);
		spiEmo = findViewById(R.id.spi_emo);
		spiEmo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emoticons));
		spiEmo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				photoUrl = "emo0" + (position + 1);
				setEmoticon(photoUrl);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		img = findViewById(R.id.img);
		Button btnCancel = findViewById(R.id.btn_cancel);
		Button btnSave = findViewById(R.id.btn_save);

		selectedDate = Calendar.getInstance();
		edtDate.setText(String.format(Locale.getDefault(), "%4d-%02d-%02d", selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH) + 1, selectedDate.get(Calendar.DAY_OF_MONTH)));

		// 다이어리를 수정할 때
		if (getIntent().getExtras() != null) {
			isAdd = false;
			Bundle bundle = getIntent().getExtras();
			if (actionBar != null) {
				actionBar.setTitle("기존 다이어리 수정");
			}
			_id = bundle.getInt("_id");
			edtTitle.setText(bundle.getString("title"));
			edtContent.setText(bundle.getString("content"));
			selectedDate.setTimeInMillis(bundle.getLong("date", System.currentTimeMillis()));
			edtDate.setText(String.format(Locale.getDefault(), "%4d-%02d-%02d", selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH) + 1, selectedDate.get(Calendar.DAY_OF_MONTH)));
			if (bundle.containsKey("photoUrl")) {
				photoUrl = bundle.getString("photoUrl");
				setEmoticon(photoUrl);
			}
		}
//날짜변경
		edtDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(AddDiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
						selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
						edtDate.setText(String.format(Locale.getDefault(), "%4d-%02d-%02d", year, month + 1, dayOfMonth));
					}
				}, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.show();
			}
		});
//일기 저장 취소
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
//일기 저장
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String title = edtTitle.getText().toString();
				String content = edtContent.getText().toString();
				if (TextUtils.isEmpty(title)) {
					Toast.makeText(AddDiaryActivity.this, "제목과 내용을 전부 입력하세요!", Toast.LENGTH_SHORT).show();
					edtTitle.requestFocus();
					return;
				}

				if (TextUtils.isEmpty(content)) {
					Toast.makeText(AddDiaryActivity.this, "제목과 내용을 전부 입력하세요!", Toast.LENGTH_SHORT).show();
					edtContent.requestFocus();
					return;
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				if (!isAdd) {
					bundle.putInt("_id", _id);
				}
				bundle.putString("title", title);
				bundle.putString("content", content);
				bundle.putLong("date", selectedDate.getTimeInMillis());
				if (!TextUtils.isEmpty(photoUrl)) {
					bundle.putString("photoUrl", photoUrl);
				}
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	//탭바?에 뒤로가기 버튼
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	void setEmoticon(String photoUrl) {
		switch (photoUrl) {
			case "emo01":
				img.setImageResource(R.drawable.emo01);
				spiEmo.setSelection(0);
				break;
			case "emo02":
				img.setImageResource(R.drawable.emo02);
				spiEmo.setSelection(1);
				break;
			case "emo03":
				img.setImageResource(R.drawable.emo03);
				spiEmo.setSelection(2);
				break;
			case "emo04":
				img.setImageResource(R.drawable.emo04);
				spiEmo.setSelection(3);
				break;
			case "emo05":
				img.setImageResource(R.drawable.emo05);
				spiEmo.setSelection(4);
				break;
			case "emo06":
				img.setImageResource(R.drawable.emo06);
				spiEmo.setSelection(5);
				break;
		}
	}
}