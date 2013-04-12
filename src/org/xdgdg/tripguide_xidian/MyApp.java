package org.xdgdg.tripguide_xidian;

import android.app.Application;

public class MyApp extends Application{
	private String  busline;

	public String getBusline() {
		return busline;
	}

	public void setBusline(String busline) {
		this.busline = busline;
	}
	 @Override 
	    public void onCreate() { 
	        // TODO Auto-generated method stub 
	        super.onCreate(); 
	        setBusline("busline"); //初始化全局变量        
	    }    
	} 


