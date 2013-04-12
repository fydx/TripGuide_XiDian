package com.baidu.map_tool;

import org.xdgdg.tripguide_xidian.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
	
	// 原缩放级别
	private final int zoom_level = 17;

	// 西電34.12309, 108.84179
	// 源地址为西电
	private double src_pt_x = 34.12309;
	private double src_pt_y = 108.84179;

	// 目标地址由外部activity传进来
	private double tar_pt_x = 0;
	private double tar_pt_y = 0;

	private MapView map_view = null;
	private Button btn_test = null;

	private MapController map_controller = null;
	private MKOfflineMap mOffline = null;
	MapMask amask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MapBase mapbase = MapBase.Instance(this);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.map);

		Intent intent = getIntent();
		tar_pt_x = intent.getDoubleExtra("pos_x", 34.0000);
		tar_pt_y = intent.getDoubleExtra("pos_y", 108.0000);
		Log.i("axlecho", "pos_x:" + String.valueOf(tar_pt_x) + "pos_y:"
				+ String.valueOf(tar_pt_y));

		map_view = (MapView) findViewById(R.id.bmapsView);
		map_controller = map_view.getController();

		// 加载离线地图
		scanofflinemap();

		// 设置起始
		setbegin();
		
		//地图图形处理工具
		amask = new MapMask(this, map_view);
		
		//启动初始化查询
		new Thread(new LooperThread()).start();  
	}

	@Override
	protected void onPause() {
		map_view.onPause();
		super.onPause();
	}

	@Override
	protected void onStart(){

		super.onStart();
	}
	
	@Override
	protected void onResume() {
		map_view.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MapBase.Instance(null).onTerminate();
	}

	public void setbegin() {
		GeoPoint pt_begin = new GeoPoint((int) (tar_pt_x * 1E6),
				(int) (tar_pt_y * 1E6));
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
		public Handler mHandler;

		public void run() {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Message message = new Message();
			message.what = mapActivity.INQUIREFIRSTLINE;

			mapActivity.this.myHandler.sendMessage(message);

		}
	};

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case mapActivity.INQUIREFIRSTLINE:
				amask.p2p_bybus(src_pt_x, src_pt_y, tar_pt_x, tar_pt_y);
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	}

