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
			Log.i("Firstboot", "false");
		}

		else {
			Toast.makeText(getApplicationContext(), "第一次启动程序，正在导入数据，请稍后",
					Toast.LENGTH_SHORT).show();
			db_welcome = FinalDb.create(this); // 打开数据库
			timer.schedule(task, 1000 * 4);
			db_init();
			db_my_init();
			Log.i("Firstboot", "true");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	private void db_init() {

		String name[] = { "骡马市", "小寨", "大差市", "民乐园", "北大街", "西大街", "南大街",
				"大唐西市", "电子商城", "长安广场" };
		String distance[] = { "22.1km", "16.9km", "22.1km", "22.4km", "20.9km",
				"19.3km", "20.3km", "17.6km", "12.8km", "10.8km" };
		String intro[] = {
				"这地方和牲口有关系？不要被它的名字欺骗了，骡马市是唐长安时的叫法，现在他可是日均吸引西安潮妹"
						+ "子上万人的步行街。是的，它很潮。除了逛街的人外，每天还有许多单车爱好者聚集于此，为它增添了别样"
						+ "的时尚气息，这里是市中心，这里是骡马市。",
				"潮，这一个字就可以描述小寨，如果说国贸大厦为小寨商圈的繁荣打下根基，那么，"
						+ "大学生们就是为小寨的“潮”输入了源源不断的血液。由于位于西安南郊与各个大学老校区很近，"
						+ "这里的商品可以说就是为大学生们准备的，这里是西安唯一可以与东大街匹敌的商圈。无论是看妹子、"
						+ "看帅哥还是购物、吃饭，来这里没错的。",
				"首先中间的字度“chai”，一声哦。它在东大街的东侧，是东大街与解放路的交汇处，距离骡马市很近。"
						+ "最近由于万达的入住，加上本来也是名商圈，变得引人注目。西安正在对这片区域进行旧城改造，"
						+ "相信焕然一新的大差市会让你有别样的体验。",
				"民乐园位于火车站附近，火车等的很无奈？下了火车想转转？民乐园是你的好选择。民乐园商圈位于解放路，火车站南侧。说起民乐园五路口，老西安总有中特别的情结。没错，这里是西安最老的商圈，西安人人皆知的民生百货，图书大厦都在这里。当然最近又有了万达，新玛特，民生新乐汇的加入，这里就是购物狂的终极目的地。",
				"说起北大街，西华门，西安人首先想到的是十字路口的美女交警。这是一个有特色的十字路口哦。"
						+ "当然除了交警，这里还有电信大楼，那是西安第一个塔钟，向东是手机的集散地，向南是改造一新的"
						+ "北大街-钟楼商圈，这里是大家逛钟楼大商圈的又一选择。其实地铁二号线的钟楼站就在这里。",
				"西大街是西安旧城改造的第一条主干道，西大街已经用统一的唐式建筑征服了无数外地游客。"
						+ "另外这里还有各路吃货由衷喜爱的回民街，回民街无论对外地还是本地人来说都是必去之地。"
						+ "而且回民街就在西大街百盛的主入口后面。怎么样，逛街，品美食都不耽搁，这就是西安的生活style。",
				"一条让屌丝们望尘莫及的商业街，西安人认识奢侈品牌就是从南大街开始的，无论是服装，"
						+ "皮包还是名表，这里应有尽有。不过南大街的街景是钟楼周围四条大街中最低调的。"
						+ "看似普通的楼宇中藏有乾坤。去吧，高富帅，白富美~~",
				"大唐西市是城西的一个新兴商业圈，出色的风格设计，完美的包装宣传，"
						+ "加上良好的地段（旁边就是西工大，西北大学你懂的）。所谓天时地利。"
						+ "短短几年就成了西安不可忽视的商圈.",
				"可以说这里是我大西电莘莘学子的福音，如此偏僻的商圈在西安实属罕见。"
						+ "虽然偏僻，这里的商业气氛却依然浓郁，电子市场，超市，KFC一样不少。"
						+ "下午没课又想转转就来这里吧。",
				"长安广场商圈凭借长安区首个大型商圈的名号吸引了广大长安区市民。同时又因为大学城的助威，"
						+ "长安广场上飘荡的早就不仅仅是浓郁的陕西地方气息了，来自五湖四海的大学生门给这里带来了新鲜的感觉。"
						+ "西外，西北大学，西北政法，陕师大的同学都会来这里逛街，西电的学子们注意啦，这些都是文科学校哦。"
		};
		String time[] = { "1小时30分钟", "1小时10分钟", "1小时30分钟", "1小时35分钟",
				"1小时25分钟", "1小时20分钟", "1小时25分钟", "1小时10分钟", "35分钟", "35分钟" };
		String area[] = { "碑林区", "雁塔区", "新城区", "新城区", "新城区", "莲湖区", "碑林区",
				"莲湖区", "雁塔区", "长安区" };
		double longti[] = { 108.955366, 108.9467, 108.9628, 108.9631, 108.9472,
				108.9391, 108.9470, 108.9100, 108.9125, 108.9288 };
		double lati[] = { 34.265701, 34.2230, 34.2594, 34.2666, 34.2646,
				34.2594, 34.2561, 34.2465, 34.2176, 34.1569, };
		int path[] = { R.drawable.dongdajie, R.drawable.xiaozhai,
				R.drawable.dachaishi, R.drawable.minleyuan,
				R.drawable.beidajie, R.drawable.xidajie, R.drawable.nandajie,
				R.drawable.datangxishi, R.drawable.gaoxinsaige,
				R.drawable.changanguangchang };
		int path_small[] = { R.drawable.cropdongdajie, R.drawable.cropxiaozhai,
				R.drawable.cropdachaishi, R.drawable.cropminleyuan,
				R.drawable.cropbeidajie, R.drawable.cropxidajie,
				R.drawable.cropnandajie, R.drawable.cropdatangxishi,
				R.drawable.cropgaoxinsaige, R.drawable.cropchanganguangchang };
		for (int i = 0; i < name.length; i++) {
			HotPosition temp_pos = new HotPosition(name[i], distance[i],
					intro[i], time[i], area[i], longti[i], lati[i], path[i],
					path_small[i]);
			Log.i("hot", temp_pos.getNameString());
			db_welcome.save(temp_pos);
		}
		Log.e("导入", "导入结束");
	}

	private void db_my_init() {
		String dateString = "2013-04-12";

		String pos_xString="34128452#34228870#34231057#34226963#";
	    String pos_yString="108847062#108953122#108952875#108953596#";
	    String nameString="西安电子科技大学(南校区)#小寨#必胜客(小寨餐厅)#竹园村火锅小寨店#";
		route route = new route(dateString, pos_xString,pos_yString,nameString);
		db_welcome.save(route);
	}

}