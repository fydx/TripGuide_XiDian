package com.baidu.map_tool;

import java.util.ArrayList;
import java.util.List;

import org.xdgdg.tripguide_xidian.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;

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


public class MapMask{

	private final int POINTSIZE = 5;
	private mapActivity mparent = null;

	//自定义图层
	private GraphicsOverlay graphicsOverlay = null;
	
	//气泡图层
	private OverItemT items  = null;
	
	//公交图层
	private TransitOverlay transit_routeOverlay = null;
	
	//走路图层
	private RouteOverlay walk_routeOverlay = null;
 
	public MapMask(Activity _parent) {
		mparent = (mapActivity) _parent;
		
		items = new OverItemT(mparent.getResources().getDrawable(R.drawable.ic_launcher),mparent); //得到需要标在地图上的资源)
		mparent.map_view.getOverlays().add(items);
		
		graphicsOverlay = new GraphicsOverlay(mparent.map_view);
		mparent.map_view.getOverlays().add(graphicsOverlay);
		
		transit_routeOverlay = new TransitOverlay(mparent,mparent.map_view);
		mparent.map_view.getOverlays().add(transit_routeOverlay);
		
		walk_routeOverlay = new RouteOverlay(mparent,mparent.map_view);
		mparent.map_view.getOverlays().add(walk_routeOverlay);
		
		
	
	}

	private void set_scrpos(GeoPoint src_pt) {
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mparent.map_view);
		
		
		//GeoPoint src_pt =new GeoPoint((int) (pos_x * 1e6),(int) (pos_y * 1e6));
		
		LocationData locData = new LocationData();
		locData.latitude = src_pt.getLatitudeE6() / 1000000.0;
		locData.longitude = src_pt.getLongitudeE6() / 1000000.0;
		locData.direction = 200.0f;
		myLocationOverlay.setData(locData);

		mparent.map_view.getOverlays().add(myLocationOverlay);
		mparent.map_view.refresh();
		mparent.map_view.getController().animateTo(src_pt);
	}

	public void p2p_bywalk(GeoPoint start, GeoPoint end) {
//		GeoPoint start = new GeoPoint((int) (start_x * 1e6),(int) (start_y * 1e6));
//		GeoPoint end   = new GeoPoint((int) (end_x * 1e6), (int) (end_y * 1e6));
		MKSearch mSearch = new MKSearch();
		mSearch.init(MapBase.Instance(null).getMapManager(),new resultListener());

		MKPlanNode node_start = new MKPlanNode();
		node_start.pt = start;

		MKPlanNode node_end = new MKPlanNode();
		node_end.pt = end;

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
		MKSearch mSearch = new MKSearch();
		mSearch.init(MapBase.Instance(null).getMapManager(),
				new resultListener());

		MKPlanNode node_start = new MKPlanNode();
		node_start.pt = start;

		MKPlanNode node_end = new MKPlanNode();
		node_end.pt = end;

		mSearch.transitSearch("西安", node_start, node_end);
		Log.i("axlecho", "p2p_bybus by point ok");
	}

	public void p2p_bybus(String start,String end){
		MKSearch mSearch = new MKSearch();
		mSearch.init(MapBase.Instance(null).getMapManager(),
				new resultListener());

		MKPlanNode node_start = new MKPlanNode();
		node_start.name = start;
//		"西安电子科技大学(南校区)";

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
		MKSearch mSearch = new MKSearch();
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
		mparent.map_view.refresh();
	}

	public void cover_point(GeoPoint point) {
//		GeoPoint point = new GeoPoint((int) (point_x * 1e6),(int) (point_y * 1e6));
 
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
		mparent.map_view.refresh();
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
		mparent.map_view.refresh();
	}
	
	public void cover_pic(GeoPoint pos ,int pic_id,String title){
		Drawable marker = mparent.getResources().getDrawable(pic_id); //得到需要标在地图上的资源
//		GeoPoint pos = new GeoPoint((int)(point_x * 1e6),(int)(point_y * 1e6));
		
		if(marker == null)Log.e("axlecho","marker is null");
		
		OverlayItem item= new OverlayItem(pos,title,"test");
		item.setMarker(marker);
		items.additem(item);
		
		mparent.map_view.refresh();//刷新地图
		Log.i("axlecho","cover_pic ok.");
	}
	
	//points_list.size()　表示有几段路线
	//points_list.get(i).size()　表示第i段路线有几个点
	private void cover_lines(ArrayList<ArrayList<GeoPoint>> points_list){
		
		Log.i("axlecho",String.valueOf(points_list.size()));
		
		int room = 0;
		for(int i = 0;i < points_list.size();i ++){
			room += points_list.get(i).size();
		}
		Geometry lineGeometry = new Geometry();
		GeoPoint[] linePoints = new GeoPoint[room];
		
		int count = 0;
		for(int i = 0;i < points_list.size();i ++){
			for(int j = 0;j < points_list.get(i).size();j ++){
			linePoints[count] = points_list.get(i).get(j);
			count ++;
			}
			
//			cover_point(linePoints[count - 1].getLatitudeE6() / 1000000.0, linePoints[count - 1].getLongitudeE6() / 1000000.0);
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
		mparent.map_view.refresh();
	}
	
	private class resultListener implements MKSearchListener {

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
			RouteOverlay routeOverlay = new RouteOverlay(mparent,
					mparent.map_view);
			routeOverlay.setData(result.getPlan(0).getRoute(0));
			mparent.map_view.getOverlays().add(routeOverlay);
			mparent.map_view.refresh();
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
			
			// 此处仅展示一个方案作为示例
			transit_routeOverlay.setData(result.getPlan(0));
			mparent.map_view.refresh();
			
			Log.i("axlecho", "i get here.");
			
			//0413-10:55修改 这样子可以获取到两条路线
			int num_line = result.getPlan(0).getNumLines() - 1;
			Log.i("fydx", "需要倒" + String.valueOf(result.getPlan(0).getNumLines() - 1)
					            + "次车");
			String tmp=null;
			 if (num_line == 0) {
				 tmp = "从 "
				            + result.getPlan(0).getLine(0).getGetOnStop().name
				             + " " + result.getPlan(0).getLine(0).getTip();
				       } else {
				  tmp = "从"
				             + result.getPlan(0).getLine(0).getGetOnStop().name
				              + result.getPlan(0).getLine(0).getTip() + "\n再"
				             + result.getPlan(0).getLine(1).getTip();
				       }
			 
			//String tmp = result.getPlan(0).getLine(0).getTip();
			Log.i("axlecho", tmp);

			mparent.src_pt = result.getPlan(0).getStart();
			mparent.current_pt = result.getPlan(0).getEnd();
			
			cover_pic(mparent.src_pt,R.drawable.icon_marka,"起点");
			cover_pic(mparent.current_pt,R.drawable.icon_marka,"终点");
			
			if (mparent.tex_tip != null)
				mparent.tex_tip.setText(tmp);
			
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mparent.map_view.getController().zoomToSpan(transit_routeOverlay.getLatSpanE6(), transit_routeOverlay.getLonSpanE6());
			mparent.map_view.getController().animateTo(result.getEnd().pt);

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int errorcode) {

			if (result == null) {
				Log.i("axlecho", "result is null.");
				return;
			}

			Log.i("axlecho", "onGetWalkingRouteResult is ok");
			
			walk_routeOverlay.setData(result.getPlan(0).getRoute(0));
			mparent.map_view.refresh();
			
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mparent.map_view.getController().zoomToSpan(walk_routeOverlay.getLatSpanE6(), walk_routeOverlay.getLonSpanE6());
			mparent.map_view.getController().animateTo(result.getEnd().pt);

		}
	}

	private class OverItemT extends ItemizedOverlay<OverlayItem> {
		
		private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
		private Context mContext;
		private PopupOverlay pop = null;
		

		private int current = -1;
		OverItemT(Drawable marker, Context context) {
			super(marker);
			
			//点击tip事件
			pop = new PopupOverlay( mparent.map_view,new PopupClickListener() {
                
                @Override
                public void onClickedPopup(int index) {
                	if(current == -1){
                		Log.e("axlecho","onClickedPopup something wrong.");
                	}

					GeoPoint pt = GeoList.get(current).getPoint();
					int pos_x = pt.getLatitudeE6();
					int pos_y = pt.getLongitudeE6();
					Intent intent = new Intent();
					intent.setClass(mparent.getApplicationContext(),SearchActivity.class);
					
					intent.putExtra("search_x", pos_x);
					intent.putExtra("search_y", pos_y);
					mparent.startActivityForResult(intent,0);

					Log.i("axlecho", String.valueOf(index) + "was clicked");
                }
            });
            populate();
			this.mContext = context;
		}
		
		public void additem(OverlayItem item) {
			GeoList.add(item);
			populate();
		}

		public void removeItem(int index) {
			GeoList.remove(index);
			populate();
		}
        
		@Override
		protected OverlayItem createItem(int i) {
			return GeoList.get(i);
		}

		@Override
		public int size() {
			return GeoList.size();
		}

		// 点击气泡事件
		protected boolean onTap(int index) {
			
			View popview = LayoutInflater.from(mContext).inflate(R.layout.popup, null);// 获取要转换的View资源
			TextView TestText = (TextView)popview.findViewById(R.id.test_text);
			TestText.setText(GeoList.get(index).getTitle() + "\n");//将每个点的Title在弹窗中以文本形式显示出来	
			
		   Bitmap popbitmap = convertViewToBitmap(popview);  
		   
			Log.i("axlecho", "buble was clicked index :" + String.valueOf(index));
			current = index;
			Bitmap[] bmps = new Bitmap[1];
//			bmps[0] = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.marker1);
			bmps[0] = popbitmap;
//			bmps[2] = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.marker3);
			pop.showPopup(bmps, GeoList.get(index).getPoint(), 32);
			
			return true;
		}

		//点击其他地方事件
		public boolean onTap(GeoPoint pt, MapView mapView) {
			Log.i("axlecho", "other area was clicked");
			if (pop != null) {
				pop.hidePop();
			}
			super.onTap(pt, mapView);
			return false;
		}
		
		public Bitmap convertViewToBitmap(View view) {
			view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
			view.buildDrawingCache();
			Bitmap bitmap = view.getDrawingCache();

			return bitmap;
		}
	};

	
}
