package com.pointsanity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PointsanityActivity extends Activity {
    /** Called when the activity is first created. */
	private ImageView mPickBtn;
	private ImageView mShareBtn;
	private ImageView mOrderBtn;
	private Button mConnect;
	private Button mShop;
	private Facebook mFacebook;
	private TextView mFBID;
	//private TextView mTitle;
	private TextView mEnter;
	
	
	/*private ListView mListView;
	private ArrayList<String> list;
	private ArrayList<String> IDs;
	private String s;
	private Bundle bundle;
	String now_id;
	int now_position;*/
	//private EditText mEditText;
	//private ImageView mImage;
    private AsyncFacebookRunner mAsyncRunner;
    
    public static final String APP_ID = "321725801219299";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //PointsanityActivity.this.getWindow().setBackgroundDrawableResource(R.drawable.mainpage);
        setContentView(R.layout.main);
        Log.d("DebugLog","setContentView");
        
        
        
        
        
        
        
        
        mPickBtn = (ImageView) findViewById(R.id.imageView1);
        mOrderBtn = (ImageView) findViewById(R.id.imageView2);
        mShareBtn = (ImageView) findViewById(R.id.imageView3);
        mConnect = (Button) findViewById(R.id.button3);
        mShop = (Button) findViewById(R.id.button1);
        mFBID = (TextView) findViewById(R.id.fbID);
        //mTitle = (TextView) findViewById(R.id.title);
        mEnter = (TextView) findViewById(R.id.enter);
        
        mPickBtn.setVisibility(View.INVISIBLE);
        mOrderBtn.setVisibility(View.INVISIBLE);
        mShareBtn.setVisibility(View.INVISIBLE);
        mFBID.setVisibility(View.INVISIBLE);
        //mTitle.setVisibility(View.INVISIBLE);
        
        mFacebook = new Facebook(APP_ID);
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
       	SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
       	String FBID = settings.getString("ID", "");
        String FBNAME = settings.getString("NAME", "");
       	if((! "".equals(FBID))&&(! "".equals(FBNAME))){
       		mFBID.setText("Hello, "+FBNAME);
			mPickBtn.setVisibility(View.VISIBLE);
	        mShareBtn.setVisibility(View.VISIBLE);
	        mOrderBtn.setVisibility(View.VISIBLE);
	        mFBID.setVisibility(View.VISIBLE);
	        mEnter.setVisibility(View.INVISIBLE);
       		
       		
       	}
       	
       	mEnter.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		mFacebook.authorize(PointsanityActivity.this,new String[]{"read_friendlists","publish_stream","read_stream"},Facebook.FORCE_DIALOG_AUTH,
        				new DialogListener(){

        					public void onComplete(Bundle values) {
        						
        						try {
        							Log.d("DebugLog","onComplete try");
        							mAsyncRunner.request("me",requestListener);
        							
        							
        						} catch (Exception e) {
        							// TODO Auto-generated catch block
        							Log.d("DebugLog","onComplete catch");
        							e.printStackTrace();
        							
        						} 
        				       	
        						
        					}

        					public void onFacebookError(FacebookError e) {
        						// TODO Auto-generated method stub
        						Log.d("DebugLog","onFacebookError");
        						
        					}

        					public void onError(DialogError e) {
        						// TODO Auto-generated method stub
        						Log.d("DebugLog","onError");
        					}

        					public void onCancel() {
        						// TODO Auto-generated method stub
        						Log.d("DebugLog","onCancel");
        					}
        					
        			
        			
        		});
					
			};//end onClick
			
        	
        	
        	
        });//end OnClickListener
       	
       	mConnect.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		Log.d("Debug","In mConnect");
        		Runnable ConnectRun = new Runnable(){  
        	       	  
               		public void run() {  
               		    // TODO Auto-generated method stub  
               			SocketClient("草尼瑪");  
               		}  
               		  };  	
        		new Thread(ConnectRun).start(); 
        		
        	};
       	});
       	
       	mShop.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		Log.d("Debug","In mShop");
        		Intent intent = new Intent();
		    	intent.setClass(PointsanityActivity.this,ShopGive.class);
		    	startActivity(intent);
        		
        	};
       	});
       	mShareBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		Log.d("Debug","In mShare");
        		Intent intent = new Intent();
		    	intent.setClass(PointsanityActivity.this,GridTest.class);
		    	startActivity(intent);
        		
        	};
       	});
       	
       	mPickBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		Log.d("Debug","In mShareBtn");
        		Intent intent = new Intent();
		    	intent.setClass(PointsanityActivity.this,Beam.class);
		    	startActivity(intent);
        		
        	};
       	});
       	
        		
       	
    }
    @Override  
    protected void onNewIntent(Intent intent) {  
        // TODO Auto-generated method stub  
        super.onNewIntent(intent);  
        Log.d("Debug","PointsanityActivity onNewIntent");
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
        	Log.d("Debug","PointsanityActivity onNewIntent1");
        	SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
           	String shop = settings.getString("SHOP", "");
           	Intent mintent = shop.equals("true") ? intent.setClass(PointsanityActivity.this,ShopGive.class) : intent.setClass(PointsanityActivity.this,Beam.class);
	    	startActivity(mintent);
	    	getIntent().setAction("");
        }  
    }  
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        Log.d("Debug","PointsanityActivity onResume");
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
        	Log.d("Debug","PointsanityActivity onResume1");
        	SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
           	String shop = settings.getString("SHOP", "");
           	Intent intent = shop.equals("true") ? getIntent().setClass(PointsanityActivity.this,ShopGive.class) : getIntent().setClass(PointsanityActivity.this,Beam.class);
	    	startActivity(intent);
	    	getIntent().setAction("");
        }
        
    }
    private RequestListener requestListener = new RequestListener(){
		public void onComplete(String response,Object state){
			Log.d("DebugLog","In requestListener");
			JSONObject jObject;
			final String id;
			final String name;
			try{
				jObject=new JSONObject(response);
				id =jObject.getString("id");
				name =jObject.getString("name");
				
				PointsanityActivity.this.runOnUiThread(new Runnable(){
					public void run(){
						mFBID.setText("Hello, "+name);
						mPickBtn.setVisibility(View.VISIBLE);
				        mShareBtn.setVisibility(View.VISIBLE);
				        mOrderBtn.setVisibility(View.VISIBLE);
				        mFBID.setVisibility(View.VISIBLE);
				        //mTitle.setVisibility(View.VISIBLE);
				        mEnter.setVisibility(View.INVISIBLE);
				        Log.d("DebugLog","SetVisibility done");
				        SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
		                settings.edit().putString("ID", id).commit();
		                settings.edit().putString("NAME", name).commit();
					}					
					
				});
				
			}
			catch(JSONException e){
				e.printStackTrace();
				Log.d("DebugLog","requestListener catch");
			}
			
		}

		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			Log.d("DebugLog","onIOException");
		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			Log.d("DebugLog","onFileNotFoundException");
		}

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			Log.d("DebugLog","onMalformedURLException");
		}

		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			Log.d("DebugLog","onFacebookError");
		}
		
		
		
	};
	
	
	private void SocketClient(String s){
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
            out.write(("Send From Client:"+s).getBytes());
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
            
        } catch (java.io.IOException e) {
            System.out.println("Socket連線有問題 !");
            System.out.println("IOException :" + e.toString());
        }
    
		
		
	}
}