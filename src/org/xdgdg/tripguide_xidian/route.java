package org.xdgdg.tripguide_xidian;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.search.MKPoiInfo;


public class route {
	int id;
	String dateString;
	String route_detail;
	String poi_x,poi_y;
	String poi_name;
	public String getPoi_x() {
		return poi_x;
	}
	public void setPoi_x(String poi_x) {
		this.poi_x = poi_x;
	}
	public String getPoi_y() {
		return poi_y;
	}
	public void setPoi_y(String poi_y) {
		this.poi_y = poi_y;
	}
	public String getPoi_name() {
		return poi_name;
	}
	public void setPoi_name(String poi_name) {
		this.poi_name = poi_name;
	}
	public route(){
		
	}
	public route(String date_1,String routeString) 
	{
		this.dateString=date_1;
		this.route_detail=routeString;
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
	public String getRoute_detail() {
		return route_detail;
	}
	public void setRoute_detail(String route_detail) {
		this.route_detail = route_detail;
	}
	public String poi_xToString(List<MKPoiInfo> poiInfo)
	{
		poi_x="";
		for(MKPoiInfo info:poiInfo)
		{
			poi_x+=Integer.toString(info.pt.getLatitudeE6())+"#";
		}
		return poi_x;
	}
	public List<Integer> poi_xToList(String poi_xString)
	{
		List<Integer> poi_x=new ArrayList<Integer>();
		int temp=0;
		int len=poi_xString.length();
		for(int i=0;i<len;i++)
		{
			char c=poi_xString.charAt(i);
			if(c!='#')
			{
				temp=temp*10+c-'0';
			}
			else
			{
				poi_x.add(temp);
				temp=0;
			}
		}
		return poi_x;
	}
	public String poi_yToString(List<MKPoiInfo> poiInfo)
	{
		poi_y="";
		for(MKPoiInfo info:poiInfo)
		{
			poi_y+=Integer.toString(info.pt.getLongitudeE6())+"#";
		}
		return poi_y;
	}
	public List<Integer> poi_yToList(String poi_yString)
	{
		List<Integer> poi_y=new ArrayList<Integer>();
		int temp=0;
		int len=poi_yString.length();
		for(int i=0;i<len;i++)
		{
			char c=poi_yString.charAt(i);
			if(c!='#')
			{
				temp=temp*10+c-'0';
			}
			else
			{
				poi_y.add(temp);
				temp=0;
			}
		}
		return poi_y;
	}
	public String poi_nameToString(List<MKPoiInfo> poiInfo)
	{
		String poi_name="";
		for(MKPoiInfo info:poiInfo)
		{
			poi_name+=info.name+"#";
		}
		return poi_name;
	}
	public List<String> poi_name_ToList(String poi_name)
	{
		List<String> poi_nameList=new ArrayList<String>();
		int len=poi_name.length();
		String temp="";
		for(int i=0;i<len;i++)
		{
			char c=poi_name.charAt(i);
			if(c!='#')
			{
				temp+=c;
			}
			else
			{
				poi_nameList.add(temp);
				temp="";
			}
		}
		return poi_nameList;
	}
}
