package org.xdgdg.tripguide_xidian;

import android.R.string;

public class HotPosition {
	int id;             //主键，for finalDb
   String nameString;   // 地点名字
   String distanceString; //距离
   String introString;   //介绍信息
   String timeString;    //到达该地所需时间
   String detail_posString; //ַ地点详情
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
public String getDetail_posString() {
	return detail_posString;
}
public void setDetail_posString(String detail_posString) {
	this.detail_posString = detail_posString;
}
}
