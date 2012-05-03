package com.pointsanity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
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
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

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
	private ArrayList<String> DisplayNames;
	private ArrayList<String> DisplayIDs;
	private ArrayList<Bitmap> Photos;
	private Facebook mFacebook;
	LazyAdapter adapter;
	int now_position;
	private ButtonOnClick buttonOnClick;
	private int shopIndex;
	private int currentPoints;
	private String currentId;
	private String myId;
	
	private static final int EXCHANGE_SUCCESS = 1;
	private static final int EXCHANGE_FAILED = 2;
	//private EditText mEditText;
	//private ImageView mImage;
    public static AsyncFacebookRunner mAsyncRunner;
    public static final String APP_ID = "321725801219299";
    String[] shopNames = new String[] { "歇手停", "69嵐", "小蘋果", "老咖啡", "親家便利商店","阿火茶舖", "KaKa真假", "KO便利商店", "ONE便利商店" };
    String[] shops = new String[] { "HAND", "LAND", "APPLE", "COFFEE", "FAMILY","FIRE", "KAKA", "KO", "ONE" };
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
        DisplayIDs=new ArrayList<String>();
        Urls=new ArrayList<String>();
        Photos=new ArrayList<Bitmap>();
        DisplayNames=new ArrayList<String>();
        buttonOnClick = new ButtonOnClick(0);
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
							mAsyncRunner.request("me",requestListener);
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
        
    
        mListView.setOnItemClickListener(new OnItemClickListener() {  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            	currentId = DisplayIDs.get(arg2);
        		genDialog();
            	
        		
        		
        	}
        });

    }//end onCreate
	private RequestListener requestListener = new RequestListener(){
		public void onComplete(String response,Object state){
			Log.d("DebugLog","In requestListener");
			JSONObject jObject;
			
			try{
				jObject=new JSONObject(response);
				 myId=jObject.getString("id");
				
				
			}
			catch(JSONException e){
				e.printStackTrace();
				Log.d("DebugLog","requestListener catch");
			}
			
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
	public void genDialog(){
   		LayoutInflater inflater = LayoutInflater.from(this);  
        final View textEntryView = inflater.inflate(R.layout.exchangedialog, null);  
        final Button mMinus = (Button) textEntryView.findViewById(R.id.button1);
        final Button mPlus = (Button) textEntryView.findViewById(R.id.button2);
        final EditText mPoints = (EditText) textEntryView.findViewById(R.id.editText1);
        //Spinner spinner = (Spinner) findViewById(R.id.shopSpinner);
        mMinus.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		Log.d("Debug","In mMinus");
        		int num=Integer.parseInt(mPoints.getText().toString());
        		if(num>0){
        			mPoints.setText(Integer.toString(num-1));
        			currentPoints = num-1;
        		}
        		
        	};
       	});
        mPlus.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		Log.d("Debug","In mPlus");
        		int num=Integer.parseInt(mPoints.getText().toString());
        		mPoints.setText(Integer.toString(num+1));
        		currentPoints = num+1;
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
        builder.setSingleChoiceItems(shopNames, 0,buttonOnClick); 
        builder.setPositiveButton("上傳", buttonOnClick);  
        builder.setNegativeButton("取消", buttonOnClick);  
        builder.show(); 

   		
   		
   		
   	}

	private RequestListener friendRequestListener = new RequestListener(){
		public void onComplete(String response,Object state){
			Log.d("Debug_Log","In friendRequestListener");
			JSONObject friend;
			JSONArray friendlist;
			adapter=new LazyAdapter(Exchange.this);
			try{
				friend=new JSONObject(response);
				friendlist=friend.getJSONArray("data");
				
				
				for(int i=0;i<friendlist.length();i+=50){
					String s="";
					for(int j=0;j<50;j++){
						
						s += (friendlist.getJSONObject(i+j).getString("id")+" ");
					
						Names.add(friendlist.getJSONObject(i+j).getString("name"));
						Urls.add("http://graph.facebook.com/"+friendlist.getJSONObject(i+j).getString("id")+"/picture");
						IDs.add(friendlist.getJSONObject(i+j).getString("id"));
					
						/*URL url = new URL("http://graph.facebook.com/"+friendlist.getJSONObject(i+j).getString("id")+"/picture");
						Photos.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
						*/
					}
					int count = 0;
					String data = updateToServer("FBLIST "+s);
					if(data.startsWith("FBLISTRESULT")){
	                	String[] part = data.split(" ");
	                	Log.d("Debug","i = "+i);
	                	Log.d("Debug","length = "+part.length);
	                	//Log.d("Debug",part[1]);
	                	for(int k=1;k<part.length;k++){
	                		Log.d("Debug","part"+k+"="+part[k]);
	                		int id_index = IDs.indexOf(part[k]);
	                		URL url = new URL(Urls.get(id_index));
	                		Log.d("Debug",Names.get(id_index));
							Photos.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
							DisplayNames.add(Names.get(id_index));
							DisplayIDs.add(IDs.get(id_index));
	                	}
	                	if(Photos.size()>count){
	                		Exchange.this.runOnUiThread(new Runnable(){
	                			public void run(){
	                				mListView.setAdapter(adapter);
	                				mListView.setTextFilterEnabled(true);
	    						
	                			}					
	    					
	                		});	
	                		count = Photos.size();
	                	}
	                }
				}
				
				
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
	        return DisplayNames.size();
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
	        text.setText(DisplayNames.get(position));
	        image.setImageBitmap(Photos.get(position));
	       
	        return vi;
	    }
	}

	private String updateToServer(String s){
		String address = "122.116.119.134";// 連線的ip
	    int port = 5566;// 連線的port
	    Socket client = new Socket();
	       
        InetSocketAddress isa = new InetSocketAddress(address, port);
       
        
        try {
            client.connect(isa, 15000);
            BufferedOutputStream out = new BufferedOutputStream(client
                    .getOutputStream(),1024);
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
	
	private class ButtonOnClick implements DialogInterface.OnClickListener
    {
      
       private int index; // 表示选项的索引
 
       public ButtonOnClick(int index)
       {
           this.index = index;
       }
 
       public void onClick(DialogInterface dialog, int which)
       {
           // which表示单击的按钮索引，所有的选项索引都是大于0，按钮索引都是小于0的。
           if (which >= 0)
           {
              //如果单击的是列表项，将当前列表项的索引保存在index中。
              //如果想单击列表项后关闭对话框，可在此处调用dialog.cancel()
              //或是用dialog.dismiss()方法。
              index = which;
           }
           else
           {
              //用户单击的是【确定】按钮
              if (which == DialogInterface.BUTTON_POSITIVE)
              {
                  //显示用户选择的是第几个列表项。
                  /*final AlertDialog ad = new AlertDialog.Builder(
                          Exchange.this).setMessage(
                          "你选择的地区是：" + index + ":" + shops[index]).show();*/
            	  Toast.makeText(Exchange.this, "Item is "+shopNames[index]+" points = "+currentPoints, Toast.LENGTH_SHORT).show();
         			
              	Runnable ConnectRun = new Runnable(){  
          	       	  
                 		public void run() {  
                 			
                 			String serverResult = updateToServer("EXCHANGE "+myId+" "+currentId+" "+shops[index]+" "+currentPoints);
                 			Log.d("Debug","serverResult = "+serverResult);
                 			if(serverResult.split(" ")[1].equals("true"))
                 				mHandler.obtainMessage(EXCHANGE_SUCCESS).sendToTarget();
                 			else
                 				mHandler.obtainMessage(EXCHANGE_FAILED).sendToTarget();
                 		}  
                 		  };  	
          		new Thread(ConnectRun).start(); 
              	
              	//setTitle(edtInput.getText());  

              }
              
           }
       }
       
       private final Handler mHandler = new Handler() {
           @Override
           public void handleMessage(Message msg) {
               switch (msg.what) {
               case EXCHANGE_SUCCESS:
                   Toast.makeText(getApplicationContext(), "交換成功!", Toast.LENGTH_SHORT).show();
                   break;
               case EXCHANGE_FAILED:
                   Toast.makeText(getApplicationContext(), "交換失敗，您的點數可能不足", Toast.LENGTH_LONG).show();
                   break;
               
               }
          
           }
       };
    }
	
}

