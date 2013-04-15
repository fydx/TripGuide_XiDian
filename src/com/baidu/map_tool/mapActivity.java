package com.baidu.map_tool;

import java.util.List;

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
	protected static final int INQUIRESECONDLINE = 0x102;
	
	protected final int sleep_time = 2000;
	
	
	// 原缩放级别
	private final int zoom_level = 17;

	// 西電34.12309, 108.84179
	//	34.1233959,108.83594160000007
	// 源地址为西电
	private double src_pt_x = 34.1233959;
	private double src_pt_y = 108.83594160000007;

	// 目标地址由外部activity传进来
//	private double tar_pt_x = 34.1233959 + 0.01;
//	private double tar_pt_y = 108.83594160000007 + 0.01;

	protected MapView map_view = null;

	
	protected MapController map_controller = null;
	protected MKOfflineMap mOffline = null;
//	protected MapMask amask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Log.i("axlecho", "set no title ok.");
		
		MapBase.Instance(this);
		Log.i("axlecho", "init instance ok");
		
		init();
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
		GeoPoint pt_begin = new GeoPoint((int) (src_pt_x * 1E6),(int) (src_pt_y * 1E6));
		Log.i("mapActivity设定地点", String.valueOf(src_pt_x * 1E6)+"  "+ String.valueOf(src_pt_y* 1E6));
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

		
	protected void init() {}
	
}