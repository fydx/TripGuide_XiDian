package com.baidu.map_tool;

import org.xdgdg.tripguide_xidian.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class mapActivity extends Activity {

	// private double mLat1 = 34.132008;
	// private double mLon1 = 108.844008;
	//西電34.12309, 108.84179
	private double mLat1 = 34.2230;
	private double mLon1 = 108.9467;

	private MapController map_controller = null;
	private MapView map_view = null;

	private Button btn_test = null;
	private MKOfflineMap mOffline = null; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MapBase mapbase = MapBase.Instance(this);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.map);

		map_view = (MapView) findViewById(R.id.bmapsView);
		map_controller = map_view.getController();

		scanofflinemap();
		setbegin();

		final MapMask amask = new MapMask(this, map_view);
		// amask.p2p_bywalk(new GeoPoint((int)(34.2230 * 1e6),(int)(108.9467 *
		// 1e6)),
		// new GeoPoint((int)(34.2441 * 1e6),(int)(108.9635 * 1e6)));
		//		amask.p2p_bycar(34.2230,108.9467,34.2441,108.9635);
		// amask.p2p_line(34.2230, 108.9467, 34.2441, 108.9635);
		// amask.cover_point(34.132008,108.844008);
		
		btn_test = (Button) findViewById(R.id.search);
		btn_test.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				amask.p2p_bywalk(34.2230,108.9467,34.2441,108.9635);
				amask.p2p_bybus(34.12203,108.84047,34.2441,108.9635);
			}
			
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MapBase.Instance(null).onTerminate();
	}

	public void setbegin() {
		GeoPoint pt_begin = new GeoPoint((int) (mLat1 * 1E6),
				(int) (mLon1 * 1E6));
		map_controller.setCenter(pt_begin);
		map_controller.setZoom(16);
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

}
