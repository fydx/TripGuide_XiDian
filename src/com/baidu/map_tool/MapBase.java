package com.baidu.map_tool;

import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

public class MapBase {
	private static final String strKey = "96DB1C18C39151F2C10F2131AD20D56A51B7CF9C";
	private BMapManager mBMapManager = null;
	private static MapBase mInstance = null;

	public void terminate() {
		if (mBMapManager != null) {
			mBMapManager.destroy();
			mBMapManager = null;

			if (mInstance != null)
				mInstance = null;
		}
		
	}

	public static MapBase Instance(Context context) {
		Log.i("axlecho", "instance.");
		if (mInstance == null) {
			mInstance = new MapBase(context);
		}
		return mInstance;
	}

	private MapBase(Context context) {
		mBMapManager = new BMapManager(context);
		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			Log.i("axlecho", "BMapManager  初始化错误!");
		}
	}

	public BMapManager getMapManager() {
		return mBMapManager;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Log.i("axlecho", "您的网络出错啦！");
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Log.i("axlecho", "输入正确的检索条件！");
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误:
				Log.i("axlecho", "请在 DemoApplication.java文件输入正确的授权Key！");
			}
		}
	}
}
