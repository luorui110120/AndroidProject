package com.kd.lockscreen;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class MyLockConf extends Activity{
	 int mAppWidgetId;  
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);  
	    
	    Log.i("myLog"," on WidgetConf ... ");  
	      
	    setResult(RESULT_CANCELED);  
	    Intent intent = getIntent();  
        Bundle extras = intent.getExtras();  
        if (extras != null) {  
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,  
                    AppWidgetManager.INVALID_APPWIDGET_ID);  
        }  
  
        // If they gave us an intent without the widget id, just bail.  
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {  
            finish();  
        }  
        RemoteViews views = new RemoteViews(MyLockConf.this
				.getPackageName(), R.layout.my_lockscreen_widget);
        
    //    views.setImageViewResource(R.id.my_lock_img, R.drawable.ic_launcher);
        Intent intent1 = new Intent(MyLockConf.this, MyLock.class);
		intent1.setAction("com_kd_LOCKSCREEN" + mAppWidgetId);
		intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,mAppWidgetId);
		PendingIntent pendingIntent = PendingIntent.getActivity(MyLockConf.this, 0,
				intent1, 0);
        // return OK  
		views.setOnClickPendingIntent(R.id.my_lock_img, pendingIntent);

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(MyLockConf.this);
		appWidgetManager.updateAppWidget(mAppWidgetId, views);

		// return OK
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				mAppWidgetId);

		setResult(RESULT_OK, resultValue);
		finish();
		Log.i("myLog"," on WidgetConf ... END!!");  
	}
}
