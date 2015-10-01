package com.swimmi.windnote;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class Passwd
{
	private static Context m_context;
	private static Dialog keyDialog;
	private static EditText keyTxt, againTxt, newTxt;
	private static String dbPasswd;
	static public boolean isPasswd(String Passwd)
	{
		String okey=keyTxt.getText().toString();
		String passwdHash = DatabaseHelper.GetSHA512Code(okey.getBytes());
		if(passwdHash.equalsIgnoreCase(dbPasswd))
		{
			return true;
		}
		return false;
	}
	static public void enterKey(Context context)			// ‰»Î√‹¬Î
	{
		m_context = context;
		View keyView = View.inflate(m_context, R.layout.cancelkey, null);
		keyDialog=new Dialog(m_context,R.style.dialog);
		keyDialog.setContentView(keyView);
		keyTxt=(EditText)keyView.findViewById(R.id.key_old);
		dbPasswd = DatabaseHelper.getPasswdHash(m_context);
		keyTxt.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String okey=keyTxt.getText().toString();
				if(isPasswd(okey))
				{
					Main.updatePasswd(okey);
					keyDialog.dismiss();
		            Intent intent=new Intent(Welcome.instance, Main.class);
		            m_context.startActivity(intent);
		            Welcome.instance.finish();
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		keyDialog.show();
	}
	static public void setKey(Context context){			//…Ë÷√√‹¬Î
		m_context = context;
		keyDialog=new Dialog(m_context,R.style.dialog);
		View keyView = View.inflate(m_context, R.layout.setkey, null);
		keyDialog.setContentView(keyView);
		keyTxt=(EditText)keyView.findViewById(R.id.key_txt);
		againTxt=(EditText)keyView.findViewById(R.id.again_txt);
		
		keyTxt.addTextChangedListener(change);
		againTxt.addTextChangedListener(change);
		keyDialog.show();
	}
	//√‹¬Î≤Ÿ◊˜
	public static TextWatcher change = new TextWatcher() {		//√‹¬Î…Ë÷√ ¬º˛
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String key=keyTxt.getText().toString();
			String again=againTxt.getText().toString();
			if(key.length() > 0 &&key.equals(again))
			{
				//sp.edit().putString("key", key).commit();
				DatabaseHelper.setPasswd(m_context, again);
				Toast.makeText(m_context, m_context.getResources().getString(R.string.key_success)+key,Toast.LENGTH_LONG).show();
				keyDialog.dismiss();
			}
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	
	public static TextWatcher change2 = new TextWatcher() {		//√‹¬Î–ﬁ∏ƒ ¬º˛
		@Override	
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String old=keyTxt.getText().toString();
			String key=newTxt.getText().toString();
			String keyAgain=againTxt.getText().toString();
			
			if(isPasswd(old)&&key.length() > 0 && key.equals(keyAgain))
			{
				//sp.edit().putString("key", key).commit();
				DatabaseHelper.setPasswd(m_context, keyAgain);
				Toast.makeText(m_context, m_context.getResources().getString(R.string.key_success)+key,Toast.LENGTH_LONG).show();
				keyDialog.dismiss();
			}
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	static public void resetKey(Context context){		//÷ÿ÷√√‹¬Î
		m_context = context;
		keyDialog=new Dialog(m_context,R.style.dialog);
		View keyView = View.inflate(m_context, R.layout.resetkey, null);
		keyDialog.setContentView(keyView);
		keyTxt=(EditText)keyView.findViewById(R.id.key_old);
		newTxt=(EditText)keyView.findViewById(R.id.key_new);
		againTxt=(EditText)keyView.findViewById(R.id.key_new_again);
		dbPasswd = DatabaseHelper.getPasswdHash(m_context);
		keyTxt.addTextChangedListener(change2);
		newTxt.addTextChangedListener(change2);		
		againTxt.addTextChangedListener(change2);
		keyTxt.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				EditText txt=(EditText)v;
				if(!v.hasFocus()&&!isPasswd(txt.getText().toString())&&!txt.getText().toString().equals(""))
					Toast.makeText(m_context, R.string.wrong_key, Toast.LENGTH_SHORT).show();		//Ã· æ‘≠√‹¬Î¥ÌŒÛ
			}
			
		});
		
		keyDialog.show();
	}
	static public void cancelKey(Context context)
	{
		m_context = context;
		keyDialog=new Dialog(m_context,R.style.dialog);
		View keyView = View.inflate(m_context, R.layout.cancelkey, null);
		keyDialog.setContentView(keyView);
		keyTxt=(EditText)keyView.findViewById(R.id.key_old);
		keyTxt.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String old=keyTxt.getText().toString();
				if(isPasswd(old))
				{
					DatabaseHelper.setPasswd(m_context, "");
					Main.updatePasswd("");
					Toast.makeText(m_context, R.string.key_canceled,Toast.LENGTH_SHORT).show();
					keyDialog.dismiss();
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		keyDialog.show();
	}
}
