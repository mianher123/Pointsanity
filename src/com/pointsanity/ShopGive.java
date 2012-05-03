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
import android.app.AlertDialog.Builder;
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
import android.widget.ImageView;
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
    ImageView mSignBoard;
    String ShopId;
    String customerID;
    String serverResult;
    String ShopName;
    int session = 0;
    private static final int MESSAGE_SENT = 1;
    private static final int GET_ID = 2;
    private static final int INFO_SUCCESS = 3;
    private static final int INFO_FAILED = 4;
    private static final int LOGIN_SENT = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shopgive);
        serverResult="";
        mInfoText = (TextView) findViewById(R.id.textView1);
        mSignBoard = (ImageView) findViewById(R.id.signboard);
        //ShopId = "5566";
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
        /*Bundle bundle = getIntent().getExtras();
		ShopName = bundle.getString("shopname");
		ShopId = bundle.getString("shopid");
		*/
		
    }


    /**
     * Implementation for the CreateNdefMessageCallback interface
     */
    public NdefMessage createNdefMessage(NfcEvent event) {
        //Time time = new Time();
        //time.setToNow();
        //String text = ("Beam me up!\n\n" + "Beam Time: " + time.format("%H:%M:%S"));
    	
    	String text = serverResult+" "+ShopName;
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
    public void genLoginDialog(){
   		LayoutInflater inflater = LayoutInflater.from(this);  
        final View textEntryView = inflater.inflate(R.layout.logindialog, null);  
        final EditText editInput1=(EditText)textEntryView.findViewById(R.id.editInput1); 
        final EditText editInput2=(EditText)textEntryView.findViewById(R.id.editInput2);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShopGive.this);  
        builder.setCancelable(false);  
        //builder.setIcon(R.drawable.icon);  
        builder.setTitle("店家登入");  
        builder.setView(textEntryView);  
        builder.setPositiveButton("確定",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	Runnable ConnectRun = new Runnable(){  
              	       	  
                       		public void run() {  
                       			if(editInput1.getText().length()>0 && editInput2.getText().length()>0){
                       				
                       			serverResult = updateToServer("VERIFY "+editInput1.getText()+" "+editInput2.getText());
                                mHandler.obtainMessage(LOGIN_SENT).sendToTarget();
                                ShopId = ""+editInput1.getText();
                                SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);            
                            	settings.edit().putString("SHOPID", ShopId).commit();
                       			}  
                       			}
                       		  };  	
                		new Thread(ConnectRun).start(); 
                    	
                    	
                    	
  
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
            
        	case INFO_SUCCESS:
        		Toast.makeText(getApplicationContext(), "GET INFO: "+serverResult, Toast.LENGTH_LONG).show();
        		break;
        	case INFO_FAILED:
        		Toast.makeText(getApplicationContext(), "The user doesn't have enough points.", Toast.LENGTH_LONG).show();
        		break;
        	case LOGIN_SENT:
            	if(serverResult.startsWith("VERIFYRESULT")){
            		String[] part = serverResult.split(" ");
            		if(part[1].equals("ERROR")){
            			Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_SHORT).show();
            			genLoginDialog();
            		}
            		else{ 
            			ShopName = part[1].toLowerCase();
            			
                    	Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();
                    	SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);            
                    	settings.edit().putString("SHOP", "true").commit();
                    	settings.edit().putString("SHOPNAME", ShopName).commit();
                    	String uri = "drawable/title_"+ShopName;
                		int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                		mSignBoard.setImageDrawable(getResources().getDrawable(imageResource));
            		}
            		
            	}
                break;
            }
       
        }
    };
    
    @Override
    public void onStop() {
    	super.onStop();
    	
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("Debug","ShopGive onResume");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        
        //Log.d("Debug","ShopGive onResume1");
        SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);         
        if(settings.getString("SHOP", "").equals("true")){
        	   
        	ShopId = settings.getString("SHOPID", "");
        	ShopName = settings.getString("SHOPNAME", "");
        	String uri = "drawable/title_"+ShopName;
    		int imageResource = getResources().getIdentifier(uri, null, getPackageName());
    		mSignBoard.setImageDrawable(getResources().getDrawable(imageResource));
        }
        else
        	genLoginDialog();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
        	Log.d("Debug","ShopGive onResume2");
            processIntent(getIntent());
            Log.d("Debug","ShopGive onResume3");
            getIntent().setAction("");
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
    	//SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
    	//settings.edit().putString("SHOP", "true").commit();
        //settings.edit().putString("SHOP", "true").commit();
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
        mInfoText.setText("使用者"+new String(msg.getRecords()[0].getPayload()));
        if(text.startsWith("ID")){
        	String[] part = text.split(" ");
        	customerID=part[1];        	
        	mHandler.obtainMessage(GET_ID).sendToTarget();
        	AlertDialog alertDialog = getAlertDialog("","請選擇集點或是兌換");
        	alertDialog.show();
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
    public void genDialog(final int pick_or_convert){
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
        if(pick_or_convert == 0)
        	builder.setTitle("請選擇欲給予的點數數量");  
        else
        	builder.setTitle("請選擇兌換的次數");  
        builder.setView(textEntryView);  
        builder.setPositiveButton("上傳",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	Runnable ConnectRun = new Runnable(){  
                	       	  
                       		public void run() {  
                       			if(pick_or_convert == 0)
                       				serverResult = updateToServer("REC "+customerID+" "+ShopId+" "+mPoints.getText());
                       			else
                       				serverResult = updateToServer("REC "+customerID+" "+ShopId+" "+(-10)*Integer.parseInt(""+mPoints.getText()));
                       			if(serverResult.startsWith("INFO")){
                            		String[] part = serverResult.split(" ");
                            		if(part[1].equals("failed")){
                            			mHandler.obtainMessage(INFO_FAILED).sendToTarget();
                            		}
                            		else
                            			mHandler.obtainMessage(INFO_SUCCESS).sendToTarget();
                       			}
                                
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
    
    private AlertDialog getAlertDialog(String title,String message){
        //���ͤ@��Builder����
        Builder builder = new AlertDialog.Builder(ShopGive.this);
        //�]�wDialog�����D
        builder.setTitle(title);
        //�]�wDialog�����e
        builder.setMessage(message);
        //�]�wPositive���s���
        builder.setPositiveButton("集點", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //���U���s����ܧ���
                //Toast.makeText(ShopGive.this, "�z���UOK���s", Toast.LENGTH_SHORT).show();
            	genDialog(0);
            }
        });
        //�]�wNegative���s���
        builder.setNegativeButton("兌換", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	genDialog(1);
                //���U���s����ܧ���
                //Toast.makeText(ShopGive.this, "�z���UCancel���s", Toast.LENGTH_SHORT).show();
            }
        });
        //�Q��Builder����إ�AlertDialog
        return builder.create();
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