package org.xdgdg.tripguide_xidian;

import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends Activity {
	private FinalDb db_detail;
	private HotPosition pos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		  Button button_setButton=(Button)findViewById(R.id.button_set);
		  TextView nameTextView=(TextView)findViewById(R.id.detail_name);
		  TextView distTextView=(TextView)findViewById(R.id.detail_distance);
		  TextView timeTextView= (TextView)findViewById(R.id.detail_time);
		  TextView detailTextView=(TextView)findViewById(R.id.pos_detail);
		  TextView areaTextView=(TextView)findViewById(R.id.detail_area);
		  ImageView img = (ImageView)findViewById(R.id.pic_crop);
		  Intent intent= getIntent();
		  int id= intent.getIntExtra("PosfromMain", 0);
		  Log.e("id",String.valueOf(id));
		  db_detail=FinalDb.create(this);
		  pos = db_detail.findById(id, HotPosition.class);
		  Log.e("name", pos.getNameString());
		  nameTextView.setText(pos.getNameString());
		  distTextView.setText("距离西电\n"+pos.getDistanceString());
		  timeTextView.setText("到达需用时(公交)\n"+pos.getTimeString());
		  detailTextView.setText(pos.getIntroString());
		  areaTextView.setText(pos.getAreaString());
		  Drawable small_img= getResources().getDrawable(pos.getDrawable_small());
		  img.setImageDrawable(small_img);
		  button_setButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

}
