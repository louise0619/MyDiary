package com.example.jin.mydiary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter extends ArrayAdapter<Diary> {
	private Context context;
	private List<Diary> objects;

	public DiaryAdapter(@NonNull Context context, int resource, @NonNull List<Diary> objects) {
		super(context, resource, objects);
		this.context = context;
		this.objects = objects;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		View itemView = convertView;
		if (itemView == null) {
			itemView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
		}

		Diary diary = objects.get(position);
		if (diary != null) {
			TextView text1 = itemView.findViewById(android.R.id.text1);
			text1.setText(diary.getTitle());

			TextView text2 = itemView.findViewById(android.R.id.text2);
			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(diary.getDate());
			text2.setText(String.format(Locale.getDefault(), "%4d. %02d. %02d", date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH)));
		}
		return itemView;
	}
}
