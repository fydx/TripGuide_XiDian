package com.baidu.map_tool;

import net.tsz.afinal.FinalDb;

import org.xdgdg.tripguide_xidian.R;
import org.xdgdg.tripguide_xidian.route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class main_mapActivity extends mapActivity {

	Button btn_end = null;
	TextView tex_tip = null;

	String src_name;
	String tar_name;

	GeoPoint current_pt;
	GeoPoint src_pt;

	
	
	main_MapMask amask;

	//持久化
	private FinalDb db;
	private route route_1;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == 0) {
			if (resultCode != RESULT_OK) {
				Log.e("axlecho", "bad Activity result.");
				return;
			}
			Bundle extras = intent.getExtras();
			if (extras == null) {
				Log.e("axlecho", "bad intent.");
				return;
			}

			GeoPoint pt = new GeoPoint(extras.getInt("pos_x"),extras.getInt("pos_y"));
			String title = extras.getString("name");

			route_1.addpoint(title, extras.getInt("pos_x"), extras.getInt("pos_y"));
			
			amask.cover_pic(pt, R.drawable.icon_marke, title);
			amask.p2p_bywalk(current_pt, pt);
			current_pt = pt;
			
		}
	}

	protected void init() {
		Log.i("axlecho", "main_mapActivity init");

		setContentView(R.layout.map);
		Log.i("axlecho", "set contentview ok.");

		Intent intent = getIntent();
		src_name = intent.getStringExtra("start");
		tar_name = intent.getStringExtra("end");
		Log.i("axlecho", "src_name:" + src_name + " " + "tar_name:" + tar_name);
		Log.i("axlecho", "get intent ok.");

		tex_tip = (TextView) findViewById(R.id.busline_detail);
		tex_tip.setText("loading");

		map_view = (MapView) findViewById(R.id.bmapsView);
		map_controller = map_view.getController();
		Log.i("axlecho", "get content wight ok.");

		// 加载离线地图
		// scanofflinemap();
		// Log.i("axlecho", "scanofflinemap ok.");

		// 设置起始
		setbegin();
		Log.i("axlecho", "setbegin ok.");

		// 地图图形处理工具
		amask = new main_MapMask(this, map_view);
		Log.i("axlecho", "new mask ok.");

		btn_end = (Button) findViewById(R.id.button_end);
		btn_end.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.i("clicker", "set!");
				route_1.setBusline(tex_tip.getText().toString());
				Log.i("busline", tex_tip.getText().toString());
				db.save(route_1);
			}

		});
		
		// 启动初始化查询
		new Thread(new LooperThread()).start();
		Log.i("axlecho", "start thread ok.");

		//获取地理位置
		amask.show_Mypos();
		
		db= FinalDb.create(this);
		Log.i("axlecho","create DB ok.");
		
		route_1 = new route();
		Log.i("axlecho","create route ok.");
		Log.i("axlecho", "oncerate ok.");
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

			main_mapActivity.this.event_handle.sendMessage(message);
		}
	};

	private Handler event_handle = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case mapActivity.INQUIREFIRSTLINE:
				amask.p2p_bybus(src_name, tar_name);
				Log.i("axlecho", "parent handler");
				// amask.p2p_bybus(src_pt_x, src_pt_y, tar_pt_x, tar_pt_y);
				break;
			}
			super.handleMessage(msg);
		}

	};

	private class main_MapMask extends MapMask {

		public main_MapMask(Activity _context, MapView _mapview) {
			super(_context, _mapview);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result,
				int arg1) {
			// TODO Auto-generated method stub
			if (result == null) {
				Log.i("axlecho", "result is null.");
				return;
			}

			Log.i("axlecho", "onGetDrivingRouteResult is ok");

			routeOverlay.setData(result.getPlan(0).getRoute(0));
			map_view.getOverlays().add(routeOverlay);
			map_view.refresh();
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int arg1) {
			if (result == null) {
				Log.i("axlecho", "result is null.");
				return;
			}
			Log.i("axlecho", "onGetTransitRouteResult ok.");

			// 此处仅展示一个方案作为示例
			transit_routeOverlay.setData(result.getPlan(0));
			map_view.refresh();

			Log.i("axlecho", "i get here.");

			// 0413-10:55修改 这样子可以获取到两条路线
			int num_line = result.getPlan(0).getNumLines() - 1;
			Log.i("fydx",
					"需要倒" + String.valueOf(result.getPlan(0).getNumLines() - 1)
							+ "次车");
			String tmp = null;
			if (num_line == 0) {
				tmp = "从 " + result.getPlan(0).getLine(0).getGetOnStop().name
						+ " " + result.getPlan(0).getLine(0).getTip();
			} else {
				tmp = "从" + result.getPlan(0).getLine(0).getGetOnStop().name
						+ result.getPlan(0).getLine(0).getTip() + "\n再"
						+ result.getPlan(0).getLine(1).getTip();
			}

			// String tmp = result.getPlan(0).getLine(0).getTip();
			Log.i("axlecho", tmp);

			((main_mapActivity) context).src_pt = result.getPlan(0).getStart();
			((main_mapActivity) context).current_pt = result.getPlan(0)
					.getEnd();

			cover_pic(((main_mapActivity) context).src_pt,R.drawable.icon_marka, "起点");
			cover_pic(((main_mapActivity) context).current_pt,R.drawable.icon_marka, "终点");

			if (((main_mapActivity) context).tex_tip != null)
				((main_mapActivity) context).tex_tip.setText(tmp);

			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			map_view.getController().zoomToSpan(
					transit_routeOverlay.getLatSpanE6(),
					transit_routeOverlay.getLonSpanE6());
			map_view.getController().animateTo(result.getEnd().pt);
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
					int errorcode) {

				if (result == null) {
					Log.i("axlecho", "result is null.");
					return;
				}

				Log.i("axlecho", "onGetWalkingRouteResult is ok");

				routeOverlay.setData(result.getPlan(0).getRoute(0));
				map_view.refresh();

				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				map_view.getController().zoomToSpan(
						routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				map_view.getController().animateTo(result.getEnd().pt);

			}
		
		public void on_tip(GeoPoint pt) {

			int pos_x = pt.getLatitudeE6();
			int pos_y = pt.getLongitudeE6();
			Intent intent = new Intent();
			intent.setClass(context.getApplicationContext(),
					SearchActivity.class);

			intent.putExtra("search_x", pos_x);
			intent.putExtra("search_y", pos_y);
			context.startActivityForResult(intent, 0);

		}

	}

	@Override
	protected void onDestroy() {
		map_view.destroy();
		MapBase.Instance(this).terminate();
		amask.clear();
		Log.i("axlecho", "destroy ok.");
		super.onDestroy();
	}
	
}
