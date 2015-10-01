package com.swimmi.windnote;



import java.util.HashMap;

import android.os.Bundle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Note extends Activity {

	private LinearLayout note;		//布局
	private TextView titleTxt;		//标题栏
	private EditText noteTxt;		//输入框
	
	private ImageButton backBtn;	//返回
	private ImageButton lockBtn;	//加锁
	private ImageButton deleteBtn;	//删除
	private ImageButton confirmBtn;	//确认
	
	private Dialog delDialog;		//删除对话框
	private Integer s_id;			//记事ID
	private String title;			//标题
	private String content;			//内容
	private Boolean lock;			//加锁状态
	private int color;				//当前皮肤颜色
	private SharedPreferences sp;
	private SQLiteDatabase wn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note); 
		
        //wn=Database(R.raw.windnote);
        wn=DatabaseHelper.Database(this, R.raw.windnote);
        sp = getSharedPreferences("setting", 0);
        color=sp.getInt("color", getResources().getColor(R.color.blue));
        
		note=(LinearLayout)findViewById(R.id.note);
		note.setBackgroundColor(color);
		
        titleTxt=(TextView)findViewById(R.id.title_note);
        noteTxt=(EditText)findViewById(R.id.note_txt); 
        backBtn=(ImageButton)findViewById(R.id.back_btn);
        lockBtn=(ImageButton)findViewById(R.id.lock_btn);
        deleteBtn=(ImageButton)findViewById(R.id.delete_btn);
        confirmBtn=(ImageButton)findViewById(R.id.confirm_btn);
        
        Intent intent=getIntent();		//恢复未保存数据
        @SuppressWarnings("unchecked")
		HashMap<String, Object> map=(HashMap<String, Object>) intent.getSerializableExtra("data");
        title=(String) map.get("title");
        content=(String) map.get("content");
        lock=(Boolean)map.get("lock");
        s_id=(Integer)map.get("id");
        titleTxt.setText(title);
        noteTxt.setText(content);

        setLock(lock);
        ImageButton[] btns={backBtn,lockBtn,deleteBtn,confirmBtn};
		for(ImageButton btn:btns)
			btn.setOnClickListener(click);
	}
	
	public void setLock(Boolean b){		//加锁（解锁）
		focus(noteTxt,b);
        lockBtn.setImageResource(b==true?R.drawable.unlock:R.drawable.lock);
        noteTxt.setTextColor(b==true?getResources().getColor(R.color.darkgray):color);
        noteTxt.setBackgroundResource(b==true?R.color.gray:R.color.white);
	}
	public void focus(EditText view,Boolean b){
		view.setCursorVisible(b);
		view.setFocusable(b);
	    view.setFocusableInTouchMode(b);
	    if(b==true)
	    	view.requestFocus();
	    else
	    	view.clearFocus();
		Spannable text = (Spannable)view.getText();
		Selection.setSelection(text, b?text.length():0);
	}
	private OnClickListener click=new OnClickListener(){
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.back_btn:
				back();
				break;
			case R.id.lock_btn:
				lock=!lock;
				setLock(lock);
				break;
			case R.id.delete_btn:
				delete();
				break;
			case R.id.confirm_btn:
				save();
				break;
			}
		}
		
	};
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event)
	{
		if(keyCode==KeyEvent.KEYCODE_BACK){
			back();
			return true;
		}
		return false;
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
				Toast.makeText(Note.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
				delDialog.dismiss();
				Intent intent=new Intent(Note.this,Main.class);
				startActivity(intent);
				finish();
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
	private void save()			//保存记事
	{
		String n_content=noteTxt.getText().toString().trim();
		Boolean n_lock=lock;
		if(n_content.trim().length()>0)
		{
			
			wn.execSQL("update notes set n_content=?,n_lock=? where id=?",new Object[]{DatabaseHelper.Encrypt(n_content, Main.notePasswd),n_lock,s_id});
			if(!n_content.equals(content))
			{
				Toast.makeText(Note.this, R.string.note_saved, Toast.LENGTH_SHORT).show();
			}
			Intent intent=new Intent(Note.this,Main.class);
			startActivity(intent);
			finish();
		}
		else
			Toast.makeText(Note.this, R.string.note_null, Toast.LENGTH_SHORT).show();
	}

	private void back(){
		Intent intent=new Intent(Note.this,Main.class);
		startActivity(intent);
		finish();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
}
