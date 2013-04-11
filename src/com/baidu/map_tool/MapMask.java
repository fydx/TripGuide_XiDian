package com.baidu.map_tool;

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
import com.baidu.mapapi.map.Symbol.Color;
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
	
	private final int POINTSIZE = 10;
	private MapView mMapView = null;
	private Activity mparent = null;
	private MKSearch mSearch = null;

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

	// public static void SetPos(Context context, MapView mMapView) {
	// Drawable marker =
	// context.getResources().getDrawable(R.drawable.ic_launcher);
	// // 得到需要标在地图上的资源
	//
	// mMapView.getOverlays().add(new OverItemT(marker, context));
	//
	// // 添加ItemizedOverlay实例到mMapView
	// mMapView.refresh();// 刷新地图
	// }

	public void p2p_line(double start_x, double start_y, double end_x,
			double end_y) {

		GeoPoint start = new GeoPoint((int) (start_x * 1e6),
				(int) (start_y * 1e6));
		GeoPoint end = new GeoPoint((int) (end_x * 1e6), (int) (end_y * 1e6));

		// add new layout
		GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
		mMapView.getOverlays().add(graphicsOverlay);

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

	public void p2p_bywalk(GeoPoint start, GeoPoint end) {
		MKSearch mSearch = new MKSearch();
		mSearch.init(MapBase.Instance(null).getMapManager(),
				new MKSearchListener() {

					@Override
					public void onGetPoiDetailSearchResult(int type, int error) {

					}

					@Override
					public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onGetBusDetailResult(MKBusLineResult arg0,
							int arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onGetDrivingRouteResult(
							MKDrivingRouteResult arg0, int arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onGetPoiResult(MKPoiResult arg0, int arg1,
							int arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onGetSuggestionResult(MKSuggestionResult arg0,
							int arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onGetTransitRouteResult(
							MKTransitRouteResult arg0, int arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onGetWalkingRouteResult(
							MKWalkingRouteResult result, int errorcode) {

						if (result == null) {
							Log.i("axlecho", "result is null.");
							return;
						}

						Log.i("axlecho", "result is ok");
						RouteOverlay routeOverlay = new RouteOverlay(mparent,
								mMapView);
						routeOverlay.setData(result.getPlan(0).getRoute(0));
						mMapView.getOverlays().add(routeOverlay);
						mMapView.refresh();

					}
				});

		MKPlanNode node_start = new MKPlanNode();
		node_start.pt = start;

		MKPlanNode node_end = new MKPlanNode();
		node_end.pt = end;

		// 设置路线搜索策略，时间优先、费用最少或距离最短
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mSearch.walkingSearch(null, node_start, null, node_end);

		Log.i("axlecho", "p2p_bywalk ok");
	}

	public void p2p_bybus(GeoPoint start, GeoPoint end) {

	}

	public void p2p_bycar(GeoPoint start, GeoPoint end) {

	}

	public void cover_circle(double center_x, double center_y, int radius) {
		GeoPoint center = new GeoPoint((int) (center_x * 1e6),
				(int) (center_y * 1e6));
		
		Geometry circleGeometry = new Geometry();

		GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
		mMapView.getOverlays().add(graphicsOverlay);
		
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
	
	public void cover_point(double point_x,double point_y){
	   GeoPoint point = new GeoPoint((int)(point_x * 1e6), (int)(point_y * 1e6));
	   	
	  //构建点并显示
  		Geometry pointGeometry = new Geometry();
  	
  		pointGeometry.setPoint(point, POINTSIZE);
		
  		// add new layout
		GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
		mMapView.getOverlays().add(graphicsOverlay);
		
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
}
