package com.baidu.map_tool;

import java.util.ArrayList;

import org.xdgdg.tripguide_xidian.MyApp;

import android.app.Activity;
import android.util.Log;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MapMask {
	
	
	private final int POINTSIZE = 10;
	private MapView mMapView = null;
	private Activity mparent = null;
    private String routeString;
	public String getRouteString() {
		return routeString;
	}

	public void setRouteString(String routeString) {
		this.routeString = routeString;
	}

	private GraphicsOverlay graphicsOverlay = null;

	private void check_masklayout() {
		// add new layout
		if (graphicsOverlay == null) {
			graphicsOverlay = new GraphicsOverlay(mMapView);
			mMapView.getOverlays().add(graphicsOverlay);
		}
	}

	public MapMask(Activity _parent, MapView _mapview) {
		mMapView = _mapview;
		mparent = _parent;
	}

	private void SetScrpos(double pos_x, double pos_y) {
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
		LocationData locData = new LocationData();

		locData.latitude = pos_x;
		locData.longitude = pos_y;
		locData.direction = 200.0f;
		myLocationOverlay.setData(locData);

		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		mMapView.getController().animateTo(
				new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
	}

	public void p2p_line(double start_x, double start_y, double end_x,
			double end_y) {

		GeoPoint start = new GeoPoint((int) (start_x * 1e6),
				(int) (start_y * 1e6));
		GeoPoint end = new GeoPoint((int) (end_x * 1e6), (int) (end_y * 1e6));

		check_masklayout();

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
		mMapView.refresh();
	}

	public void p2p_bywalk(double start_x, double start_y, double end_x,
			double end_y) {
		GeoPoint start = new GeoPoint((int) (start_x * 1e6),
				(int) (start_y * 1e6));
		GeoPoint end = new GeoPoint((int) (end_x * 1e6), (int) (end_y * 1e6));
		MKSearch mSearch = new MKSearch();
		mSearch.init(MapBase.Instance(null).getMapManager(),
				new resultListener());

		MKPlanNode node_start = new MKPlanNode();
		node_start.pt = start;

		MKPlanNode node_end = new MKPlanNode();
		node_end.pt = end;

		// 设置路线搜索策略，时间优先、费用最少或距离最短
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.walkingSearch(null, node_start, null, node_end);
		Log.i("axlecho", "p2p_bywalk ok");
	}
    //返回值由void 改为String (返回路线)
	public void p2p_bybus(double start_x, double start_y, double end_x,
			double end_y) {
		GeoPoint start = new GeoPoint((int) (start_x * 1e6),
				(int) (start_y * 1e6));
		GeoPoint end = new GeoPoint((int) (end_x * 1e6), (int) (end_y * 1e6));
		MKSearch mSearch = new MKSearch();
		resultListener reListener_edit = new resultListener();
		mSearch.init(MapBase.Instance(null).getMapManager(),
				reListener_edit);

		MKPlanNode node_start = new MKPlanNode();
		// node_start.pt = start;
		node_start.name = "西安电子科技大学(南校区)";

		MKPlanNode node_end = new MKPlanNode();
		node_end.pt = end;
		// node_end.name = "小寨";
		
		mSearch.transitSearch("西安", node_start, node_end);
		Log.i("axlecho", "p2p_bybus ok");
		
	}

	public void p2p_bycar(double start_x, double start_y, double end_x,
			double end_y) {
		GeoPoint start = new GeoPoint((int) (start_x * 1e6),
				(int) (start_y * 1e6));
		GeoPoint end = new GeoPoint((int) (end_x * 1e6), (int) (end_y * 1e6));
		MKSearch mSearch = new MKSearch();
		//改写一下
		//原本的版本在注释里
		/* mSearch.init(MapBase.Instance(null).getMapManager(),
				new resultListener());
			*/
		;
		mSearch.init(MapBase.Instance(null).getMapManager(),
				new resultListener());

		MKPlanNode node_start = new MKPlanNode();
		node_start.pt = start;

		MKPlanNode node_end = new MKPlanNode();
		node_end.pt = end;

		// 设置路线搜索策略，时间优先、费用最少或距离最短
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.drivingSearch(null, node_start, null, node_end);
		Log.i("axlecho", "p2p_bycar ok");
	}

	public void cover_circle(double center_x, double center_y, int radius) {
		GeoPoint center = new GeoPoint((int) (center_x * 1e6),
				(int) (center_y * 1e6));

		Geometry circleGeometry = new Geometry();

		check_masklayout();

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
		mMapView.refresh();
	}

	public void cover_point(double point_x, double point_y) {
		GeoPoint point = new GeoPoint((int) (point_x * 1e6),
				(int) (point_y * 1e6));

		// 构建点并显示
		Geometry pointGeometry = new Geometry();

		pointGeometry.setPoint(point, POINTSIZE);

		check_masklayout();

		Symbol pointSymbol = new Symbol();
		Symbol.Color pointColor = pointSymbol.new Color();
		pointColor.red = 0;
		pointColor.green = 255;
		pointColor.blue = 255;
		pointColor.alpha = 126;
		pointSymbol.setPointSymbol(pointColor);

		Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);

		graphicsOverlay.setData(pointGraphic);
		mMapView.refresh();
	}

	class resultListener implements MKSearchListener {
	
	
		@Override
		public void onGetPoiDetailSearchResult(int type, int error) {
			Log.i("axlecho", "onGetPoiDetailSearchResult is ok");
		}

		@Override
		public void onGetAddrResult(MKAddrInfo result, int arg1) {
			// TODO Auto-generated method stub
			if (result == null) {
				Log.i("axlecho", "result is null.");
				return;
			}
			Log.i("axlecho", "onGetAddrResult is ok");
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult result, int arg1) {
			// TODO Auto-generated method stub
			if (result == null) {
				Log.i("axlecho", "result is null.");
				return;
			}
			Log.i("axlecho", "onGetBusDetailResult is ok");
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
			RouteOverlay routeOverlay = new RouteOverlay(mparent, mMapView);
			routeOverlay.setData(result.getPlan(0).getRoute(0));
			mMapView.getOverlays().add(routeOverlay);
			mMapView.refresh();
		}

		@Override
		public void onGetPoiResult(MKPoiResult result, int arg1, int arg2) {
			// TODO Auto-generated method stub
			if (result == null) {
				Log.i("axlecho", "result is null.");
				return;
			}
			Log.i("axlecho", "onGetPoiResult is ok");
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult result, int arg1) {
			// TODO Auto-generated method stub
			if (result == null) {
				Log.i("axlecho", "result is null.");
				return;
			}
			Log.i("axlecho", "onGetSuggestionResult is ok");
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int arg1) {
			if (result == null) {
				Log.i("axlecho", "result is null.");
				return;
			}
			Log.i("axlecho", "onGetTransitRouteResult ok.");
			TransitOverlay routeOverlay = new TransitOverlay(mparent, mMapView);
			// 此处仅展示一个方案作为示例
			routeOverlay.setData(result.getPlan(0));
			// mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			mMapView.refresh();
			Log.i("axlecho", "i get here.");
			int num_line = result.getPlan(0).getNumLines() - 1;
			Log.i("fydx",
					"需要倒" + String.valueOf(result.getPlan(0).getNumLines() - 1)
							+ "次车");
			Log.i("axlecho", String.valueOf(result.getPlan(0).getDistance()));
			Log.i("axlecho", "content" + result.getPlan(0).getContent());
			Log.i("axlecho", "Uid" + result.getPlan(0).getLine(0).getUid());
			Log.i("axlecho", "OnStop"
					+ result.getPlan(0).getLine(0).getGetOnStop().name);
			Log.i("axlecho", "OffStop"
					+ result.getPlan(0).getLine(0).getGetOffStop().name);
			Log.i("axlecho", "Tip" + result.getPlan(0).getLine(0).getTip());
			Log.i("two", "Tip2" + result.getPlan(0).getLine(num_line).getTip());
			if (num_line == 0) {
				routeString = "从 "
						+ result.getPlan(0).getLine(0).getGetOnStop().name
						+ " " + result.getPlan(0).getLine(0).getTip();
			} else {
				routeString = "从"
						+ result.getPlan(0).getLine(0).getGetOnStop().name
						 + result.getPlan(0).getLine(0).getTip() + ",再"
						+ result.getPlan(0).getLine(1).getTip();
			}
			
			Log.e("路线", routeString);
			
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			mMapView.getController().animateTo(result.getStart().pt);

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int errorcode) {

			if (result == null) {
				Log.i("axlecho", "result is null.");
				return;
			}

			Log.i("axlecho", "onGetWalkingRouteResult is ok");
			RouteOverlay routeOverlay = new RouteOverlay(mparent, mMapView);
			routeOverlay.setData(result.getPlan(0).getRoute(0));

			MKRoute aRoute = result.getPlan(0).getRoute(0);
			ArrayList<ArrayList<GeoPoint>> pointslist = aRoute.getArrayPoints();
			ArrayList<GeoPoint> points = pointslist.get(0);

			for (int i = 0; i < points.size(); i++) {
				Log.i("axlecho", points.get(i).toString());
			}

			mMapView.getOverlays().add(routeOverlay);
			mMapView.refresh();

			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			mMapView.getController().animateTo(result.getStart().pt);

		}
	}

}
