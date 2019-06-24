package com.example.smartpenforboard;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MServiceConnection implements ServiceConnection{
	private BluetoothLEService mService = null; // 蓝牙服务

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		mService= ((BluetoothLEService.LocalBinder) service).getService();
//		this.iBinder=service;
		if (!mService.initialize()) {
			return;
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		
	}
	public BluetoothLEService gBluetoothLEService() {
		
		return mService;
	}

}
