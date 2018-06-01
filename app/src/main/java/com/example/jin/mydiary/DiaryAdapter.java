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

//다이어리를 배열에 저장해 리스트 형식으로 보여주는
public class DiaryAdapter extends ArrayAdapter<Diary> {
	private Context context;
	private List<Diary> objects;

	DiaryAdapter(@NonNull Context context, int resource, @NonNull List<Diary> objects) {
		super(context, resource, objects);
		this.context = context;
		this.objects = objects;
	}

	// 데이터셋의 지정된 위치에 데이터를 표시하는 뷰를 가져옴
	@NonNull // 이 메소드가 반환하는 값은 null일 수 없음을 명시(annotation)
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		View itemView = convertView;        // convertView를 새 변수에 재할당
		if (itemView == null) {          //itemview가 비어있을 경우 리스트를 false로 설정.
			itemView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
		}
		//position값을 diary에 넣음
		Diary diary = objects.get(position);
		if (diary != null) {        //diary값이 널이 아니면 리스트로 출력
			TextView text1 = itemView.findViewById(android.R.id.text1);
			text1.setText(diary.getTitle());        //list_item2번째에서 text1에 제목을 넣음

			TextView text2 = itemView.findViewById(android.R.id.text2);
			Calendar date = Calendar.getInstance();  //list_item2번째에서 text2에 날짜를 넣음
			date.setTimeInMillis(diary.getDate());
			text2.setText(String.format(Locale.getDefault(), "%4d. %02d. %02d", date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH)));
		}
		return itemView;
	}
}
