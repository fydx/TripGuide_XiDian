package org.xdgdg.tripguide_xidian;


public class route {
	int id;
	String dateString;
	String route_detail;
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
	
}
