package org.xdgdg.tripguide_xidian;


public class HotPosition {
	int id; // 主键，for finalDb
	String nameString; // 地点名字
	String distanceString; // 距离
	String introString; // 介绍信息
	String timeString; // 到达该地所需时间
	String areaString; //在哪个区 
	double longitude; // 经度
	double latitude; // 纬度
	// String path_picString; // 图像路径
	int drawable_id; // drawable id
	int drawable_small;

	public HotPosition() {

	}

	public HotPosition(String name, String distance, String intro, String time,String area,
			double longti, double lati, int path,int path_small) {
		this.nameString = name;
		this.distanceString = distance;
		this.introString = intro;
		this.timeString = time;
		this.areaString= area;
		this.longitude = longti;
		this.latitude = lati;
		this.drawable_id = path;
		this.drawable_small=path_small;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getDrawable_id() {
		return drawable_id;
	}

	public void setDrawable_id(int drawable_id) {
		this.drawable_id = drawable_id;
	}

	public String getAreaString() {
		return areaString;
	}

	public void setAreaString(String areaString) {
		this.areaString = areaString;
	}

	public int getDrawable_small() {
		return drawable_small;
	}

	public void setDrawable_small(int drawable_small) {
		this.drawable_small = drawable_small;
	}
	

}