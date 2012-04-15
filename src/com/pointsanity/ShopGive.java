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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;



public class ShopGive extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {
    NfcAdapter mNfcAdapter;
    TextView mInfoText;
    String ShopId;
    String customerID;
    String serverResult;
    private static final int MESSAGE_SENT = 1;
    private static final int GET_ID = 2;
    private static final int GET_INFO = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shopgive);
        serverResult="";
        mInfoText = (TextView) findViewById(R.id.textView1);
        ShopId = "5566";
        //mInfoText = (TextView) findViewById(R.id.textView1);
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            mInfoText = (TextView) findViewById(R.id.textView1);
            mInfoText.setText("NFC is not available on this device.");
        }
        // Register callback to set NDEF message
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        // Register callback to listen for message-sent success
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
    }


    /**
     * Implementation for the CreateNdefMessageCallback interface
     */
    public NdefMessage createNdefMessage(NfcEvent event) {
        //Time time = new Time();
        //time.setToNow();
        //String text = ("Beam me up!\n\n" + "Beam Time: " + time.format("%H:%M:%S"));
    	
    	String text = serverResult;
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
            
            case GET_ID:
            	Toast.makeText(getApplicationContext(), "GET ID: "+customerID, Toast.LENGTH_LONG).show();
                break;
            
        	case GET_INFO:
        		Toast.makeText(getApplicationContext(), "GET INFO: "+serverResult, Toast.LENGTH_LONG).show();
        		break;
            }
       
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Debug","ShopGive onResume");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
        settings.edit().putString("SHOP", "true").commit();
        Log.d("Debug","ShopGive onResume1");
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
        	Log.d("Debug","ShopGive onResume2");
            processIntent(getIntent());
            Log.d("Debug","ShopGive onResume3");
        }
        else{
        	Log.d("Debug","ShopGive onResume4");
        	
        }
    }
    
    
    @Override
    public void onNewIntent(Intent intent) {
    	Log.d("Debug","ShopGive onNewIntent");
    	//mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // onResume gets called after this to handle the intent
    	SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
        settings.edit().putString("SHOP", "true").commit();
        // Check to see that the Activity started due to an Android Beam
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
        //mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
        String text = new String(msg.getRecords()[0].getPayload());
        mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
        if(text.startsWith("ID")){
        	String[] part = text.split(" ");
        	customerID=part[1];        	
        	mHandler.obtainMessage(GET_ID).sendToTarget();
        	genDialog();
            //time.format("%H:%M:%S")
            /*Runnable ConnectRun = new Runnable(){  
  	       	  
           		public void run() {  
           			Time time = new Time();
                    time.setToNow();
                    updateToServer("REC "+customerID+" "+mPoints.getText()+" "+time.format("%Y:%H:%M:%S"));  
           		}  
           		  };  	
    		new Thread(ConnectRun).start(); */
        	
        }
        
        
        
    }
    public void genDialog(){
   		LayoutInflater inflater = LayoutInflater.from(this);  
        final View textEntryView = inflater.inflate(R.layout.addpointdialog, null);  
        final Button mMinus = (Button) textEntryView.findViewById(R.id.button1);
        final Button mPlus = (Button) textEntryView.findViewById(R.id.button2);
        final EditText mPoints = (EditText) textEntryView.findViewById(R.id.editText1);
        mMinus.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		Log.d("Debug","In mMinus");
        		int num=Integer.parseInt(mPoints.getText().toString());
        		if(num>0)
        			mPoints.setText(Integer.toString(num-1));
        		
        	};
       	});
        mPlus.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		Log.d("Debug","In mPlus");
        		int num=Integer.parseInt(mPoints.getText().toString());
        		mPoints.setText(Integer.toString(num+1));
        		
        	};
       	});
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShopGive.this);  
        builder.setCancelable(false);  
        //builder.setIcon(R.drawable.icon);  
        builder.setTitle("請選擇欲給予的點數數量");  
        builder.setView(textEntryView);  
        builder.setPositiveButton("上傳",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	Runnable ConnectRun = new Runnable(){  
                	       	  
                       		public void run() {  
                       			
                                serverResult = updateToServer("REC "+customerID+" "+ShopId+" "+mPoints.getText());
                                mHandler.obtainMessage(GET_INFO).sendToTarget();
                       		}  
                       		  };  	
                		new Thread(ConnectRun).start(); 
                    	
                    	//setTitle(edtInput.getText());  
                    }  
                });  
        builder.setNegativeButton("取消",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        //setTitle("");  
                    }  
                });  
        builder.show(); 

   		
   		
   		
   	}
    private String updateToServer(String s){
		String address = "122.116.119.134";// 連線的ip
	    int port = 5566;// 連線的port
	    Socket client = new Socket();
	       
        InetSocketAddress isa = new InetSocketAddress(address, port);
        /*try {
			server=new ServerSocket(7788);
		} catch (IOException e1) {
			System.out.println("serverSocket建立有問題 !");
            System.out.println("IOException :" + e1.toString());
		}*/
        
        try {
            client.connect(isa, 15000);
            BufferedOutputStream out = new BufferedOutputStream(client
                    .getOutputStream());
            Log.d("Debug","已得到out");
            BufferedInputStream in = new BufferedInputStream(client
                    .getInputStream());
            Log.d("Debug","已得到in");
            // 送出字串
            out.write(s.getBytes());
            out.flush();
            /*out.close();
            out = null;*/
            Log.d("Debug","已送出字串");
            
            byte[] b = new byte[1024];
            String data = "";
            int length;
            //while ((length = in.read(b)) > 0)// <=0的話就是結束了
            length = in.read(b);
            data += new String(b, 0, length);
            
            Log.d("Debug","我取得的值:" + data);
            //System.out.println("我取得的值:" + data);
            in.close();
            in = null;
            Log.d("Debug","已讀取完畢");
            /*synchronized (server) {
                socket = server.accept();
            }*/
           // System.out.println("取得連線 : InetAddress = " + socket.getInetAddress());
            //Log.d("Debug","取得連線 : InetAddress = " + socket.getInetAddress());
            // TimeOut時間
            //socket.setSoTimeout(15000);

            /*in = new java.io.BufferedInputStream(socket.getInputStream());
            byte[] b = new byte[1024];
            String data = "";
            int length;
            while ((length = in.read(b)) > 0)// <=0的話就是結束了
            {
                data += new String(b, 0, length);
            }
            Log.d("Debug","我取得的值:" + data);
            //System.out.println("我取得的值:" + data);
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
