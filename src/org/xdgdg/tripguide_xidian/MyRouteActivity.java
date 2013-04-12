package org.xdgdg.tripguide_xidian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xdgdg.tripguide_xidian.DestinationActivity.ImageSimpleAdapter;

import com.baidu.map_tool.mapActivity;

import net.tsz.afinal.FinalDb;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MyRouteActivity extends Activity {
	private FinalDb db;
	private List<Map<String, String>> mData;
	private List<route> routes;
	private SimpleAdapter mSchedule;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_route);
		ListView listView = (ListView)findViewById(R.id.listView_myroute);
		db=FinalDb.create(this);//打开数据库
		routes=db.findAll(route.class);
		mData= new ArrayList<Map<String, String>>();
		for(route rou: routes)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("date","录入时间"+rou.getDateString());
			map.put("detail", rou.getRoute_detail());
			mData.add(map);
		}
		mSchedule = new SimpleAdapter(this, // 没什么解释
				mData,// 数据来源
				R.layout.listitem_my,// ListItem的XML实现

				// 动态数组与ListItem对应的子项
				new String[] { "date", "detail",
						},

				// ListItem的XML文件里面的两个TextView ID
				new int[] { R.id.route_date, R.id.route_detail
						});
		// 添加并且显示
		listView.setDividerHeight(0);
		listView.setAdapter(mSchedule);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_route, menu);
		return true;
	}

}
