package com.moz.signin.bordercast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetWorkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(networkInfo ==null||!networkInfo.isAvailable()){
			Toast.makeText(context,"当前无网络连接,请检查网络设置" , Toast.LENGTH_SHORT).show();
		}
		//context.unregisterReceiver(this);/*这是动态广播，所以必须在不需要的时候将这个广播手动注销*/
	}

}
