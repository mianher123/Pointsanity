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
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;




public class ShopList extends Activity implements CreateNdefMessageCallback,
        OnNdefPushCompleteCallback, LocationListener{
    NfcAdapter mNfcAdapter;
    TextView mInfoText;
    ImageView mNFC;
    ImageView mWifi;
    ImageView mRefresh;
    ImageView mMap;
    private ListView mListView;
    private static final int MESSAGE_SENT = 1;
    private static final int REFRESH_SUCCESS = 2;
    String FBID;
    ArrayList<HashMap<String,Object>> mList = new ArrayList<HashMap<String,Object>>();   
    private LocationManager mgr;
    private String best;
    private double shopPose[][] = {{25.018009,121.540192}/*活大歇腳亭*/,{25.014461,121.533465}/*汀洲路50嵐*/,{25.016055,121.532414}/*橘菓子*/,{25.015773,121.53339}/*Starbucks*/,{25.021976,121.541179}/*後門全家*/,{25.015151,121.536362}/*小小福阿水*/,{25.014723,121.534367}/*公館Coco*/,{25.017076,121.530848}/*三總旁OK*/,{25.017173,121.544849}/*長興小七*/};
    Location location;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shoplist);
        
        
        
        mListView = (ListView) findViewById(R.id.list);
        /*mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
              // When clicked, show a toast with the TextView text
            	Toast.makeText(getApplicationContext(),
            			"Click ListItem Number " + position, Toast.LENGTH_LONG)
            			.show();
            }
          });
          */
        
        mListView.setOnItemClickListener(new OnItemClickListener() {  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            	HashMap<String, Object> map = (HashMap<String, Object>) mListView.getItemAtPosition(arg2);
            	//current_map=map2;
            	//current_title=""+map2.get("goods");
            	Toast.makeText(getApplicationContext(), "選取了 " + map.get("abbr"), Toast.LENGTH_SHORT).show();
            	Bundle bundle = new Bundle();
            	bundle.putString("abbr", ""+map.get("abbr"));
            	Intent intent = new Intent();
            	//設定下一個Actitity
            	intent.setClass(ShopList.this, PointGrid.class);
            	intent.putExtras(bundle);
            	//開啟Activity
            	startActivity(intent);
            	//setTitle("選取了 " + map2.get("shopname"));     	
            	}  
            });
        
        
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
		    	intent.setClass(ShopList.this,Map.class);
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


    /**
     * Implementation for the CreateNdefMessageCallback interface
     */
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
        		updateDistanceInfo();
        		Toast.makeText(getApplicationContext(), "已更新距離與點數", Toast.LENGTH_SHORT).show();
                
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
        SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
        settings.edit().putString("SHOP", "").commit();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
            Log.d("Debug","Beam onResume1");
        }
        
        
        //below is location operations
        LocationManager status = (LocationManager)(this.getSystemService(Context.LOCATION_SERVICE));
		
		if(status.isProviderEnabled(LocationManager.GPS_PROVIDER)||status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
		    best = mgr.getBestProvider(criteria, true);
//		    mgr.requestLocationUpdates("gps", 60000, 1, this);
//		    Location location = mgr.getLastKnownLocation("gps");
		    mgr.requestLocationUpdates(best, 60000, 1, this);
		    location = mgr.getLastKnownLocation(best);
	        
		} else {
			new AlertDialog.Builder(ShopList.this)
	       	.setTitle("No Location Service")
	       	.setMessage("Please enable location service before use this app")
	       	.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			   	public void onClick(DialogInterface dialog, int whichButton) {  
			   	    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			   	}
	       	})
	       	.show();
		}
		updateDistanceInfo();
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
    
    private void updateDistanceInfo(){
    	
    	
    	Location shop = new Location("reverseGeocoded");
    	SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
    	String uri;
    	int imageResource;
    	int num_points=0;
        int Dist;
        mList.clear();
    	HashMap<String,Object> item = new HashMap<String,Object>();
    	item.put("icon", R.drawable.hand);
        item.put("shopname", "店名:歇手停");
        item.put("abbr", "hand");
        shop.setLatitude(shopPose[0][0]);
		shop.setLongitude(shopPose[0][1]);
		Dist = (int)location.distanceTo(shop);
		item.put("distance", (location == null ?"距離:未知":"距離:約"+getDistString(Dist) +"公里"));
		
		num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString("hand", ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString("hand", ""+0).commit();
		}
		if(num_points<10){
			uri = "drawable/empty";
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		else{
			
			uri = "drawable/n_"+num_points/10;
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		uri = "drawable/n_"+num_points%10;
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		item.put("numview1", imageResource);
		mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.land);
        item.put("shopname", "店名:69嵐");
        item.put("abbr", "land");
        shop.setLatitude(shopPose[1][0]);
		shop.setLongitude(shopPose[1][1]);
		Dist = (int)location.distanceTo(shop);
        item.put("distance", (location == null ?"距離:未知":"距離:約"+getDistString(Dist)+"公里"));
        num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString("land", ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString("land", ""+0).commit();
		}
		if(num_points<10){
			uri = "drawable/empty";
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		else{
			
			uri = "drawable/n_"+num_points/10;
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		uri = "drawable/n_"+num_points%10;
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		item.put("numview1", imageResource);
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.apple);
        item.put("shopname", "店名:小蘋果");
        item.put("abbr", "apple");
        shop.setLatitude(shopPose[2][0]);
		shop.setLongitude(shopPose[2][1]);
		Dist = (int)location.distanceTo(shop);
        item.put("distance", (location == null ?"距離:未知":"距離:約"+getDistString(Dist) +"公里"));
        num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString("apple", ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString("apple", ""+0).commit();
		}
		if(num_points<10){
			uri = "drawable/empty";
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		else{
			
			uri = "drawable/n_"+num_points/10;
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		uri = "drawable/n_"+num_points%10;
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		item.put("numview1", imageResource);
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.coffee);
        item.put("shopname", "店名:老咖啡");
        item.put("abbr", "coffee");
        shop.setLatitude(shopPose[3][0]);
		shop.setLongitude(shopPose[3][1]);
		Dist = (int)location.distanceTo(shop);
        item.put("distance", (location == null ?"距離:未知":"距離:約"+getDistString(Dist) +"公里"));
        num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString("coffee", ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString("coffee", ""+0).commit();
		}
		if(num_points<10){
			uri = "drawable/empty";
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		else{
			
			uri = "drawable/n_"+num_points/10;
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		uri = "drawable/n_"+num_points%10;
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		item.put("numview1", imageResource);
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.family);
        item.put("shopname", "店名:親家便利商店");
        item.put("abbr", "family");
        shop.setLatitude(shopPose[4][0]);
		shop.setLongitude(shopPose[4][1]);
		Dist = (int)location.distanceTo(shop);
        item.put("distance", (location == null ?"距離:未知":"距離:約"+getDistString(Dist) +"公里"));
        num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString("family", ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString("family", ""+0).commit();
		}
		if(num_points<10){
			uri = "drawable/empty";
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		else{
			
			uri = "drawable/n_"+num_points/10;
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		uri = "drawable/n_"+num_points%10;
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		item.put("numview1", imageResource);
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.fire);
        item.put("shopname", "店名:阿火茶舖");
        item.put("abbr", "fire");
        shop.setLatitude(shopPose[5][0]);
		shop.setLongitude(shopPose[5][1]);
		Dist = (int)location.distanceTo(shop);
        item.put("distance", (location == null ?"距離:未知":"距離:約"+getDistString(Dist) +"公里"));
        num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString("fire", ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString("fire", ""+0).commit();
		}
		if(num_points<10){
			uri = "drawable/empty";
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		else{
			
			uri = "drawable/n_"+num_points/10;
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		uri = "drawable/n_"+num_points%10;
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		item.put("numview1", imageResource);
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.kaka);
        item.put("shopname", "店名:KaKa真假");
        item.put("abbr", "kaka");
        shop.setLatitude(shopPose[6][0]);
		shop.setLongitude(shopPose[6][1]);
		Dist = (int)location.distanceTo(shop);
        item.put("distance", (location == null ?"距離:未知":"距離:約"+getDistString(Dist)+"公里"));
        num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString("kaka", ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString("kaka", ""+0).commit();
		}
		if(num_points<10){
			uri = "drawable/empty";
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		else{
			
			uri = "drawable/n_"+num_points/10;
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		uri = "drawable/n_"+num_points%10;
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		item.put("numview1", imageResource);
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.ko);
        item.put("shopname", "店名:KO便利商店");
        item.put("abbr", "ko");
        shop.setLatitude(shopPose[7][0]);
		shop.setLongitude(shopPose[7][1]);
		Dist = (int)location.distanceTo(shop);
        item.put("distance", (location == null ?"距離:未知":"距離:約"+getDistString(Dist)+"公里"));
        num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString("ko", ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString("ko", ""+0).commit();
		}
		if(num_points<10){
			uri = "drawable/empty";
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		else{
			
			uri = "drawable/n_"+num_points/10;
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		uri = "drawable/n_"+num_points%10;
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		item.put("numview1", imageResource);
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.one);
        item.put("shopname", "店名:ONE便利商店");
        item.put("abbr", "one");
        shop.setLatitude(shopPose[8][0]);
		shop.setLongitude(shopPose[8][1]);
		Dist = (int)location.distanceTo(shop);
        item.put("distance", (location == null ?"距離:未知":"距離:約"+getDistString(Dist) +"公里"));
        num_points=0;
		try{
			num_points = Integer.parseInt(settings.getString("one", ""));
		}
		catch(Exception e){
			e.printStackTrace();
			settings.edit().putString("one", ""+0).commit();
		}
		if(num_points<10){
			uri = "drawable/empty";
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		else{
			
			uri = "drawable/n_"+num_points/10;
			imageResource = getResources().getIdentifier(uri, null, getPackageName());
			item.put("numview2", imageResource);
			
		}
		uri = "drawable/n_"+num_points%10;
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		item.put("numview1", imageResource);
        mList.add(item);
        
        Collections.sort(mList, new Comparator(){
        	 
            public int compare(Object o1, Object o2) {
            	HashMap<String,Object> p1 = (HashMap<String,Object>) o1;
            	HashMap<String,Object> p2 = (HashMap<String,Object>) o2;
            	SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
            	
               return Integer.parseInt(settings.getString((String) p2.get("abbr"), "")) > Integer.parseInt(settings.getString((String) p1.get("abbr"), ""))?1:-1;
            }
 
        });
        
       	SimpleAdapter sAdapter;
        sAdapter = new SimpleAdapter(this, mList, R.layout.listitem, 
        		   new String[] {"icon","shopname","distance","numview2","numview1","abbr"}, 
        		   new int[] {R.id.ItemImage,R.id.ItemTitle, R.id.ItemDist,R.id.numberView2,R.id.numberView1, 0}  );

        mListView.setAdapter(sAdapter);
        mListView.setTextFilterEnabled(true);
    	
    	
    }
    
    private String getDistString(int dist) {
    	double x = dist*0.001;
    	BigDecimal a = new BigDecimal(""+x);
    	BigDecimal result = a.setScale(2, RoundingMode.DOWN);
    	
		return ""+result;
	}


	@Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
    	Log.d("Debug","Beam onNewIntent");
    	mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            mInfoText = (TextView) findViewById(R.id.textView1);
            mInfoText.setText("NFC is not available on this device.");
            mNFC.setImageResource(R.drawable.nfc_off);
            
        }
        else
        	mNFC.setImageResource(R.drawable.nfc_on);
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
        	if(part[1].equals(FBID)){
        		mInfoText.setText("You have "+part[2]+" points.");
        		settings.edit().putString("NUMPOINTS", part[2]).commit();
        	}
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

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		//updateDistanceInfo();
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
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
