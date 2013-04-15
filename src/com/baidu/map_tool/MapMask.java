package com.baidu.map_tool;

import java.util.ArrayList;
import java.util.List;

import org.xdgdg.tripguide_xidian.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MapMask {

	private final int POINTSIZE = 5;
	// private mapActivity mparent = null;

	// 地图view
	protected MapView map_view = null;

	// 上下文
	protected Activity context = null;

	// 自定义图层
	protected GraphicsOverlay graphicsOverlay = null;

	// 气泡图层
	protected OverItemT items = null;

	// 公交图层
	protected TransitOverlay transit_routeOverlay = null;

	// 走路图层
	protected RouteOverlay routeOverlay = null;

	// 定位相关
	private LocationClient mLocClient = null;
	private MyLocationOverlay myLocationOverlay = null;
	private LocationData locData = null;
	private MyLocationListenner aLocListenner = null;

	public MapMask(Activity _context, MapView _mapview) {
		context = _context;
		map_view = _mapview;

		items = new OverItemT(context.getResources().getDrawable(
				R.drawable.ic_launcher), context); // 得到需要标在地图上的资源)
		map_view.getOverlays().add(items);

		graphicsOverlay = new GraphicsOverlay(map_view);
		map_view.getOverlays().add(graphicsOverlay);

		transit_routeOverlay = new TransitOverlay(context, map_view);
		map_view.getOverlays().add(transit_routeOverlay);

		routeOverlay = new RouteOverlay(context, map_view);
		map_view.getOverlays().add(routeOverlay);

		myLocationOverlay = new MyLocationOverlay(map_view);
		map_view.getOverlays().add(myLocationOverlay);

		aLocListenner = new MyLocationListenner();
	}

	public void p2p_bywalk(GeoPoint start, GeoPoint end) {
		// GeoPoint start = new GeoPoint((int) (start_x * 1e6),(int) (start_y *
		// 1e6));
		// GeoPoint end = new GeoPoint((int) (end_x * 1e6), (int) (end_y *
		// 1e6));

		MKPlanNode node_start = new MKPlanNode();
		node_start.pt = start;

		MKPlanNode node_end = new MKPlanNode();
		node_end.pt = end;

		MKSearch mSearch = new MKSearch();
		mSearch.init(MapBase.Instance(null).getMapManager(),
				new resultListener());

		// 设置路线搜索策略，时间优先、费用最少或距离最短
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.walkingSearch(null, node_start, null, node_end);
		Log.i("axlecho", "p2p_bywalk ok");
	}

	public void p2p_bybus(double start_x, double start_y, double end_x,
			double end_y) {
		GeoPoint start = new GeoPoint((int) (start_x * 1e6),
				(int) (start_y * 1e6));
		GeoPoint end = new GeoPoint((int) (end_x * 1e6), (int) (end_y * 1e6));

		MKPlanNode node_start = new MKPlanNode();
		node_start.pt = start;

		MKPlanNode node_end = new MKPlanNode();
		node_end.pt = end;

		MKSearch mSearch = new MKSearch();
		mSearch.init(MapBase.Instance(null).getMapManager(),
				new resultListener());

		mSearch.transitSearch("西安", node_start, node_end);
		Log.i("axlecho", "p2p_bybus by point ok");
	}

	public void p2p_bybus(String start, String end) {
		MKSearch mSearch = new MKSearch();
		mSearch.init(MapBase.Instance(null).getMapManager(),
				new resultListener());

		MKPlanNode node_start = new MKPlanNode();
		node_start.name = start;
		// "西安电子科技大学(南校区)";

		MKPlanNode node_end = new MKPlanNode();
		node_end.name = end;

		mSearch.transitSearch("西安", node_start, node_end);
		Log.i("axlecho", "p2p_bybus by name ok");
	}

	public void p2p_bycar(double start_x, double start_y, double end_x,
			double end_y) {
		GeoPoint start = new GeoPoint((int) (start_x * 1e6),
				(int) (start_y * 1e6));
		GeoPoint end = new GeoPoint((int) (end_x * 1e6), (int) (end_y * 1e6));

		MKPlanNode node_start = new MKPlanNode();
		node_start.pt = start;

		MKPlanNode node_end = new MKPlanNode();
		node_end.pt = end;

		MKSearch mSearch = new MKSearch();
		mSearch.init(MapBase.Instance(null).getMapManager(),
				new resultListener());

		// 设置路线搜索策略，时间优先、费用最少或距离最短
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.drivingSearch(null, node_start, null, node_end);
		Log.i("axlecho", "p2p_bycar ok");
	}

	public void cover_circle(double center_x, double center_y, int radius) {
		GeoPoint center = new GeoPoint((int) (center_x * 1e6),
				(int) (center_y * 1e6));

		Geometry circleGeometry = new Geometry();

		circleGeometry.setCircle(center, radius);

		Symbol circleSymbol = new Symbol();
		Symbol.Color circleColor = circleSymbol.new Color();
		circleColor.red = 0;
		circleColor.green = 255;
		circleColor.blue = 0;
		circleColor.alpha = 126;
		circleSymbol.setSurface(circleColor, 1, 3);

		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);

		graphicsOverlay.setData(circleGraphic);
		map_view.refresh();
	}

	public void cover_point(GeoPoint point) {
		// GeoPoint point = new GeoPoint((int) (point_x * 1e6),(int) (point_y *
		// 1e6));

		// 构建点并显示
		Geometry pointGeometry = new Geometry();

		pointGeometry.setPoint(point, POINTSIZE);

		Symbol pointSymbol = new Symbol();
		Symbol.Color pointColor = pointSymbol.new Color();
		pointColor.red = 0;
		pointColor.green = 0;
		pointColor.blue = 0;
		pointColor.alpha = 255;
		pointSymbol.setPointSymbol(pointColor);

		Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);

		graphicsOverlay.setData(pointGraphic);
		map_view.refresh();
	}

	public void cover_line(double start_x, double start_y, double end_x,
			double end_y) {

		GeoPoint start = new GeoPoint((int) (start_x * 1e6),
				(int) (start_y * 1e6));
		GeoPoint end = new GeoPoint((int) (end_x * 1e6), (int) (end_y * 1e6));

		Geometry lineGeometry = new Geometry();
		GeoPoint[] linePoints = new GeoPoint[2];
		linePoints[0] = start;
		linePoints[1] = end;
		// linePoints[2] = pt3;
		lineGeometry.setPolyLine(linePoints);

		Symbol lineSymbol = new Symbol();
		Symbol.Color lineColor = lineSymbol.new Color();

		lineColor.red = 0;
		lineColor.green = 0;
		lineColor.blue = 0;
		lineColor.alpha = 255;

		lineSymbol.setLineSymbol(lineColor, 10);

		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);

		graphicsOverlay.setData(lineGraphic);
		map_view.refresh();
	}

	public void cover_pic(GeoPoint pos, int pic_id, String title) {
		Drawable marker = context.getResources().getDrawable(pic_id); // 得到需要标在地图上的资源
		// GeoPoint pos = new GeoPoint((int)(point_x * 1e6),(int)(point_y *
		// 1e6));

		if (marker == null)
			Log.e("axlecho", "marker is null");

		OverlayItem item = new OverlayItem(pos, title, "test");
		item.setMarker(marker);
		items.additem(item);

		map_view.refresh();// 刷新地图
		Log.i("axlecho", "cover_pic ok.");
	}

	// points_list.size()　表示有几段路线
	// points_list.get(i).size()　表示第i段路线有几个点
	public void cover_lines(ArrayList<GeoPoint> points_list) {

		Log.i("axlecho", String.valueOf(points_list.size()));
		
		Geometry lineGeometry = new Geometry();
		GeoPoint[] linePoints = new GeoPoint[points_list.size()];

		for (int i = 0; i < points_list.size(); i++) {
				linePoints[i] = points_list.get(i);
		}
		lineGeometry.setPolyLine(linePoints);

		Symbol lineSymbol = new Symbol();
		Symbol.Color lineColor = lineSymbol.new Color();

		lineColor.red = 32;
		lineColor.green = 188;
		lineColor.blue = 250;
		lineColor.alpha = 180;

		lineSymbol.setLineSymbol(lineColor, 5);
		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);

		graphicsOverlay.setData(lineGraphic);
		map_view.refresh();
	}

	protected class resultListener implements MKSearchListener {

		resultListener() {
			Log.e("axlecho", "mask_resultListener");
		}

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			MapMask.this.onGetDrivingRouteResult(arg0, arg1);
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			MapMask.this.onGetTransitRouteResult(arg0, arg1);
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			Log.i("axlecho", "get WalkingRouteResult");
			MapMask.this.onGetWalkingRouteResult(arg0, arg1);
		}

	}

	protected class OverItemT extends ItemizedOverlay<OverlayItem> {

		private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
		private Context mContext;
		private PopupOverlay pop = null;

		private int current = -1;

		public OverItemT(Drawable marker, Context _mContext) {
			super(marker);
			mContext = _mContext;
			// 点击tip事件
			pop = new PopupOverlay(map_view, new PopupClickListener() {

				@Override
				public void onClickedPopup(int index) {
					if (current == -1) {
						Log.e("axlecho", "onClickedPopup something wrong.");
						return;
					}
					on_tip(GeoList.get(current).getPoint());
				}
			});

			populate();
			this.mContext = _mContext;
		}

		@Override
		protected OverlayItem createItem(int i) {
			// TODO Auto-generated method stub
			return GeoList.get(i);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return GeoList.size();
		}

		protected void additem(OverlayItem item) {
			GeoList.add(item);
			populate();
		}

		protected boolean onTap(int index) {

			View popview = LayoutInflater.from(mContext).inflate(
					R.layout.popup, null);// 获取要转换的View资源
			TextView TestText = (TextView) popview.findViewById(R.id.test_text);
			TestText.setText(GeoList.get(index).getTitle() + "\n");// 将每个点的Title在弹窗中以文本形式显示出来

			Bitmap popbitmap = convertViewToBitmap(popview);

			Log.i("axlecho",
					"buble was clicked index :" + String.valueOf(index));
			current = index;
			Bitmap[] bmps = new Bitmap[1];
			bmps[0] = popbitmap;
			pop.showPopup(bmps, GeoList.get(index).getPoint(), 32);

			return true;
		}

		// 点击其他地方事件
		public boolean onTap(GeoPoint pt, MapView mapView) {
			Log.i("axlecho", "other area was clicked");
			if (pop != null) {
				pop.hidePop();
			}
			super.onTap(pt, mapView);
			return false;
		}

		public Bitmap convertViewToBitmap(View view) {
			view.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
			view.buildDrawingCache();
			Bitmap bitmap = view.getDrawingCache();

			return bitmap;
		}

	}

	protected void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {

	}

	protected void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {

	}

	protected void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {

	}

	protected void on_tip(GeoPoint pt) {

	}

	public void show_Mypos() {
		mLocClient = new LocationClient(context.getApplicationContext());
		mLocClient.registerLocationListener(aLocListenner);
		LocationClientOption locationOption = new LocationClientOption();
		locationOption.setOpenGps(true);
		locationOption.setCoorType("bd09ll");
		locationOption.setPriority(LocationClientOption.GpsFirst);
		locationOption.setAddrType("all");
		locationOption.setProdName("通过GPS定位");
		locationOption.setScanSpan(3000);
		mLocClient.setLocOption(locationOption);
		mLocClient.start();

		locData = new LocationData();
		myLocationOverlay.setData(locData);
		myLocationOverlay.enableCompass();

		mLocClient.requestLocation();
	}

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			if (myLocationOverlay == null)
				return;
			Log.e("loc listen", "ok");
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			locData.accuracy = location.getRadius();
			locData.direction = location.getDerect();

			myLocationOverlay.setData(locData);
			map_view.refresh();
			Log.i("axlecho", "onReceiveLocation");
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	public void clear() {
		aLocListenner = null;

		context = null;
		// private route route_1;
		// 自定义图层
		graphicsOverlay = null;

		// 气泡图层
		items = null;

		// 公交图层
		transit_routeOverlay = null;

		// 走路图层
		routeOverlay = null;

		// 定位相关
		mLocClient = null;
		myLocationOverlay = null;
		locData = null;
		aLocListenner = null;
	}

	public ArrayList<GeoPoint> GetallPoint() {
		ArrayList<GeoPoint> pts = new ArrayList<GeoPoint>();
		for (int i = 0; i < transit_routeOverlay.mPlan.size(); i++) {
			for (int j = 0; j < transit_routeOverlay.mPlan.get(i).getNumLines(); j++) {
				pts.addAll(transit_routeOverlay.mPlan.get(i).getLine(j)
						.getPoints());
			}
		}

		for (int i = 0; i < routeOverlay.mRoute.size(); i++) {
			for (int j = 0; j < routeOverlay.mRoute.get(i).getArrayPoints()
					.size(); j++) {
				pts.addAll(routeOverlay.mRoute.get(i).getArrayPoints().get(j));
			}
		}
		return pts;

	}
	
}
