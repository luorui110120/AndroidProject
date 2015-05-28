package com.kd.lockscreen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class LockScreenWidget extends AppWidgetProvider{

	final String mPerfName = "com.silenceburn.MyColorNoteConf";  
	  
    @Override  
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,  
            int[] appWidgetIds) {  
        // TODO Auto-generated method stub  
        final int N = appWidgetIds.length;  
        for (int i = 0; i < N; i++) {  
            int appWidgetId = appWidgetIds[i];  
            Log.i("myLog", "this is [" + appWidgetId + "] onUpdate!");  
  
        }  
        Intent intent=new Intent(context, MyLock.class);
//        Intent intent=new Intent(context, MyLockConf.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, 0);
        //RemoteViews类描述了一个View对象能够显示在其他进程中，可以融合layout资源文件实现布局。
        //虽然该类在android.widget.RemoteViews而不是appWidget下面,但在Android Widgets开发中会经常用到它，
        //主要是可以跨进程调用(appWidget由一个服务宿主来统一运行的)。
        RemoteViews myRemoteViews = new RemoteViews(context.getPackageName(), R.layout.my_lockscreen_widget);
        //myRemoteViews.setImageViewResource(R.id.imageView, R.drawable.png1);//设置布局控件的属性（要特别注意）
        myRemoteViews.setOnClickPendingIntent(R.id.my_lock_img, pendingIntent);
        ComponentName myComponentName = new ComponentName(context, LockScreenWidget.class);
        //负责管理AppWidget，向AppwidgetProvider发送通知。提供了更新AppWidget状态，获取已经安装的Appwidget提供信息和其他的相关状态
        AppWidgetManager myAppWidgetManager = AppWidgetManager.getInstance(context);
        myAppWidgetManager.updateAppWidget(myComponentName, myRemoteViews);
    }  
  
    @Override  
    public void onDeleted(Context context, int[] appWidgetIds) {  
        // TODO Auto-generated method stub  
        final int N = appWidgetIds.length;  
        for (int i = 0; i < N; i++) {  
            int appWidgetId = appWidgetIds[i];  
            Log.i("myLog", "this is [" + appWidgetId + "] onDelete!");  
        }  
    } 
    @Override 
    public void onReceive(Context context, Intent intent)
    {
    	super.onReceive(context, intent);
    	Log.i("myLog", "this is [] onReceive!");  
    }


}
