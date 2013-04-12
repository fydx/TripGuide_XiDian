package org.xdgdg.tripguide_xidian;

import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Welcome extends Activity {
	private FinalDb db_welcome;
	private SharedPreferences sp;
	private int count1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setContentView(R.layout.activity_welcome);
		
		// 判断是否为第一次进入程序
		sp = getSharedPreferences("COUNT", Context.MODE_PRIVATE);
		count1 = sp.getInt("COUNT", 0);
		Editor e = sp.edit();
		e.putInt("COUNT", ++count1);
		e.commit();
		// 判断结束
		// 设置timer
		final Intent intent2 = new Intent(Welcome.this, MainActivity.class);
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				startActivity(intent2);
				finish(); // 结束
			}
		};
		if (count1 > 1) // 如果不是第一次进入程序，直接跳转到下一个界面
		{
			timer.schedule(task, 1000 * 2);
			Log.i("Firstboot","false");
		}

		else 
		{
			Toast.makeText(getApplicationContext(), "第一次启动程序，正在导入数据，请稍后", Toast.LENGTH_SHORT).show();
			db_welcome = FinalDb.create(this); //打开数据库
			timer.schedule(task, 1000 * 4);
			db_init();
			db_my_init();
			Log.i("Firstboot","true");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}
	private void db_init() 
	{
		
		String name[]={
				"东大街","小寨"
            };
		String distance[]=
			{
				"22.1km","16.9km"
			};
		String intro[]={
				"这地方和牲口有关系？不要被它的名字欺骗了，骡马市是唐长安时的叫法，现在他可是日均吸引西安潮妹" +
				"子上万人的步行街。是的，它很潮。除了逛街的人外，每天还有许多单车爱好者聚集于此，为它增添了别样" +
				"的时尚气息，这里是市中心，这里是骡马市。",
				"潮，这一个字就可以描述小寨，如果说国贸大厦为小寨商圈的繁荣打下根基，那么，" +
				"大学生们就是为小寨的“潮”输入了源源不断的血液。由于位于西安南郊与各个大学老校区很近，" +
				"这里的商品可以说就是为大学生们准备的，这里是西安唯一可以与东大街匹敌的商圈。无论是看妹子、" +
				"看帅哥还是购物、吃饭，来这里没错的。"


		};
		String time[]={
				"1小时40分钟","1小时20分钟"
		};
		String area[]={
				"碑林区","雁塔区"
		};
		double longti[]={
				108.9485,108.9467
		};
		double lati[]={
				34.2594,34.2230
		};
		int path[]={
			R.drawable.dongdajie,R.drawable.xiaozhai
		};
		int path_small[]={
				R.drawable.cropdongdajie,R.drawable.cropxiaozhai
		};
		for(int i=0;i<name.length;i++)
		{
			HotPosition temp_pos =new HotPosition(name[i], distance[i], intro[i], time[i], area[i],longti[i], lati[i],path[i],path_small[i]);
			Log.i("hot",temp_pos.getNameString()
					);
			db_welcome.save(temp_pos);
		}
		Log.e("导入", "导入结束");
	}
	private void db_my_init(){
		String dateString = "2013-04-12";
		String detailString= "916";
		route route= new route(dateString, detailString);
		db_welcome.save(route);
	}
   
}