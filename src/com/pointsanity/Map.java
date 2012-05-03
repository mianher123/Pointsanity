package com.pointsanity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mapview);
//        MapView map = new MapView(this, "0vUBBiZoYQvqxxLi0pgax3xP_ccG6zB-WhI4U5Q");
//        setContentView(map);
        findViews();
        setupMap();
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private MapView map;
    private MapController controller;

    private void findViews() {
            map = (MapView) findViewById(R.id.map);
            controller = map.getController();
    }

    private MyLocationOverlay locLayer;
    private LandMarkOverlay markLayer;
    private void setupMap() {
            
            map.setTraffic(false);
            map.setBuiltInZoomControls(true);
            
    		
//            List<Overlay> overlays = map.getOverlays();
    		locLayer = new MyLocationOverlay(this, map);
    		map.getOverlays().add(locLayer);
    		locLayer.runOnFirstFix(new Runnable() {
                public void run() {
                    // Zoom in to current location
                    map.setTraffic(false);
                    controller.setZoom(17);
                    controller.animateTo(locLayer.getMyLocation());
                }
            });
//            overlays.add(locLayer);
    		
    		Drawable pin=getResources().getDrawable(R.drawable.mark_red);
            pin.setBounds(0, 0, pin.getMinimumWidth(), pin.getMinimumHeight());

            markLayer = new LandMarkOverlay(pin);
            map.getOverlays().add(markLayer);
    }

	protected static final int MENU_HAND = Menu.FIRST;
    protected static final int MENU_LAND = Menu.FIRST+1;
    protected static final int MENU_APPLE = Menu.FIRST+2;
    protected static final int MENU_COFFEE = Menu.FIRST+3;
    protected static final int MENU_FAMILY = Menu.FIRST+4;
    protected static final int MENU_FIRE = Menu.FIRST+5;
    protected static final int MENU_KAKA = Menu.FIRST+6;
    protected static final int MENU_KO = Menu.FIRST+7;
    protected static final int MENU_ONE = Menu.FIRST+8;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            // TODO Auto-generated method stub
    	menu.add(0, MENU_HAND, 0, "歇手停");
        menu.add(0, MENU_LAND, 0, "69嵐");
        menu.add(0, MENU_APPLE, 0, "小蘋果");
        menu.add(0, MENU_COFFEE, 0, "老咖啡");
        menu.add(0, MENU_FAMILY, 0, "親家便利商店");
        menu.add(0, MENU_FIRE, 0, "阿火茶鋪");
        menu.add(0, MENU_KAKA, 0, "KaKa真假");
        menu.add(0, MENU_KO, 0, "KO便利商店");
        menu.add(0, MENU_ONE, 0, "ONE便利商店");
            return super.onCreateOptionsMenu(menu);
    }

    GeoPoint hand = new GeoPoint(
                (int) (25.018009 * 1000000),
                    (int) (121.540192 * 1000000)
            );
    GeoPoint land = new GeoPoint(
                (int) (25.014461 * 1000000),
                    (int) (121.533465 * 1000000)
            );
    GeoPoint apple = new GeoPoint(
                (int) (25.016055 * 1000000),
                    (int) (121.532414 * 1000000)
            );
    GeoPoint coffee = new GeoPoint(
            (int) (25.015773 * 1000000),
                (int) (121.53339 * 1000000)
        );
    GeoPoint family = new GeoPoint(
            (int) (25.021976 * 1000000),
                (int) (121.541179 * 1000000)
        );
    GeoPoint fire = new GeoPoint(
            (int) (25.015151 * 1000000),
                (int) (121.536362 * 1000000)
        );
    GeoPoint kaka = new GeoPoint(
            (int) (25.014723 * 1000000),
                (int) (121.534367 * 1000000)
        );
    GeoPoint ko = new GeoPoint(
            (int) (25.017076 * 1000000),
                (int) (121.530848 * 1000000)
        );
    GeoPoint one = new GeoPoint(
            (int) (25.017173 * 1000000),
                (int) (121.544849 * 1000000)
        );

    public boolean onOptionsItemSelected(MenuItem item) {
            // TODO Auto-generated method stub
            super.onOptionsItemSelected(item);
            switch(item.getItemId()) {
                    case MENU_HAND:
                            controller.animateTo(hand);
                            break;
                    case MENU_LAND:
                            controller.animateTo(land);
                            break;
                    case MENU_APPLE:
                            controller.animateTo(apple);
                            break;
                    case MENU_COFFEE:
                        controller.animateTo(coffee);
                        break;
                    case MENU_FAMILY:
                        controller.animateTo(family);
                        break;
                    case MENU_FIRE:
                        controller.animateTo(fire);
                        break;
                    case MENU_KAKA:
                    	controller.animateTo(kaka);
                    	break;
                    case MENU_KO:
                    	controller.animateTo(ko);
                    	break;
                    case MENU_ONE:
                    	controller.animateTo(one);
                    	break;
            }
            return super.onOptionsItemSelected(item);
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (/*keyCode == KeyEvent.KEYCODE_I ||*/ keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Zooming In
//            controller.setZoom(map.getZoomLevel() + 1);
        	controller.setZoom(Math.min(map.getMaxZoomLevel(), map.getZoomLevel() + 1));
        	return true;
        } else if (/*keyCode == KeyEvent.KEYCODE_O ||*/ keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Zooming Out
//            controller.setZoom(map.getZoomLevel() - 1);
        	controller.setZoom(Math.max(15, map.getZoomLevel() - 1));
        	return true;
        }/* else if (keyCode == KeyEvent.KEYCODE_S) {
            // Switch to satellite view
            map.setSatellite(true) ;
            map.setTraffic(false);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_T) {
            // Switch on traffic overlays
            map.setSatellite(false) ;
            map.setTraffic(true);
            return true;
        }*/
        return super.onKeyDown(keyCode,event);
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//check connectivity
    	ConnectivityManager connectivityMgr =  (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo netInfo = connectivityMgr.getActiveNetworkInfo();
    	if (netInfo == null) {
    		new AlertDialog.Builder(Map.this)
        	.setTitle("No Connection")
        	.setMessage("Please enable internet connection before use this app")
        	.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    		    	startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    			}
        	})
        	.show();
    	} else {
    		locLayer.enableMyLocation();
    	}
	}
	
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locLayer.disableMyLocation();
	}
    
    private class LandMarkOverlay extends ItemizedOverlay<OverlayItem> {

        private List<OverlayItem> items = new ArrayList<OverlayItem>();
        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow){
        
        	super.draw(canvas, mapView, false);
        
        }

        public LandMarkOverlay(Drawable defaultMarker) {
                super(defaultMarker);
                // TODO Auto-generated constructor stub
                items.add(
                    new OverlayItem(
                                hand,
                                "台北",
                                "歇手停")
                        );
                items.add(
                    new OverlayItem(
                                land,
                                "台中",
                                "69嵐")
                        );
                items.add(
                    new OverlayItem(
                                apple,
                                "高雄",
                                "小蘋果")
                        );
                items.add(
                        new OverlayItem(
                                    coffee,
                                    "高雄",
                                    "老咖啡")
                            );
                items.add(
                        new OverlayItem(
                                    family,
                                    "高雄",
                                    "親家便利商店")
                            );
                items.add(
                        new OverlayItem(
                                    fire,
                                    "高雄",
                                    "阿火茶舖")
                            );
                items.add(
                        new OverlayItem(
                                    kaka,
                                    "高雄",
                                    "KaKa真假")
                            );
                items.add(
                        new OverlayItem(
                                    ko,
                                    "高雄",
                                    "KO便利商店")
                            );
                items.add(
                        new OverlayItem(
                                    one,
                                    "高雄",
                                    "ONE便利商店")
                            );
                populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
                // TODO Auto-generated method stub
                return items.get(i);
        }

        @Override
        public int size() {
                // TODO Auto-generated method stub
                return items.size();
        }

    @Override
    protected boolean onTap(int pIndex) {
        Toast.makeText(Map.this,
            "" + items.get(pIndex).getSnippet(),
            Toast.LENGTH_SHORT).show();
        return true;
    }
}
}