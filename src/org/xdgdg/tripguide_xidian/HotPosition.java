package org.xdgdg.tripguide_xidian;

import android.R.string;

public class HotPosition {
	int id;             //主键，for finalDb
   String nameString;   // 地点名字
   String distanceString; //距离
   String introString;   //介绍信息
   String timeString;    //到达该地所需时间
   double longitude; //经度
   double latitude;   //纬度
   String path_picString; //图像路径 
   public HotPosition (String name,String distance,String intro,String time,double longti,double lati,String path )
   {
	   this.nameString=name;
	   this.distanceString=distance;
	   this.introString=intro;
	   this.timeString=time;
	   this.longitude = longti;
	   this.latitude= lati;
	   this.path_picString=path;
   }
   public String getNameString() {
	return nameString;
}
public void setNameString(String nameString) {
	this.nameString = nameString;
}
public String getDistanceString() {
	return distanceString;
}
public void setDistanceString(String distanceString) {
	this.distanceString = distanceString;
}
public String getIntroString() {
	return introString;
}
public void setIntroString(String introString) {
	this.introString = introString;
}
public String getTimeString() {
	return timeString;
}
public void setTimeString(String timeString) {
	this.timeString = timeString;
}

}
