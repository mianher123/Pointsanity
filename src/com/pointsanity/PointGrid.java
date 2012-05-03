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
import android.net.ConnectivityManager;
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
import android.view.KeyEvent;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.pointsanity.Beam.GridAdapter;




public class PointGrid extends Activity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback{
    GridView pointsGrid;
    TextView cardInfo;
    GridAdapter gridAdapter;
    ImageView mSignBoard;
    ImageView mNFC;
    ImageView mWifi;
    ImageView mRefresh;
    ImageView mMap;
    NfcAdapter mNfcAdapter;
    String title;
    private static final int MESSAGE_SENT = 1;
    private static final int REFRESH_SUCCESS = 2;
    String FBID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pointsgrid);
        pointsGrid = (GridView) findViewById(R.id.PointsGrid);
        cardInfo = (TextView) findViewById(R.id.CardInfo);
        mSignBoard = (ImageView) findViewById(R.id.shopView);
        //mNFC = (ImageView) findViewById(R.id.nfcView);
        gridAdapter = new GridAdapter();
		pointsGrid.setAdapter(gridAdapter);
		SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
       	String FBID = settings.getString("ID", "");
		Bundle bundle = getIntent().getExtras();
		title = bundle.getString("abbr");
		
		mNFC = (ImageView) findViewById(R.id.nfcView1);
        mWifi = (ImageView) findViewById(R.id.wifiView1);
        mRefresh = (ImageView) findViewById(R.id.refreshView1);
        mMap = (ImageView) findViewById(R.id.mapView1);
        if(checkWifiStatus()){
        	mWifi.setImageResource(R.drawable.wifi_on);
        	
        }
        else
        	mWifi.setImageResource(R.drawable.wifi_off);
       	
        //updateDistanceInfo();
        
       	
       	
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            
            mNFC.setImageResource(R.drawable.nfc_off);
            
        }
        else
        	mNFC.setImageResource(R.drawable.nfc_on);
        mNFC.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		mNFC.setImageResource(R.drawable.nfc_off);
        		Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        		
        	};
       	});
        mRefresh.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		mRefresh.setImageResource(R.drawable.refresh_off);
        		Runnable ConnectRun = new Runnable(){  
      	       	  
               		public void run() {  
               			updatePoints();
                        mHandler.obtainMessage(REFRESH_SUCCESS).sendToTarget();
               		}  
               		  };  	
        		new Thread(ConnectRun).start(); 
        		
        	};
       	});
        mMap.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		mMap.setImageResource(R.drawable.map_off);
        		Log.d("Debug","In mMap");
        		Intent intent = new Intent();
		    	intent.setClass(PointGrid.this,Map.class);
		    	startActivity(intent);
        	};
       	});
        mWifi.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		mWifi.setImageResource(R.drawable.wifi_off);
        		Toast.makeText(getApplicationContext(), "Please activate WIFI and press Back to return to the application!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        		
        	};
       	});
        // Register callback to set NDEF message
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        // Register callback to listen for message-sent success
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
		
    }
    private String updateToServer(String s){
		String address = "122.116.119.134";// �s�u��ip
	    int port = 5566;// �s�u��port
	    Socket client = new Socket();
	       
        InetSocketAddress isa = new InetSocketAddress(address, port);
        /*try {
			server=new ServerSocket(7788);
		} catch (IOException e1) {
			System.out.println("serverSocket�إߦ����D !");
            System.out.println("IOException :" + e1.toString());
		}*/
        
        try {
            client.connect(isa, 15000);
            BufferedOutputStream out = new BufferedOutputStream(client
                    .getOutputStream());
            //Log.d("Debug","已得到out");
            BufferedInputStream in = new BufferedInputStream(client
                    .getInputStream());
            //Log.d("Debug","已得到in");
            // �e�X�r��
            out.write(s.getBytes());
            out.flush();
            /*out.close();
            out = null;*/
            Log.d("Debug","已送出字串");
            
            byte[] b = new byte[1024];
            String data = "";
            int length;
            //while ((length = in.read(b)) > 0)// <=0���ܴN�O�����F
            length = in.read(b);
            data += new String(b, 0, length);
            
            Log.d("Debug","我取得的值:" + data);
            //System.out.println("�ڨ�o����:" + data);
            in.close();
            in = null;
            Log.d("Debug","已讀取完畢");
            /*synchronized (server) {
                socket = server.accept();
            }*/
           // System.out.println("��o�s�u : InetAddress = " + socket.getInetAddress());
            //Log.d("Debug","��o�s�u : InetAddress = " + socket.getInetAddress());
            // TimeOut�ɶ�
            //socket.setSoTimeout(15000);

            /*in = new java.io.BufferedInputStream(socket.getInputStream());
            byte[] b = new byte[1024];
            String data = "";
            int length;
            while ((length = in.read(b)) > 0)// <=0���ܴN�O�����F
            {
                data += new String(b, 0, length);
            }
            Log.d("Debug","�ڨ�o����:" + data);
            //System.out.println("�ڨ�o����:" + data);
            in.close();
            in = null;
			*/
            client.close();
            client = null;
            Log.d("Debug","關閉socket");
            return data;
        } catch (java.io.IOException e) {
            System.out.println("Socket連線有問題 !");
            System.out.println("IOException :" + e.toString());
            return null;
        }
    
		
		
	}
    
    void updatePoints(){
    	SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
    	String serverResult = updateToServer("REFRESH "+settings.getString("ID", ""));
    	if(serverResult.startsWith("REFRESHRESULT")){
    		String[] part = serverResult.split(" ");
    		
    		for(int i=1;i<part.length;i+=2){
    			settings.edit().putString(part[i].toLowerCase(), part[i+1]).commit();
    		
    		}
    	}
    	
    }
    boolean checkWifiStatus()    {
    	ConnectivityManager connMgr = (ConnectivityManager)
    	this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    	if( wifi.isAvailable() || mobile.isAvailable()){
    		return true;
    	}
    	else
    		return false;
    }
    private void InfoUpdate(){
    	
    	String uri = "drawable/title_"+title;
		int imageResource = getResources().getIdentifier(uri, null, getPackageName());
		mSignBoard.setImageDrawable(getResources().getDrawable(imageResource));
		
    	gridAdapter = new GridAdapter();
		
    	SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
		int num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString(title, ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString(title, ""+0).commit();
		}
		int num_cards = num_points / 10;
		for(int i=0;i<10;i++){
			if(i<(num_points % 10)){
			 gridAdapter.addItem(getResources().getDrawable(R.drawable.seal));
			}
			else
				gridAdapter.addItem(getResources().getDrawable(R.drawable.empty));
		}
		cardInfo.setText("您已集滿"+num_cards+"張集點卡");
		pointsGrid.setAdapter(gridAdapter);
    	
    	
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
    public NdefMessage createNdefMessage(NfcEvent event) {
        //Time time = new Time();
        //time.setToNow();
        //String text = ("Beam me up!\n\n" + "Beam Time: " + time.format("%H:%M:%S"));
    	SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
       	FBID = settings.getString("ID", "");
    	String text ="ID "+FBID;
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMimeRecord(
                        "application/com.pointsanity", text.getBytes())
         /**
          * The Android Application Record (AAR) is commented out. When a device
          * receives a push with an AAR in it, the application specified in the AAR
          * is guaranteed to run. The AAR overrides the tag dispatch system.
          * You can add it back in to guarantee that this
          * activity starts when receiving a beamed message. For now, this code
          * uses the tag dispatch system.
          */
          ,NdefRecord.createApplicationRecord("com.pointsanity")
        });
        return msg;
    }

    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
        
    }

    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
                Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                break;
            case REFRESH_SUCCESS:
        		Toast.makeText(getApplicationContext(), "已更新點數", Toast.LENGTH_SHORT).show();
        		InfoUpdate();
        		mRefresh.setImageResource(R.drawable.refresh_on);
        		break;
        	
            }
        }
    };
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d("Debug","Beam onResume");
        mMap.setImageResource(R.drawable.map_on);
        mRefresh.setImageResource(R.drawable.refresh_on);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            
            mNFC.setImageResource(R.drawable.nfc_off);
            
        }
        else
        	mNFC.setImageResource(R.drawable.nfc_on);
        if(checkWifiStatus()){
        	mWifi.setImageResource(R.drawable.wifi_on);
        	
        }
        else
        	mWifi.setImageResource(R.drawable.wifi_off);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        /*if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            
            mNFC.setImageResource(R.drawable.nfc_off);
            
        }
        else
        	mNFC.setImageResource(R.drawable.nfc_on);
        */
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
            Log.d("Debug","Beam onResume1");
        }
        else
        	InfoUpdate();
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
    	Log.d("Debug","Beam onNewIntent");
    	
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String text = new String(msg.getRecords()[0].getPayload());
        //mInfoText.setText(text);
        SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
       	String FBID = settings.getString("ID", "");
        if(text.startsWith("INFO")){
        	String[] part = text.split(" ");
        	if(part[2].equals(FBID)){
        		//mInfoText.setText("You have "+part[3]+" points.");
        		
        		title = part[4];
        		settings.edit().putString(title, part[3]).commit();
        		Log.d("debug","title = "+title);
        		
        	}
        	InfoUpdate();
        }
        
    }

    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     *
     * @param mimeType
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // If NFC is not available, we won't be needing this menu
        if (mNfcAdapter == null) {
            return super.onCreateOptionsMenu(menu);
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/



}   