package com.swimmi.windnote;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	private Dialog menuDialog;		//菜单对话框
	private Dialog delDialog;		//删除对话框
	private GridView menuGrid;		//菜单选项
	private View menuView;			//菜单选项视图
	
	private ImageButton addBtn;		//添加
	private ImageButton menuBtn;	//弹出菜单
	private ImageButton searchBtn;	//搜索
	private ImageButton modeBtn;	//显示模式
	private ImageButton sortBtn;	//排序
	
	private ListView notesLis;		//记事列表
	private GridView notesGrd;		//记事网格
	private TextView titleTxt;		//标题
	private LinearLayout main;		//布局
	private EditText searchTxt;		//搜索框
	private TextView refreshTxt;	//刷新标签
	
	private Integer s_id;			//记事ID
	private boolean sort_desc;		//排序标识
	private boolean mode_list;		//模式标识
	private long exitTime;			//退出时间
	private int color;				//当前皮肤颜色
	private String q_content;		//引言内容
	private String q_author;		//引言作者
	private String q_type;			//引言类型
	private HashMap<Integer,Integer> idMap;		//IDMap
	
	final int ACTION_SKIN=0;	//菜单选项
	final int ACTION_KEY=1;
	final int ACTION_SAY=2;
	final int ACTION_HELP=3;
	final int ACTION_ABOUT=4;
	final int ACTION_EXIT=5;
	private float mx;		//屏幕触点坐标
	private float my;
	
	private ColorPickerDialog cpDialog;		//颜色选择对话框
	private SharedPreferences sp;			//数据存储
	private SQLiteDatabase wn;				//数据库连接
	static public String notePasswd  = "";		//程序密码
	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//wn=Database(R.raw.windnote);
		wn=DatabaseHelper.Database(this, R.raw.windnote);
		sp = getSharedPreferences("setting", 0);
		idMap=new HashMap<Integer, Integer>();		//获取记事ID列表
        color=sp.getInt("color", getResources().getColor(R.color.blue));
		main=(LinearLayout)findViewById(R.id.main);
		main.setBackgroundColor(color);
		
		titleTxt=(TextView)findViewById(R.id.title_main);
		addBtn=(ImageButton)findViewById(R.id.add_btn);
		menuBtn=(ImageButton)findViewById(R.id.menu_btn);
		searchBtn=(ImageButton)findViewById(R.id.search_btn);
		modeBtn=(ImageButton)findViewById(R.id.mode_btn);
		sortBtn=(ImageButton)findViewById(R.id.sort_btn);
		notesLis=(ListView)findViewById(R.id.notes_lis);
		notesLis.setVerticalScrollBarEnabled(true);
		notesGrd=(GridView)findViewById(R.id.notes_grd);
		notesGrd.setVerticalScrollBarEnabled(true);
		@SuppressWarnings("deprecation")
		int width=getWindowManager().getDefaultDisplay().getWidth();	//获取屏幕宽度
		notesGrd.setNumColumns(width/120);			//设置网格布局列数
		
		q_content=sp.getString("q_content", "");
		q_author=sp.getString("q_author", "");
		q_type=sp.getString("q_type", "");
		
		ImageButton[] btns={addBtn,menuBtn,searchBtn,modeBtn,sortBtn};
		for(ImageButton btn:btns)
			btn.setOnClickListener(click);
		
		sort_desc=sp.getBoolean("sort", true);		//获取排序方式
		mode_list=sp.getBoolean("mode", true);		//获取显示模式
		
		menuDialog = new Dialog(this,R.style.dialog);		//自定义菜单
		menuView = View.inflate(this, R.layout.gridmenu, null);
		menuDialog.setContentView(menuView);
		menuDialog.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU)
				dialog.dismiss();
				return false;
			}
		});
		menuGrid=(GridView)menuView.findViewById(R.id.grid);
		menuGrid.setAdapter(getMenuAdapter());		//设置菜单项
		menuGrid.setOnItemClickListener(itemClick);
		searchTxt=(EditText)findViewById(R.id.search_txt);
		searchTxt.setBackgroundColor(color);
		searchTxt.addTextChangedListener(search);
		searchTxt.setText(sp.getString("word", ""));
		titleTxt.setOnClickListener(click);
		refreshTxt=(TextView)findViewById(R.id.refresh_txt);
		
		Long lastdate=sp.getLong("lastdate", new Date().getTime());		//更新记事保存时间
		long passday=(int)(new Date().getTime()-lastdate)/(3600000*24);
		wn.execSQL("update notes set n_time=n_time-"+passday+" where n_time>0");
		sp.edit().putLong("lastdate",new Date().getTime()).commit();
		showItem(sort_desc,mode_list);
	}
	public OnTouchListener touch = new OnTouchListener(){		//触摸事件（记事显示区内触摸）
		@Override
		public boolean onTouch(View view, MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			switch(e.getAction()){
			case MotionEvent.ACTION_DOWN:
				mx=x;
				my=y;
				break;
			case MotionEvent.ACTION_UP:
				float dx = x-mx;
				float dy = y-my;
				if(dy>30&&dx<30){			//下拉刷新
					refreshTxt.setVisibility(View.VISIBLE);
					showItem(sort_desc,mode_list);
					Handler refreshHand = new Handler();
					Runnable refreshShow=new Runnable()		
				    {
				        @Override
				        public void run()
				        {  
				        	refreshTxt.setVisibility(View.GONE);
				        }
				    };
					refreshHand.postDelayed(refreshShow, 500);
				}
			}
			return false;
		}
	};
	@Override
	public boolean onTouchEvent(MotionEvent e){			//触摸事件（记事显示区外触摸）
		float x = e.getX();
		float y = e.getY();
		switch(e.getAction()){
		case MotionEvent.ACTION_DOWN:
			mx=x;
			my=y;
			break;
		case MotionEvent.ACTION_UP:
			float dx = x-mx;
			float dy = y-my;
			if(dy>30&&dx<30){
				refreshTxt.setVisibility(View.VISIBLE);
				showItem(sort_desc,mode_list);
				Handler refreshHand = new Handler();
				Runnable refreshShow=new Runnable()
			    {
			        @Override
			        public void run()
			        {  
			        	refreshTxt.setVisibility(View.GONE);
			        }
			    };
				refreshHand.postDelayed(refreshShow, 500);
			}
		}
		return true;
	}


	private void editKey(){			//修改密码
		View keyView = View.inflate(this, R.layout.editkey, null);
		final Dialog dialog=new Dialog(this,R.style.dialog);
		dialog.setContentView(keyView);
		Button resetBtn=(Button)keyView.findViewById(R.id.reset_key);
		Button cancelBtn=(Button)keyView.findViewById(R.id.cancel_key);
		resetBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Passwd.resetKey(Main.this);
				dialog.dismiss();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Passwd.cancelKey(Main.this);
				dialog.dismiss();
			}
		});
		dialog.show();
	}


	private void showItem(Boolean desc,Boolean list){		//显示记事
		String word=searchTxt.getText().toString().trim();
		SimpleAdapter adapter = new SimpleAdapter(Main.this,getData(desc,word),list?R.layout.listitem:R.layout.griditem,
				new String[]{"id","title","content","time","count","lock","postdate"},
				new int[]{R.id.id,R.id.title,R.id.content,R.id.time,R.id.count,R.id.lock,R.id.postdate});
		sortBtn.setImageResource(desc?R.drawable.asc:R.drawable.desc);
		modeBtn.setImageResource(list?R.drawable.grid:R.drawable.list);
		if(list)
		{	
			notesLis.setVisibility(View.VISIBLE);
			notesGrd.setVisibility(View.GONE);
			notesLis.setAdapter(adapter);		//生成记事列表
			notesLis.setOnItemClickListener(new OnItemClickListener(){		//点击记事事件
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					ListView listView =(ListView)parent;		
					@SuppressWarnings("unchecked")
					HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(position);  
					wn.execSQL("update notes set n_count=n_count-1 where n_count>0 and id="+idMap.get(position));	//更新可浏览次数
					Intent intent=new Intent(Main.this,Note.class);
					intent.putExtra("data", map);
					startActivity(intent);
					finish();
				}
			});
			notesLis.setOnTouchListener(touch);
			notesLis.setOnItemLongClickListener(longClick);			//记事长按事件
			titleTxt.setText(getResources().getString(R.string.app_name)+"\t["+notesLis.getCount()+"]");
		}
		else{
			notesGrd.setVisibility(View.VISIBLE);
			notesLis.setVisibility(View.GONE);
			notesGrd.setAdapter(adapter);		//生成记事网格
			notesGrd.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					GridView gridView =(GridView)parent;		
					@SuppressWarnings("unchecked")
					HashMap<String, Object> map = (HashMap<String, Object>) gridView.getItemAtPosition(position);  
					wn.execSQL("update notes set n_count=n_count-1 where n_count>0 and id="+idMap.get(position));
					Intent intent=new Intent(Main.this,Note.class);
					intent.putExtra("data", map);
					startActivity(intent);
					finish();
				}
			});
			notesGrd.setOnTouchListener(touch);
			notesGrd.setOnItemLongClickListener(longClick);			//记事长按事件
			titleTxt.setText(getResources().getString(R.string.app_name)+"\t["+notesGrd.getCount()+"]");
		}
	}
	private void chooseColor(){			//选择皮肤
		Dialog dialog=new Dialog(this,R.style.dialog);
		View colorView = View.inflate(this, R.layout.gridmenu, null);
		dialog.setContentView(colorView);
		GridView colorGrid=(GridView)colorView.findViewById(R.id.grid);
		colorGrid.setNumColumns(2);
		colorGrid.setAdapter(getColorAdapter());		//设置皮肤选项
		colorGrid.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) 
			{
				if(getResources().getColor(My.colors[position])!=color)
				{
					if(position<My.colors.length-1)			//选择了当前皮肤
					{						
						sp.edit().putInt("color", getResources().getColor(My.colors[position])).commit();
						Intent intent=new Intent(Main.this,Welcome.class);
						intent.putExtra("needKey", false);
						startActivity(intent);
						finish();
					}
					else if(position==My.colors.length-1)		//选择了新的皮肤
					{
						cpDialog = new ColorPickerDialog(Main.this, color,   
		                        getResources().getString(R.string.word_confirm),   
		                        new ColorPickerDialog.OnColorChangedListener() { 
		                    @Override  
		                    public void colorChanged(int c) 
		                    {  
								sp.edit().putInt("color", c).commit();
								Intent intent=new Intent(Main.this,Welcome.class);
								intent.putExtra("needKey", false);
								startActivity(intent);
								finish();
		                    }
		                });
						cpDialog.getWindow().setBackgroundDrawableResource(R.drawable.list_focused);
		                cpDialog.show();  
					}
				}
				else
				{
					Toast.makeText(Main.this, R.string.now_skin, Toast.LENGTH_SHORT).show();
				}
			}
		});
		dialog.show();
	}
	private SimpleAdapter getColorAdapter()			//获取皮肤列表
	{
		SimpleAdapter adapter = new SimpleAdapter(this,getColor(),R.layout.menuitem,
				new String[]{"txt"},
				new int[]{R.id.item_txt});
		return adapter;
	}
	private SimpleAdapter getMenuAdapter()			//获取菜单列表
	{
		SimpleAdapter adapter = new SimpleAdapter(this,getMenu(),R.layout.menuitem,
				new String[]{"img","txt"},
				new int[]{R.id.item_img,R.id.item_txt});
		return adapter;
	}
	private List<Map<String, Object>> getColor() {			//获取颜色列表
		String[] txts=My.cs;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(int i=0;i<txts.length;i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("txt", txts[i]);
			list.add(map);
		}
		return list;
	}
	private List<Map<String, Object>> getMenu() {			//获取菜单
		int[] imgs={R.drawable.skin,R.drawable.key,R.drawable.say,R.drawable.help,R.drawable.about,R.drawable.exit};
		int[] txts={R.string.action_skin,R.string.action_key,R.string.action_say,R.string.action_help,R.string.action_about,R.string.action_exit};
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(int i=0;i<imgs.length;i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("img", imgs[i]);
			map.put("txt", getResources().getString(txts[i]));
			list.add(map);
		}
		return list;
	}
	private List<Map<String, Object>> getData(Boolean desc, String word) {		//获取记事数据
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Cursor cursor=wn.rawQuery("select id,n_title,n_content,n_time,n_count,n_lock,julianday(date('now','localtime'))-julianday(date(n_postdate)) as n_postday from notes where n_time!=0 and n_count!=0 order by n_postdate "+(desc!=true?"":"desc"), null);
		if(word.length()>0)
		{
			if(notePasswd.length() > 0)
			{
				cursor=wn.rawQuery("select id,n_title,n_content,n_time,n_count,n_lock,julianday(date('now','localtime'))-julianday(date(n_postdate)) as n_postday from notes where n_time!=0 and n_count!=0 and (n_title||'`'||n_postdate||'`'||n_postday) like '%"+word+"%' order by n_postdate "+(desc!=true?"":"desc"), null);
			}
			else
			{
				cursor=wn.rawQuery("select id,n_title,n_content,n_time,n_count,n_lock,julianday(date('now','localtime'))-julianday(date(n_postdate)) as n_postday from notes where n_time!=0 and n_count!=0 and (n_title||'`'||n_content||'`'||n_postdate||'`'||n_postday) like '%"+word+"%' order by n_postdate "+(desc!=true?"":"desc"), null);
			}
		}
		if(word.equals("#all"))
			cursor=wn.rawQuery("select id,n_title,n_content,n_time,n_count,n_lock,julianday(date('now','localtime'))-julianday(date(n_postdate)) as n_postday from notes order by n_postdate "+(desc!=true?"":"desc"), null);
		sp.edit().putString("word", word).commit();
		int pos=0;
		while(cursor.moveToNext())
		{
			int n_id=cursor.getInt(cursor.getColumnIndex("id"));
			idMap.put(pos, n_id);
			pos+=1;
			//String n_title=DatabaseHelper.getCursorDecryptString(cursor, "n_title");
			String n_title=cursor.getString(cursor.getColumnIndex("n_title"));
			String n_content=DatabaseHelper.getCursorDecryptString(cursor, "n_content");
			Integer n_time=cursor.getInt(cursor.getColumnIndex("n_time"));
			Integer n_count=cursor.getInt(cursor.getColumnIndex("n_count"));
			Boolean n_lock=cursor.getInt(cursor.getColumnIndex("n_lock"))>0;
			Integer n_postdate=cursor.getInt(cursor.getColumnIndex("n_postday"));
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", n_id);
			map.put("title", n_title);
			map.put("content", n_content);
			map.put("time", n_time);
			map.put("count", n_count);
			map.put("lock", n_lock);
			map.put("postdate", n_postdate==0?getResources().getString(R.string.word_today):n_postdate+getResources().getString(R.string.word_ago));
			list.add(map);
		}
		cursor.close();
		return list;
	}

	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event)
	{
		if(keyCode==KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			return true;
		}
		return false;
	}
	private OnItemClickListener itemClick=new OnItemClickListener(){			//菜单点击事件
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch(position){
			case ACTION_SKIN:
				chooseColor();
				break;
			case ACTION_KEY:
				if(notePasswd.length() == 0)
					Passwd.setKey(Main.this);
				else
					editKey();
				break;
			case ACTION_SAY:
				say();
				break;
			case ACTION_HELP:
				help();
				break;
			case ACTION_ABOUT:
				about();
				break;
			case ACTION_EXIT:
				//原始退出代码
//				Intent intent = new Intent(Intent.ACTION_MAIN);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				intent.addCategory(Intent.CATEGORY_HOME);
//				startActivity(intent);
				System.exit(0);
				break;
			}
		}
	};
	private void help(){		//帮助
		wn.execSQL("update notes set n_count=1,n_postdate=datetime('now','localtime') where id=1");
		showItem(sort_desc,mode_list);		//显示使用说明
		menuDialog.dismiss();
	}
	private void about(){		//关于
		Dialog aboutDialog=new Dialog(this,R.style.dialog);
		View aboutView = View.inflate(this, R.layout.aboutme, null);
		aboutDialog.setContentView(aboutView);
		aboutDialog.show();
	}
	@SuppressLint("SimpleDateFormat")
	private void say(){			//感悟
		Intent intent= new Intent(Main.this,Add.class);
		Bundle data = new Bundle();
		data.putString("title",getResources().getString(R.string.word_my)+q_type+getResources().getString(R.string.action_say)+"\t\t"+new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		data.putString("content","        "+q_author+getResources().getString(R.string.word_said)+q_content+"\r\n");
		intent.putExtras(data);
		startActivity(intent);
		finish();
	}
	private void delete(){		//删除记事
		View deleteView = View.inflate(this, R.layout.deletenote, null);
		delDialog=new Dialog(this,R.style.dialog);
		delDialog.setContentView(deleteView);
		Button yesBtn=(Button)deleteView.findViewById(R.id.delete_yes);
		Button noBtn=(Button)deleteView.findViewById(R.id.delete_no);
		TextView goneTimeTxt=(TextView)deleteView.findViewById(R.id.gone_time);
		TextView goneCountTxt=(TextView)deleteView.findViewById(R.id.gone_count);
		Cursor cursor=wn.rawQuery("select n_time,n_count from notes where id="+s_id,null);
		while(cursor.moveToNext()){
			int time=cursor.getInt(cursor.getColumnIndex("n_time"));
			int count=cursor.getInt(cursor.getColumnIndex("n_count"));
			String time_txt=time>0?String.valueOf(time):"n";
			String count_txt=count>0?String.valueOf(count):"n";
			goneTimeTxt.setText(R.string.left_txt);
			goneCountTxt.setText(time_txt+getResources().getString(R.string.word_time)+count_txt+getResources().getString(R.string.word_count));
		}
		yesBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				wn.execSQL("delete from notes where id="+s_id);
				delDialog.dismiss();
				showItem(sort_desc,mode_list);
			}
		});
		noBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				delDialog.dismiss();
			}
		});
		delDialog.show();
	}
	private OnItemLongClickListener longClick= new OnItemLongClickListener()		//长按删除
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			s_id=idMap.get(position);
			delete();
			return false;
		}
	};
	private OnClickListener click=new OnClickListener(){			//点击事件监听

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.add_btn:		//新建记事
				Intent intent= new Intent(Main.this,Add.class);
				if(getIntent().hasExtra("title"))
					intent.putExtras(getIntent().getExtras());
				startActivity(intent);
				finish();
				break;
			case R.id.menu_btn:		//菜单
				if (menuDialog == null) 
				{
					menuDialog = new Dialog(Main.this,R.style.dialog);
					menuDialog.show();
				}
				else
				{
					menuDialog.show();
				}
				break;
			case R.id.search_btn:		//搜索
				showHide(searchTxt);
				Add.focus(searchTxt,true);
				break;
			case R.id.mode_btn:			//模式
				mode_list=!mode_list;
				sp.edit().putBoolean("mode", mode_list).commit();
				showItem(sort_desc,mode_list);
				break;
			case R.id.sort_btn:			//排序
				sort_desc=!sort_desc;
				sp.edit().putBoolean("sort", sort_desc).commit();
				showItem(sort_desc,mode_list);
				break;
			case R.id.title_main:		//点击标题栏
				searchTxt.setText("");
				sp.edit().remove("word").commit();
				showItem(sort_desc, mode_list);
			}
		}
	};
	private TextWatcher search=new TextWatcher(){		//搜索事件
		@Override
		public void afterTextChanged(Editable arg0) {
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			showItem(sort_desc, mode_list);
		}
	};
	private void showHide(View view){		//显隐元素
		if(view.getVisibility()==View.VISIBLE)
			view.setVisibility(View.INVISIBLE);
		else
			view.setVisibility(View.VISIBLE);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {		//设置菜单
		menu.removeItem(0);
		if (menuDialog == null) 
		{
			menuDialog = new Dialog(Main.this,R.style.dialog);
			menuDialog.show();
		}
		else 
		{
			menuDialog.show();
		}
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem mi){
		return super.onOptionsItemSelected(mi);
	}
	public static void updatePasswd(String passwd)
	{
		notePasswd = passwd;
	}

}
