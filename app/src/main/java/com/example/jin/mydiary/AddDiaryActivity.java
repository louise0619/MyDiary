package com.example.jin.mydiary;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class AddDiaryActivity extends AppCompatActivity {
	private EditText edtTitle;
	private EditText edtContent;
	private EditText edtDate;
	private FrameLayout flImg;
	private ImageView img;
	private Button btnImg;
	private Button btnCancel;
	private Button btnSave;

	private Calendar selectedDate;
	private String photoUrl = "";

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

		edtTitle = findViewById(R.id.edt_title);
		edtContent = findViewById(R.id.edt_content);
		edtDate = findViewById(R.id.edt_date);
		img = findViewById(R.id.img);
		flImg = findViewById(R.id.fl_img);
		btnImg = findViewById(R.id.btn_img);
		btnCancel = findViewById(R.id.btn_cancel);
		btnSave = findViewById(R.id.btn_save);
		selectedDate = Calendar.getInstance();
		selectedDate.setTimeInMillis(getIntent().getLongExtra("date", System.currentTimeMillis()));
		edtDate.setText(String.format(Locale.getDefault(), "%4d-%02d-%02d", selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH) + 1, selectedDate.get(Calendar.DAY_OF_MONTH)));

		if (getIntent().getExtras() != null) {
			Bundle bundle = getIntent().getExtras();
			if (actionBar != null) {
				actionBar.setTitle("기존 다이어리 수정");
			}
			edtTitle.setText(bundle.getString("title"));
			edtContent.setText(bundle.getString("content"));
			selectedDate.setTimeInMillis(bundle.getLong("date", System.currentTimeMillis()));
			edtDate.setText(String.format(Locale.getDefault(), "%4d-%02d-%02d", selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH) + 1, selectedDate.get(Calendar.DAY_OF_MONTH)));
			if (bundle.containsKey("photoUrl")) {
				// TODO set photoURL into img
				btnImg.setText("이미지 수정");
			}
		}

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

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		btnSave.setOnClickListener(view -> {
			String title = edtTitle.getText().toString();
			String content = edtContent.getText().toString();
			if (TextUtils.isEmpty(title)) {
				Toast.makeText(this, "제목과 내용을 전부 입력하세요!", Toast.LENGTH_SHORT).show();
				edtTitle.requestFocus();
				return;
			}

			if (TextUtils.isEmpty(content)) {
				Toast.makeText(this, "제목과 내용을 전부 입력하세요!", Toast.LENGTH_SHORT).show();
				edtContent.requestFocus();
				return;
			}
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("title", title);
			bundle.putString("content", content);
			bundle.putLong("date", selectedDate.getTimeInMillis());
			if (!TextUtils.isEmpty(photoUrl)) {
				bundle.putString("photoUrl", photoUrl);
			}
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}