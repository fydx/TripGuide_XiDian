package com.baidu.map_tool;

import java.util.List;

import org.xdgdg.tripguide_xidian.R;
import org.xdgdg.tripguide_xidian.route;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class result_mapActivity extends mapActivity{
	private static int i = 0;
	private List<String> rec_name = null;
	private List<Integer> rec_pts_x = null;
	private List<Integer> rec_pts_y = null;
	private route rec_route = null;	
	private BMapManager mBMapMan = null;
	// 定位相关
		LocationClient mLocClient;
		public MyLocationListenner myListener = new MyLocationListenner();
		public NotifyLister mNotifyer = null;
		MyLocationOverlay myLocationOverlay = null;
		LocationData locData = null;
	protected final int sleep_time = 8000;
	private class LooperThread extends Thread {

		public void run() {
//			try {
//				Thread.sleep(sleep_time);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			Message message = new Message();
//			message.what = mapActivity.INQUIREFIRSTLINE;
//
//			result_mapActivity.this.event_handle.sendMessage(message);
//			Log.i("axlecho","send a message:INQUIREFIRSTLINE");
			
			while (true){
				if(is_end() == true)return;
				
				try {
					Thread.sleep(sleep_time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				i ++;
				Message message1 = new Message();
				message1.what = mapActivity.INQUIRESECONDLINE;
				result_mapActivity.this.event_handle.sendMessage(message1);
				Log.i("axlecho","send a message:INQUIRESECONDLINE");
			}
		}
	};

	private Handler event_handle = new Handler() {
		public void handleMessage(Message msg) {
						
			Log.i("axlecho","i:" + String.valueOf(i));
			switch (msg.what) {
			case mapActivity.INQUIREFIRSTLINE:
				Log.i("axlecho","get a message:INQUIREFIRSTLINE");
				amask.p2p_bybus(rec_name.get(0), rec_name.get(1));
				break;
				
			case mapActivity.INQUIRESECONDLINE:
				Log.i("axlecho","get a message:INQUIRESECONDLINE");
				GeoPoint a = new GeoPoint(rec_pts_x.get(i),rec_pts_y.get(i));
				GeoPoint b = new GeoPoint(rec_pts_x.get(i + 1),rec_pts_y.get(i + 1));
				amask.p2p_bywalk(a, b);
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private boolean is_end(){
		if (i == rec_name.size() - 1) {

			Log.i("axlehco", "deal !!");
			return false;
		}
			
		return true;
	}
	
	@Override
	public void init(){
		Log.i("axlecho","chiled init");
		setContentView(R.layout.result_map);
		Log.i("axlecho","setContentView ok.");
		
		map_view = (MapView) findViewById(R.id.bmapsresultView);
		map_controller = map_view.getController();
		
		setbegin();
		Log.i("axlehco","set srcpoint ok.");
		
		// 地图图形处理工具
		amask = new MapMask(this);
		Log.i("axlecho", "new mask ok.");

		Intent intent = getIntent();
		rec_route = (route) intent.getSerializableExtra("route");
		Log.i("axlecho","getIntent ok");
		
		rec_name = rec_route.poi_name_ToList(rec_route.getPoi_name());
		rec_pts_x = rec_route.poi_xToList(rec_route.getPoi_x());
		rec_pts_y = rec_route.poi_yToList(rec_route.getPoi_y());
		for (int i = 0; i < rec_pts_x.size(); i++) {
			Log.i("坐标", rec_pts_x.get(i).toString()+ " "+ rec_pts_y.get(i).toString());
		}
//		 启动初始化查询
		new Thread(new LooperThread()).start();
		Log.i("axlecho", "start thread ok.");
		
		
		GeoPoint pos = new GeoPoint((int)(34.1233959 * 1e6),(int)(108.83594160000007 * 1e6));
	//	GeoPoint pos1 = new GeoPoint((int)(34.14 * 1e6),(int)(108.85 * 1e6));
		amask.cover_point(pos);
		amask.cover_pic(pos, R.drawable.icon_marka, rec_name.get(i));
	//	amask.cover_pic(pos1, R.drawable.icon_marka, rec_name.get(i));
		//amask.cover_pic(pos,R.drawable.icon_marka,"起点");
		
		print_point();
		show_Mypos();
	}
	
	public void print_point(){
		for(int i = 0;i < rec_name.size();i ++){
			GeoPoint pos = new GeoPoint(rec_pts_x.get(i),rec_pts_y.get(i));
			Log.i("axlecho","x:" + String.valueOf(rec_pts_x.get(i)) + " " + "y:" + String.valueOf(rec_pts_y.get(i)));
			amask.cover_pic(pos, R.drawable.icon_marka, rec_name.get(i));
		}
	}
	public void show_Mypos(){
	//	MapBase mapBase = MapBase.Instance(getApplicationContext());
		mBMapMan = new BMapManager(getApplication());
	//	mBMapMan= mapBase.getMapManager();
		mBMapMan.init("5DD5B539C690BC0AF97D9E69733C1D87C9D70F7E", null);
		
	//setContentView(R.layout.activity_main);
	//	mMapView = (MapView) findViewById(R.id.bmapView);
		map_view.setBuiltInZoomControls(true);
		map_controller = map_view.getController();
		map_view.getController().setZoom(12);
		map_view.getController().enableClick(true);
		map_view.setBuiltInZoomControls(true);
		
		mLocClient = new LocationClient(this.getApplicationContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption locationOption = new LocationClientOption();
		locationOption.setOpenGps(true);
		locationOption.setCoorType("bd09ll");
		locationOption.setPriority(LocationClientOption.GpsFirst);
		locationOption.setAddrType("all");
		locationOption.setProdName("通过GPS定位");
		locationOption.setScanSpan(1000);
		mLocClient.setLocOption(locationOption);
		mLocClient.start();
		
		myLocationOverlay = new MyLocationOverlay(map_view);
		locData = new LocationData();
		myLocationOverlay.setData(locData);
		map_view.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		mLocClient.requestLocation();
		
	
	}
	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{
			if (location == null)
				return;
//			Log.e("loc listen", "ok");
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			locData.accuracy = location.getRadius();
			locData.direction = location.getDerect();
			myLocationOverlay.setData(locData);
			map_view .getOverlays().add(myLocationOverlay);
			map_view .refresh();
//			mMapController.animateTo(new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6)));
		}
		public void onReceivePoi(BDLocation poiLocation)
		{
			if (poiLocation == null)
			{
				return;
			}
		}
	}
	
	public class NotifyLister extends BDNotifyListener
	{
		public void onNotify(BDLocation mlocation, float distance)
		{}
	}
	
	public void MoveTo(GeoPoint pt)
	{
		map_controller.animateTo(pt);
	}
}

