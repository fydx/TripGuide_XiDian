package org.xdgdg.tripguide_xidian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DestinationActivity extends Activity {
	//private List<Bitmap> pics=null;
	//private ImageView img;
	private SimpleAdapter mSchedule;
	private List<HotPosition> positions=null;
	//private TextView distanceTextView,nameTextView;
	private FinalDb db;
	private List<Map<String, Object>> mData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_destination);
		ListView listView= (ListView)findViewById(R.id.listView_main);
		/*img=(ImageView) findViewById(R.id.picture_pos);
		distanceTextView=(TextView)findViewById(R.id.distance);
		nameTextView= (TextView)findViewById(R.id.position_name);*/
		//positions= new  ArrayList<HotPosition>();
		//数据库查询
		db=FinalDb.create(this);
		positions = db.findAll(HotPosition.class);
		Log.e("pos size",String.valueOf(positions.size()));
		//Log.e("pos1", positions.get(0).getNameString());
		
		Resources res = getResources();
		
		mData= new ArrayList<Map<String, Object>>();
		for(HotPosition pos: positions)
		{
			Log.i("info",pos.getNameString());
			HashMap<String, Object> map = new HashMap<String, Object>();
			Bitmap bm= BitmapFactory.decodeResource(res, pos.getDrawable_id());
		//	int width=250;
		//	int height=bm.getHeight();
		//	Bitmap resizedbm=Bitmap.createBitmap(bm,0,0, width, height);
			map.put("name", pos.getNameString());
			map.put("distance", "距离西电"+pos.getDistanceString());
			map.put("pic",bm);
			mData.add(map);
			
		}
		mSchedule = new ImageSimpleAdapter(this, // 没什么解释
				mData,// 数据来源
				R.layout.listitem_main,// ListItem的XML实现

				// 动态数组与ListItem对应的子项
				new String[] { "name", "distance",
						"pic"},

				// ListItem的XML文件里面的两个TextView ID
				new int[] { R.id.position_name, R.id.distance,
						R.id.picture_pos});
		// 添加并且显示
		listView.setDividerHeight(0);
		listView.setAdapter(mSchedule);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {  
		        Intent intent = new Intent(getApplicationContext(), DetailActivity.class); 
		        intent.putExtra("PosfromMain",positions.get(position).getId() );
		        startActivity(intent);   
		     }  
		});
		
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.destination, menu);
		return true;
	}
	//listview 加载的adapter
	public class ImageSimpleAdapter extends SimpleAdapter {
		private int[] mTo;
		private String[] mFrom;
		private ViewBinder mViewBinder;
		private List<? extends Map<String, ?>> mData;
		private int mResource;
		private int mDropDownResource;
		private LayoutInflater mInflater;

		public ImageSimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			mTo = to;
			mFrom = from;
			mData = data;
			mResource = mDropDownResource = resource;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return createViewFromResource(position, convertView, parent, mResource);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return createViewFromResource(position, convertView, parent,
					mDropDownResource);
		}

		private View createViewFromResource(int position, View convertView,
				ViewGroup parent, int resource) {
			View v;
			if (convertView == null) {
				v = mInflater.inflate(resource, parent, false);
			} else {
				v = convertView;
			}

			bindView(position, v);

			return v;
		}

		private void bindView(int position, View view) {
			final Map dataSet = mData.get(position);
			if (dataSet == null) {
				return;
			}

			final ViewBinder binder = mViewBinder;
			final String[] from = mFrom;
			final int[] to = mTo;
			final int count = to.length;

			for (int i = 0; i < count; i++) {
				final View v = view.findViewById(to[i]);
				if (v != null) {
					final Object data = dataSet.get(from[i]);
					String text = data == null ? "" : data.toString();
					if (text == null) {
						text = "";
					}

					boolean bound = false;
					if (binder != null) {
						bound = binder.setViewValue(v, data, text);
					}

					if (!bound) {
						if (v instanceof Checkable) {
							if (data instanceof Boolean) {
								((Checkable) v).setChecked((Boolean) data);
							} else if (v instanceof TextView) {
								// Note: keep the instanceof TextView check at the
								// bottom of these
								// ifs since a lot of views are TextViews (e.g.
								// CheckBoxes).
								setViewText((TextView) v, text);
							} else {
								throw new IllegalStateException(v.getClass()
										.getName()
										+ " should be bound to a Boolean, not a "
										+ (data == null ? "<unknown type>"
												: data.getClass()));
							}
						} else if (v instanceof TextView) {
							// Note: keep the instanceof TextView check at the
							// bottom of these
							// ifs since a lot of views are TextViews (e.g.
							// CheckBoxes).
							setViewText((TextView) v, text);
						} else if (v instanceof ImageView) {
							if (data instanceof Integer) {
								setViewImage((ImageView) v, (Integer) data);
							} else if (data instanceof Bitmap) {// 仅仅添加这一步
								setViewImage((ImageView) v, (Bitmap) data);
							} else {
								setViewImage((ImageView) v, text);
							}
						} else {
							throw new IllegalStateException(
									v.getClass().getName()
											+ " is not a view that can be bounds by this SimpleAdapter");
						}
					}
				}
			}
		}

		/**
		 * 添加这个方法来处理Bitmap类型参数
		 * 
		 * @param v
		 * @param bitmap
		 */
		public void setViewImage(ImageView v, Bitmap bitmap) {
			v.setImageBitmap(bitmap);
		}

 }
}
