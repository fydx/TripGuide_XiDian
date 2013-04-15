package com.baidu.map_tool;

import java.util.ArrayList;
import java.util.List;

import org.xdgdg.tripguide_xidian.R;
import org.xdgdg.tripguide_xidian.route;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class result_mapActivity extends mapActivity {
	private static int i = 0;
	private List<String> rec_name = null;
	private List<Integer> rec_pts_x = null;
	private List<Integer> rec_pts_y = null;
	private route rec_route = null;
	
	private int[] markers={R.drawable.icon_marka,R.drawable.icon_markb,R.drawable.icon_markc
			,R.drawable.icon_markd,R.drawable.icon_marke,R.drawable.icon_markf,R.drawable.icon_markg,
			R.drawable.icon_markh,R.drawable.icon_marki,R.drawable.icon_markj};

	private result_MapMask amask;

	@Override
	public void init() {
		Log.i("axlecho", "chiled init");
		setContentView(R.layout.result_map);
		Log.i("axlecho", "setContentView ok.");

		map_view = (MapView) findViewById(R.id.bmapsresultView);
		map_controller = map_view.getController();

		setbegin();
		Log.i("axlehco", "set srcpoint ok.");

		// 地图图形处理工具
		amask = new result_MapMask(this, map_view);
		Log.i("axlecho", "new mask ok.");

		Intent intent = getIntent();
		rec_route = (route) intent.getSerializableExtra("route");
		Log.i("axlecho", "getIntent ok");

		rec_name = rec_route.poi_name_ToList(rec_route.getPoi_name());
		rec_pts_x = rec_route.poi_xToList(rec_route.getPoi_x());
		rec_pts_y = rec_route.poi_yToList(rec_route.getPoi_y());

		GeoPoint pos = new GeoPoint((int) (34.1233959 * 1e6),(int) (108.83594160000007 * 1e6));
		
		amask.cover_pic(pos, R.drawable.icon_marka, rec_name.get(i));

		ArrayList<GeoPoint> lines = make_point();

		lines.add(pos);
		rec_name.add("西电");
		amask.cover_lines(lines);
		print_point(lines);
		
	}

	public ArrayList<GeoPoint> make_point(){
		ArrayList<GeoPoint> lines = new ArrayList<GeoPoint>();
		
		for (int i = 0; i < rec_name.size(); i++) {
			GeoPoint pos = new GeoPoint(rec_pts_x.get(i), rec_pts_y.get(i));
			Log.i("axlecho", "x:" + String.valueOf(rec_pts_x.get(i)) + " "+ "y:" + String.valueOf(rec_pts_y.get(i)));
			lines.add(pos);
		}
		return lines;
	}
	
	public void print_point(ArrayList<GeoPoint> lines) {
		for(int i = 0;i < lines.size();i ++)
			amask.cover_pic(lines.get(i), R.drawable.icon_marka, rec_name.get(i));
		
	}

	class result_MapMask extends MapMask {
		public result_MapMask(Activity _context, MapView _mapview) {
			super(_context, _mapview);
			// TODO Auto-generated constructor stub
		}
		
		//在这里可以重写点击tip的响应事件
		protected void on_tip(GeoPoint pt) {

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
