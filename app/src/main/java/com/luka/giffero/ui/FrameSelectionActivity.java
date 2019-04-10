package com.luka.giffero.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.luka.giffero.R;

import java.util.ArrayList;

public class FrameSelectionActivity extends Activity {

	public static String SelectedFrameID = "SelectedFrameID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame_selection);

		initUI();
	}

	void initUI() {
		final ArrayList<Integer> arrFrames = new ArrayList<>();
		arrFrames.add(R.drawable.frame_0);
		arrFrames.add(R.drawable.frame_1);
		arrFrames.add(R.drawable.frame_2);

		GridView gridView = findViewById(R.id.gridView);
		gridView.setAdapter(new FrameGridAdapter(this, arrFrames));
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent data = new Intent();
				data.putExtra(SelectedFrameID,arrFrames.get(position));
				setResult(RESULT_OK, data);
				finish();
			}
		});
	}

	private class FrameGridAdapter extends BaseAdapter {

		Context context = null;
		ArrayList<Integer> arrFrames = null;

		FrameGridAdapter(Context context, ArrayList<Integer> arrFrames) {
			super();

			this.context = context;
			this.arrFrames = arrFrames;
		}

		@Override
		public int getCount() {
			return arrFrames.size();
		}

		@Override
		public Object getItem(int position) {
			return arrFrames.get(position);
		}

		@Override
		public long getItemId(int position) {
			return arrFrames.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				final LayoutInflater layoutInflater = LayoutInflater.from(context);
				convertView = layoutInflater.inflate(R.layout.layout_frame_selection_cell, null);
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				convertView.setElevation(10);

			int frameID = arrFrames.get(position);
			ImageView imageView = (ImageView)convertView.findViewById(R.id.frameImageView);
			imageView.setImageResource(frameID);

			return convertView;
		}
	}
}
