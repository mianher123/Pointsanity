/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pointsanity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;




public class GridTest extends Activity{
    GridView pointsGrid;
    TextView cardInfo;
    GridAdapter gridAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pointsgrid);
        pointsGrid = (GridView) findViewById(R.id.PointsGrid);
        cardInfo = (TextView) findViewById(R.id.CardInfo);
        gridAdapter = new GridAdapter();
		pointsGrid.setAdapter(gridAdapter);
		SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
		int num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString("NUMPOINTS", ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString("NUMPOINTS", ""+0).commit();
		}
		int num_cards = num_points / 10;
		for(int i=0;i<10;i++){
			if(i<(num_points % 10))
				gridAdapter.addItem(getResources().getDrawable(R.drawable.seal));
			else
				gridAdapter.addItem(getResources().getDrawable(R.drawable.empty));
		}
		cardInfo.setText("您已集滿"+num_cards+"張集點卡");
		
    }
    
    class ViewHolder {
		ImageView imageview;
		int id;
	}
    
    public class GridAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		ArrayList<Drawable> drawables = new ArrayList<Drawable>();
		Context mContext;
		FileInputStream fis;

		public GridAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return drawables.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
		public void setItem(int index,Drawable item) {
			drawables.set(index,item);
		}
		public void addItem(Drawable item) {
			drawables.add(item);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.griditem, null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// holder.checkbox.setId(position);
			holder.imageview.setId(position);
			/*
			 * holder.checkbox.setOnClickListener(new OnClickListener() {
			 * 
			 * public void onClick(View v) { // TODO Auto-generated method stub
			 * CheckBox cb = (CheckBox) v; int id = cb.getId(); if
			 * (photoselection[id]){ cb.setChecked(false); photoselection[id] =
			 * false; //Toast.makeText(MyCustomActivity.this, "onClick",
			 * Toast.LENGTH_SHORT).show(); } else { cb.setChecked(true);
			 * photoselection[id] = true;
			 * //Toast.makeText(MyCustomActivity.this, "onClick",
			 * Toast.LENGTH_SHORT).show(); } } });
			 */
			holder.imageview.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					
					

				}
			});
			// holder.imageview.setImageBitmap(thumbnails[position]);
			holder.imageview.setImageDrawable(drawables.get(position));
			
			// holder.imageview.setImageResource(R.drawable.ic_launcher);
			// holder.imageview.setLayoutParams(new CoverFlow.LayoutParams(100,
			// 100));

			holder.imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// holder.checkbox.setChecked(photoselection[position]);
			holder.id = position;
			// BitmapDrawable drawable = (BitmapDrawable)
			// holder.imageview.getDrawable();
			// drawable.setAntiAlias(true);
			return convertView;
		}
	}


}   