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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;



public class ShopList extends Activity implements CreateNdefMessageCallback,
        OnNdefPushCompleteCallback {
    NfcAdapter mNfcAdapter;
    TextView mInfoText;
    ImageView mNFC;
    ImageView mWifi;
    private ListView mListView;
    private static final int MESSAGE_SENT = 1;
    String FBID;
    ArrayList<HashMap<String,Object>> mList = new ArrayList<HashMap<String,Object>>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shoplist);
        
        
        
        mListView = (ListView) findViewById(R.id.listView1);
       	mNFC = (ImageView) findViewById(R.id.nfcView1);
        mWifi = (ImageView) findViewById(R.id.wifiView1);
        if(checkWifiStatus()){
        	mWifi.setImageResource(R.drawable.wifi_on);
        	
        }
        else
        	mWifi.setImageResource(R.drawable.wifi_off);
       	
       	HashMap<String,Object> item = new HashMap<String,Object>();
        item.put("icon", R.drawable.hand);
        item.put("shopname", "店名:歇手停");
        item.put("distance", "距離:3公里");
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.land);
        item.put("shopname", "店名:69嵐");
        item.put("distance", "距離:5公里");
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.apple);
        item.put("shopname", "店名:小蘋果");
        item.put("distance", "距離:5公里");
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.coffee);
        item.put("shopname", "店名:老咖啡");
        item.put("distance", "距離:5公里");
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.family);
        item.put("shopname", "店名:親家便利商店");
        item.put("distance", "距離:5公里");
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.fire);
        item.put("shopname", "店名:阿火茶鋪");
        item.put("distance", "距離:5公里");
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.kaka);
        item.put("shopname", "店名:KaKa真假");
        item.put("distance", "距離:5公里");
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.ko);
        item.put("shopname", "店名:KO便利商店");
        item.put("distance", "距離:5公里");
        mList.add(item);
        
        item = new HashMap<String,Object>();
        item.put("icon", R.drawable.one);
        item.put("shopname", "ONE便利商店");
        item.put("distance", "距離:5公里");
        mList.add(item);
        
       	SimpleAdapter sAdapter;
        sAdapter = new SimpleAdapter(this, mList, R.layout.listitem, 
        		   new String[] {"icon","shopname","distance"}, 
        		   new int[] {R.id.ItemImage,R.id.ItemTitle, R.id.ItemDist}  );

        mListView.setAdapter(sAdapter);
        mListView.setTextFilterEnabled(true);
       	
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            
            mNFC.setImageResource(R.drawable.nfc_off);
            
        }
        else
        	mNFC.setImageResource(R.drawable.nfc_on);
        mNFC.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        		
        	};
       	});
        mWifi.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
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
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Debug","Beam onResume");
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