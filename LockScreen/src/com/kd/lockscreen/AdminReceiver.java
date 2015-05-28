package com.kd.lockscreen;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class AdminReceiver extends DeviceAdminReceiver {

    public void onEnabled(Context context, Intent intent) {
       // Log.d(LOG_TAG, "MyAdmin enabled");
	}

	public void onDisabled(Context context, Intent intent) {
	      //  Log.d(LOG_TAG, "MyAdmin disabled");
	}

}
