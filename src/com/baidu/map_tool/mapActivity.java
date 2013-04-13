package com.baidu.map_tool;

import org.xdgdg.tripguide_xidian.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class mapActivity extends Activity {
	
	protected static final int INQUIREFIRSTLINE = 0x101;
	private final int sleep_time = 2000;
	
	// 原缩放级别
	private final int zoom_level = 17;

	// 西電34.12309, 108.84179
	//	34.1233959,108.83594160000007
	// 源地址为西电
	private double src_pt_x = 34.1233959;
	private double src_pt_y = 108.83594160000007;

	// 目标地址由外部activity传进来
	private double tar_pt_x = 0;
	private double tar_pt_y = 0;

	MapView map_view = null;
	Button btn_test = null;
	TextView tex_tip = null;

	private MapController map_controller = null;
	private MKOfflineMap mOffline = null;
	private MapMask amask;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("axlecho", "activity create.");
		MapBase.Instance(this);

		Log.i("axlecho", "init instance ok");

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Log.i("axlecho", "set no title ok.");

		setContentView(R.layout.map);

		Log.i("axlecho", "set contentview ok.");

		Intent intent = getIntent();
		tar_pt_x = intent.getDoubleExtra("pos_x", 34.0000);
		tar_pt_y = intent.getDoubleExtra("pos_y", 108.0000);
		
//		test 小寨
		tar_pt_x = 34.222936;
		tar_pt_y = 108.946687;
		
		Log.i("axlecho", "get intent ok.");

		tex_tip = (TextView) findViewById(R.id.busline_detail);
		tex_tip.setText("loading");

		map_view = (MapView) findViewById(R.id.bmapsView);
		map_controller = map_view.getController();

		Log.i("axlecho", "get content wight ok.");

		// 加载离线地图
		// scanofflinemap();

		Log.i("axlecho", "scanofflinemap ok.");

		// 设置起始
		setbegin();
		Log.i("axlecho", "setbegin ok.");

		// 地图图形处理工具
		amask = new MapMask(this);
		Log.i("axlecho", "new mask ok.");

		// 启动初始化查询
		new Thread(new LooperThread()).start();
		Log.i("axlecho", "start thread ok.");

		// amask.set_scrpos(src_pt_x, src_pt_y);

		amask.cover_pic(src_pt_x, src_pt_y, R.drawable.icon_marka);
		amask.cover_pic(tar_pt_x, tar_pt_y, R.drawable.icon_markb);
		Log.i("axlecho", "oncerate ok.");
		
	
		btn_test = (Button)findViewById(R.id.btn_test);
		btn_test.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				amask.p2p_bywalk(src_pt_x, src_pt_y, tar_pt_x, tar_pt_y);
				src_pt_x = tar_pt_x;
				src_pt_y = tar_pt_y;
				
				tar_pt_x += 0.02;
				tar_pt_y += 0.02;
			}
			
		});
	}

	@Override
	protected void onPause() {
		map_view.onPause();
		Log.i("axlecho", "pause ok.");
		super.onPause();
	}

	@Override
	protected void onResume() {
		map_view.onResume();
		Log.i("axlecho", "resume ok.");
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		map_view.destroy();
		MapBase.Instance(this).terminate();
		Log.i("axlecho", "destroy ok.");
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		map_view.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		map_view.onRestoreInstanceState(savedInstanceState);
	}

	public void setbegin() {
		GeoPoint pt_begin = new GeoPoint((int) (tar_pt_x * 1E6),
				(int) (tar_pt_y * 1E6));
		Log.i("mapActivity设定地点", String.valueOf(tar_pt_x * 1E6)+"  "+ String.valueOf(tar_pt_y* 1E6));
		map_controller.setCenter(pt_begin);
		map_controller.setZoom(zoom_level);
	}

	public void scanofflinemap() {
		/** 离线地图初始化 **/
		mOffline = new MKOfflineMap();
		mOffline.init(map_controller, new MKOfflineMapListener() {
			public void onGetOfflineMapState(int type, int state) {
				switch (type) {
				case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
					MKOLUpdateElement update = mOffline.getUpdateInfo(state);
					// mText.setText(String.format("%s : %d%%", update.cityName,
					// update.ratio));
				}
					break;
				case MKOfflineMap.TYPE_NEW_OFFLINE:
					Log.d("OfflineDemo",
							String.format("add offlinemap num:%d", state));
					break;
				case MKOfflineMap.TYPE_VER_UPDATE:
					Log.d("OfflineDemo", String.format("new offlinemap ver"));
					break;
				}
			}
		});

		/** 离线地图导入离线包 **/
		int num = mOffline.scan();
		Log.i("axlecho", "the offline packet number:" + String.valueOf(num));

	}

	private class LooperThread extends Thread {

		public void run() {
			try {
				Thread.sleep(sleep_time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Message message = new Message();
			message.what = mapActivity.INQUIREFIRSTLINE;

			mapActivity.this.event_handle.sendMessage(message);
		}
	};

	private Handler event_handle = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case mapActivity.INQUIREFIRSTLINE:
//				amask.p2p_bybus("西安电子科技大学(南校区)", "小寨");
				amask.p2p_bybus(src_pt_x, src_pt_y, tar_pt_x, tar_pt_y);
				break;
			}
			super.handleMessage(msg);
		}
	};

}
