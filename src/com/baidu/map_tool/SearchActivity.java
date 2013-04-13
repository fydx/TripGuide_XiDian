package com.baidu.map_tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xdgdg.tripguide_xidian.HotPosition;
import org.xdgdg.tripguide_xidian.R;
import org.xdgdg.tripguide_xidian.DestinationActivity.ImageSimpleAdapter;

import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKPoiInfo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SearchActivity extends Activity {
	private EditText editText;
	private SimpleAdapter mSchedule;
	private ListView listView;
	private List<Map<String, String>> mData;
	private List<MKPoiInfo> pois=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		listView = (ListView)findViewById(R.id.listView_search);
		 editText = (EditText)findViewById(R.id.edittext_search);
		Button button = (Button)findViewById(R.id.search);
		mData= new ArrayList<Map<String, String>>();
		
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String type= editText.getText().toString();
				initListView();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	public void initListView(){
		for(MKPoiInfo poi: pois)
		{
			Log.i("info",poi.name);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", poi.name);
			map.put("distance", poi.address);
			map.put("pic",poi.phoneNum);
			mData.add(map);
			
		}
		mSchedule = new SimpleAdapter(this, // 没什么解释
				mData,// 数据来源
				R.layout.listitem_search,// ListItem的XML实现

				// 动态数组与ListItem对应的子项
				new String[] { "name", "address",
						"tel"},

				// ListItem的XML文件里面的两个TextView ID
				new int[] { R.id.search_name, R.id.search_address,
						R.id.search_tel});
		// 添加并且显示
		listView.setDividerHeight(0);
		listView.setAdapter(mSchedule);
	}

}
