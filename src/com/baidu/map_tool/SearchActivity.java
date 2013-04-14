package com.baidu.map_tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xdgdg.tripguide_xidian.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class SearchActivity extends Activity {
	private EditText editText;
	private SimpleAdapter mSchedule;
	private ListView listView;
	private List<Map<String, String>> mData = new ArrayList<Map<String, String>>();
	private List<MKPoiInfo> pois = new ArrayList<MKPoiInfo>();
	private int pos_x, pos_y;
	private SeekBar seekBar;
	private int time_hour;
	private TextView textView_time;
	private MKSearch mMKSearch = null;
	private BMapManager mBMapMan = null;
	private Button button, button_cinema, button_ktv, button_rest,
			button_coffee;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMKSearch = new MKSearch();
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("5DD5B539C690BC0AF97D9E69733C1D87C9D70F7E", null);
		mMKSearch.init(mBMapMan, new MySearchListener());
		MKSearch.setPoiPageCapacity(30); // 设置一次可以查询到的poi最大数量
		setContentView(R.layout.activity_search);
		Intent intent = getIntent();
		pos_x = intent.getIntExtra("search_x", 34000000);
		pos_y = intent.getIntExtra("search_y", 108000000);
		Log.e("get pos", pos_x + " " + pos_y);
		listView = (ListView) findViewById(R.id.listView_search);
		editText = (EditText) findViewById(R.id.edittext_search);
		button = (Button) findViewById(R.id.search);
		button_rest = (Button) findViewById(R.id.search_rest);
		button_cinema = (Button) findViewById(R.id.search_cinema);
		button_ktv = (Button) findViewById(R.id.search_ktv);
		button_coffee = (Button) findViewById(R.id.search_coffee);
		button_rest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMKSearch.poiSearchNearBy("餐厅", new GeoPoint((int) (pos_x),
						(int) (pos_y)), 1500);
			}
		});
		button_cinema.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMKSearch.poiSearchNearBy("电影院", new GeoPoint((int) (pos_x),
						(int) (pos_y)), 1500);
			}
		});
		button_ktv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMKSearch.poiSearchNearBy("KTV", new GeoPoint((int) (pos_x),
						(int) (pos_y)), 1500);
			}
		});
		button_coffee.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMKSearch.poiSearchNearBy("咖啡厅", new GeoPoint((int) (pos_x),
						(int) (pos_y)), 1500);
			}
		});

		mData = new ArrayList<Map<String, String>>();
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String type = editText.getText().toString();
				mMKSearch.poiSearchNearBy(type, new GeoPoint((int) (pos_x),
						(int) (pos_y)), 1500);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	public void initListView() {
		mData.clear(); // 清空上次添加的信息
		for (MKPoiInfo poi : pois) {
			Log.i("info", poi.name);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", poi.name);
			map.put("address", poi.address);
			map.put("tel", poi.phoneNum);
			Log.i("poi.name", poi.name);
			Log.i("poi.address", poi.address);
			Log.i("poi.tel", poi.phoneNum);
			mData.add(map);
		}
		mSchedule = new SimpleAdapter(this, // 没什么解释
				mData,// 数据来源
				R.layout.listitem_search,// ListItem的XML实现
				// 动态数组与ListItem对应的子项
				new String[] { "name", "address", "tel" },
				// ListItem的XML文件里面的三个TextView ID
				new int[] { R.id.search_name, R.id.search_address,
						R.id.search_tel });
		// 添加并且显示
		listView.setDividerHeight(0);
		listView.setAdapter(mSchedule);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				init_dialog(); // 仅初始化，未定义传值！
			}
		});
	}

	public class MySearchListener implements MKSearchListener {
		@Override
		public void onGetAddrResult(MKAddrInfo result, int iError) {
			// 返回地址信息搜索结果
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result,
				int iError) {
			// 返回驾乘路线搜索结果
		}

		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			if (error != 0 || res == null) {
				Toast.makeText(SearchActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_LONG).show();
				return;
			}
			pois.clear();
			pois = res.getAllPoi();
			initListView(); // 初始化Listview
			Log.e("poi num", pois.size() + "");
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int iError) {
			// 返回公交搜索结果
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int iError) {
			// 返回步行路线搜索结果
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			// 返回公交车详情信息搜索结果
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult result, int iError) {
			// 返回联想词信息搜索结果
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
		}
	}

	public void init_dialog() {

		LayoutInflater factory = LayoutInflater.from(SearchActivity.this);
		final View dialogView = factory.inflate(R.layout.dialog_time, null);
		seekBar = (SeekBar) dialogView.findViewById(R.id.stoptime);
		textView_time = (TextView) dialogView.findViewById(R.id.textView_time);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				textView_time.setText(String.valueOf(seekBar.getProgress())
						+ "小时");
			}
		});
		new AlertDialog.Builder(SearchActivity.this).setTitle("选择停留时间(小时)")
				.setView(dialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						time_hour = seekBar.getProgress();
						Log.e("stoptime", String.valueOf(time_hour));

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).create().show();
		;

	}
}
