package com.pointsanity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;


public class Exchange extends Activity{
	
	private ListView mListView;
	ArrayList<HashMap<String,Object>> mList;
	
	private ArrayList<String> Names;
	private ArrayList<String> Urls;
	private ArrayList<String> IDs;
	private ArrayList<Bitmap> Photos;
	private Facebook mFacebook;
	LazyAdapter adapter;
	int now_position;
	//private EditText mEditText;
	//private ImageView mImage;
    public static AsyncFacebookRunner mAsyncRunner;
    public static final String APP_ID = "321725801219299";
    //public static final String APP_ID = "304201572932743";
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange);
        Log.d("DebugLog","setContentView");
        
    	
        //list=new ArrayList<String>();
        mList = new ArrayList<HashMap<String,Object>>();
        IDs=new ArrayList<String>();
        Names=new ArrayList<String>();
        Urls=new ArrayList<String>();
        Photos=new ArrayList<Bitmap>();
        SharedPreferences settings = getSharedPreferences("POINTSANITY_PREF", 0);
       	String FBID = settings.getString("ID", "");
       	Log.d("debug",FBID);
        mListView=(ListView) findViewById(R.id.exchangeList);
        mFacebook = new Facebook(APP_ID);
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
       	mFacebook.authorize(Exchange.this,new String[]{"read_friendlists","publish_stream","read_stream"},Facebook.FORCE_DIALOG_AUTH,
				new DialogListener(){

					public void onComplete(Bundle values) {
						
						try {
							Log.d("DebugLog","onComplete try");
							mAsyncRunner.request("me/friends",friendRequestListener);
							
							
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
        
        
        /*
        mLoginButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
				if(mLoginButton.getText().equals("Login"))
					mFacebook.authorize(MyFBActivity.this,new String[]{"read_friendlists","publish_stream","read_stream"},
						new DialogListener(){

							public void onComplete(Bundle values) {
								mAsyncRunner.request("me/friends",friendRequestListener);
								Log.d("DebugLog","onComplete");
								
							}

							public void onFacebookError(FacebookError e) {
								// TODO Auto-generated method stub
								
							}

							public void onError(DialogError e) {
								// TODO Auto-generated method stub
								
							}

							public void onCancel() {
								// TODO Auto-generated method stub
								
							}
							
					
					
				});//end authorize, end if
				//Log.d("DebugLog","end authorize");
				else{
					mAsyncRunner.logout(MyFBActivity.this,LogoutListener);
					
					
				}
					
			};//end onClick
			
        	
        	
        	
        });//end OnClickListener
        */
        //mEditText = (EditText) findViewById(R.id.editText1);
        //mImage = (ImageView) findViewById(R.id.imageView1);
        //mEditText.setHint("Send message to him/her");
        
        
        mListView.setOnItemClickListener(new OnItemClickListener() {  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        		genDialog();
            	
            	/*AlertDialog.Builder adb=new AlertDialog.Builder(MyFBActivity.this);
        		adb.setTitle("LVSelectedItemExample");
        		adb.setMessage("Selected Item is = "+mListView.getItemAtPosition(position)+" at "+position);
        		adb.setPositiveButton("Ok", null);
        		adb.show();*/
        		//now_position=arg2;
        		
        		//mAsyncRunner.request(IDs.get(now_position)+"/feed",infoRequestListener);
        		
        		
        	}
        });

    }//end onCreate
	public void genDialog(){
   		LayoutInflater inflater = LayoutInflater.from(this);  
        final View textEntryView = inflater.inflate(R.layout.exchangedialog, null);  
        final Button mMinus = (Button) textEntryView.findViewById(R.id.button1);
        final Button mPlus = (Button) textEntryView.findViewById(R.id.button2);
        final EditText mPoints = (EditText) textEntryView.findViewById(R.id.editText1);
        Spinner spinner = (Spinner) findViewById(R.id.shopSpinner);
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
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(Exchange.this,android.R.layout.simple_spinner_item,new String[]{"紅茶","奶茶","綠茶"});
        //設定下拉選單的樣式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/
        final AlertDialog.Builder builder = new AlertDialog.Builder(Exchange.this);  
        builder.setCancelable(false);  
        //builder.setIcon(R.drawable.icon);  
        builder.setTitle("請選擇商家與點數");  
        builder.setView(textEntryView);  
        String[] province = new String[] { "歇手停", "69嵐", "小蘋果", "老咖啡", "親家便利商店","阿火茶舖", "KaKa真假", "KO便利商店", "ONE便利商店" };
        builder.setSingleChoiceItems(province, 0, null); 
        builder.setPositiveButton("上傳",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	Runnable ConnectRun = new Runnable(){  
                	       	  
                       		public void run() {  
                       			/*if(pick_or_convert == 0)
                       				serverResult = updateToServer("REC "+customerID+" "+ShopId+" "+mPoints.getText());
                       			else
                       				serverResult = updateToServer("REC "+customerID+" "+ShopId+" "+(-10)*Integer.parseInt(""+mPoints.getText()));
                                mHandler.obtainMessage(GET_INFO).sendToTarget();*/
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

	private RequestListener friendRequestListener = new RequestListener(){
		public void onComplete(String response,Object state){
			Log.d("Debug_Log","In friendRequestListener");
			JSONObject friend;
			JSONArray friendlist;
			try{
				friend=new JSONObject(response);
				friendlist=friend.getJSONArray("data");
				String s=friendlist.getJSONObject(0).getString("name");
				for(int i=0;i<10/*friendlist.length()*/;i++){
					/*HashMap<String,Object> item = new HashMap<String,Object>();
			    	item.put("friendname", friendlist.getJSONObject(i).getString("name"));
			    	URL img_value = null;
			    	img_value = new URL("http://graph.facebook.com/"+friendlist.getJSONObject(i).getString("id")+"/picture");
			    	Bitmap mIcon = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
			    	item.put("friendphoto", mIcon);
			    	mList.add(item);
					//list.add(friendlist.getJSONObject(i).getString("name"));
					IDs.add(friendlist.getJSONObject(i).getString("id"));
					Log.d("friend_name",friendlist.getJSONObject(i).getString("name"));
					*/
					Names.add(friendlist.getJSONObject(i).getString("name"));
					Urls.add("http://graph.facebook.com/"+friendlist.getJSONObject(i).getString("id")+"/picture");
					IDs.add(friendlist.getJSONObject(i).getString("id"));
					
					URL url = new URL("http://graph.facebook.com/"+friendlist.getJSONObject(i).getString("id")+"/picture");
					Photos.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
			        
				}
				adapter=new LazyAdapter(Exchange.this);
				Exchange.this.runOnUiThread(new Runnable(){
					public void run(){
						mListView.setAdapter(adapter);
				        mListView.setTextFilterEnabled(true);
						
					}					
					
				});
				/*Exchange.this.runOnUiThread(new Runnable(){
					public void run(){
						SimpleAdapter sAdapter;
				        sAdapter = new SimpleAdapter(Exchange.this, mList, R.layout.friendlistitem, 
				        		   new String[] {"friendphoto","friendname"}, 
				        		   new int[] {R.id.FriendPhoto,R.id.FriendName}  );

				        mListView.setAdapter(sAdapter);
				        mListView.setTextFilterEnabled(true);
						
					}					
					
				});*/
				
			}
			catch(JSONException e){
				e.printStackTrace();
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}

		
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}
		
		
		
	};
	
	public class LazyAdapter extends BaseAdapter {
	    
	    private Activity activity;
	    private LayoutInflater inflater=null;
	    public ImageLoader imageLoader; 
	    
	    public LazyAdapter(Activity a) {
	        activity = a;
	        
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        //imageLoader=new ImageLoader(activity.getApplicationContext());
	    }

	    public int getCount() {
	        return Names.size();
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }
	    
	    public View getView(int position, View convertView, ViewGroup parent){
	        View vi=convertView;
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.friendlistitem, null);

	        TextView text=(TextView)vi.findViewById(R.id.FriendName);;
	        ImageView image=(ImageView)vi.findViewById(R.id.FriendPhoto);
	        text.setText(Names.get(position));
	        image.setImageBitmap(Photos.get(position));
	       
	        return vi;
	    }
	}

	
	/*
	private RequestListener infoRequestListener = new RequestListener(){
		public void onComplete(String response,Object state){
			Log.d("Debug_Log","In infoRequestListener");
			JSONObject friend;
			JSONArray friendlist;
			int count=0;
			now_id=IDs.get(now_position);
			bundle = new Bundle();
			try{
				friend=new JSONObject(response);
				friendlist=friend.getJSONArray("data");
				Log.d("xxxxx","already get data");
				for(int i=0;i<friendlist.length();i++){
					if(count<3 && friendlist.getJSONObject(i).getString("type").equals("status") && friendlist.getJSONObject(i).getJSONObject("from").getString("id").equals(now_id)){
						try{
							Log.d("message",friendlist.getJSONObject(i).getString("message"));
							bundle.putString("STR"+count,friendlist.getJSONObject(i).getString("message"));
							count++;
						}
						catch(JSONException e2){
							e2.printStackTrace();
						}
					//list.add(friendlist.getJSONObject(i).getString("name"));
					//IDs.add(friendlist.getJSONObject(i).getString("id"));
					}
					
				}
				MyFBActivity.this.runOnUiThread(new Runnable(){
					public void run(){
						ArrayAdapter<String> adapter=new ArrayAdapter<String>(MyFBActivity.this,android.R.layout.simple_list_item_1,list);  
						//Toast.makeText(MyFBActivity.this, s, Toast.LENGTH_SHORT).show();
						mListView.setAdapter(adapter);
						mLoginButton.setText("Logout");
						mTextView.setText("朋友列表");
					}					
					
				});
				
			}
			catch(JSONException e){
				e.printStackTrace();
				
			}
			
    		Intent intent = new Intent();
	    	intent.setClass(MyFBActivity.this,Info.class);
	    	bundle.putString("NAME", ""+mListView.getItemAtPosition(now_position));
	    	bundle.putString("ID", IDs.get(now_position));
	    	
	    	intent.putExtras(bundle);
	    	Log.d("ddd","before start activity!");
	    	startActivityForResult(intent,9977);
			
			
		}

		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
		
		
		
	};
	*/
	/*
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode,resultCode,data);
		if(requestCode==9977&&resultCode==RESULT_CANCELED){;}
		else if(requestCode==9977&&resultCode==RESULT_OK){
			 Log.d("yyyyyyyy","9977,0");
			 Bundle bundle1 = data.getExtras(); 
			 if(bundle1!=null){
					String reply= bundle1.getString("REPLY");
					final String name= bundle1.getString("NAME");
					
					mTextView.setText("已傳送訊息至"+name); 
					mTextView.setTextSize(20);
					Handler handler = new Handler(); 
				    handler.postDelayed(new Runnable() { 
				         public void run() { 
				        	 
				        	 mTextView.setText("朋友列表");
				        	 mTextView.setTextSize(45);
									
				         } 
				    }, 2000);
				    Bundle b =new Bundle();
				    b.putString("method","POST");
				    b.putString("message",reply);
				    mAsyncRunner.request(""+now_id+"/feed",b,postListener);
				}
		}
		else
			mFacebook.authorizeCallback(requestCode, resultCode, data);
		
		
	}*/
	
}

