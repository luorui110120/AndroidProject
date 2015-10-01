package com.swimmi.windnote;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Welcome extends Activity {

	private LinearLayout welcome;		//布局
	private TextView quoteTxt;			//引言标签
	private int color;

	private SharedPreferences sp;

	private Boolean needKey=true;		//是否需要密码
	private SQLiteDatabase wn;
	private Handler welcomeHand;		//欢迎页停留
	private Runnable welcomeShow;
	
	private String quote;
	static public Welcome instance;
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.welcome);
	    instance = this;
	   // wn=Database(R.raw.windnote);
	    wn=DatabaseHelper.Database(this, R.raw.windnote);
	    sp = getSharedPreferences("setting", 0);
	    String content=getResources().getString(R.string.hello_world);		//引言内容
	    String author=getResources().getString(R.string.app_name);			//引言作者
	    String type=getResources().getString(R.string.app_name);			//引言类型
	    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	    if(!sp.getString("today","2012-12-21").equals(sdf.format(new Date())))		//控制每天显示一则引言
	    {
		    Cursor cursor=wn.rawQuery("select * from quotes order by q_count,id limit 1", null);
		    if(cursor.moveToFirst())
		    {
		    	content=cursor.getString(cursor.getColumnIndex("q_content"));
		    	author=cursor.getString(cursor.getColumnIndex("q_author"));
		    	type=cursor.getString(cursor.getColumnIndex("q_type"));
				sp.edit().putString("q_content",content).commit();
				sp.edit().putString("q_author", author).commit();
				sp.edit().putString("q_type", type).commit();
		    	quote=content;
		    	int id=cursor.getInt(cursor.getColumnIndex("id"));
		    	wn.execSQL("update quotes set q_count=q_count+1 where id="+id);
		    	sp.edit().putString("today", sdf.format(new Date())).commit();
		    }
		    cursor.close();
	    }
	    else
	    {
	    	content=sp.getString("q_content", "");
	    	author=sp.getString("q_author", "");
	    	type=sp.getString("q_type", "");
	    	quote=content;
	    }
	    
	    color=sp.getInt("color", getResources().getColor(R.color.blue));
		welcome=(LinearLayout)findViewById(R.id.welcome);
		welcome.setBackgroundColor(color);
		welcome.setOnClickListener(new OnClickListener(){		//点击屏幕跳过引言
			@Override
			public void onClick(View arg0) {
				welcome();
	        	welcomeHand.removeCallbacks(welcomeShow);
			}
		});
		quoteTxt=(TextView)findViewById(R.id.quote_txt);
		quoteTxt.setTextColor(color);
		quoteTxt.setText(content+"\r\n\r\nby "+author);
        
		welcomeHand = new Handler();
		welcomeShow=new Runnable()
	    {
	        @Override
	        public void run()
	        {  
	        	welcome();
	        }
	    };
		//welcomeHand.postDelayed(welcomeShow, quote.length() * 100); 
	    welcomeHand.postDelayed(welcomeShow, 100); 
	}
	private void welcome(){		//欢迎界面
//		Intent data=getIntent();
//    	needKey=data.getBooleanExtra("needKey", true);
//    	if(needKey&&sp.contains("key"))
//    		enterKey();
		String strPasswd = DatabaseHelper.getPasswdHash(this);
		if(strPasswd.length() > 0)
		{
			Passwd.enterKey(this);
		}
       	else
    	{
            Intent intent=new Intent(Welcome.this,Main.class);
            startActivity(intent);
            finish();
    	}
	}

	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			if(needKey){
				finish();
				System.exit(0);
			}
			else
			{return true;}
		}
		if(keyCode==KeyEvent.KEYCODE_MENU)
		{
			welcome();
        	welcomeHand.removeCallbacks(welcomeShow);
		}
		return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
}
