package com.swimmi.windnote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

//DatabaseHelper作为一个访问SQLite的助手类，提供两个方面的功能，
//第一，getReadableDatabase(),getWritableDatabase()可以获得SQLiteDatabse对象，通过该对象可以对数据库进行操作
//第二，提供了onCreate()和onUpgrade()两个回调函数，允许我们在创建和升级数据库时，进行自己的操作

public class DatabaseHelper {

	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2)
	{
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}
	public static byte[] GetMD5Code(byte[] datas)
	{
		try
		{
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(datas);
			/*
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i)
			{
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
			*/
			return array;
		} catch (java.security.NoSuchAlgorithmException e)
		{
		}
		return null;
	}
	public static String GetSHA512Code(byte[] datas)
	{
		try
		{
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("SHA-512");
			byte[] array = md.digest(datas);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i)
			{
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static byte[] des3EncodeCBC(byte[] key, byte[] keyiv, byte[] data)
	{
		try
		{
			SecretKey deskey = null;
			DESedeKeySpec spec = new DESedeKeySpec(key);
			SecretKeyFactory keyfactory = SecretKeyFactory
					.getInstance("desede");
			deskey = keyfactory.generateSecret(spec);

			Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
			IvParameterSpec ips = new IvParameterSpec(keyiv);
			cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
			byte[] bOut = cipher.doFinal(data);
			return bOut;
		} catch (Exception e)
		{
			System.err.println("3DES Encrypt Error!");
			e.printStackTrace();
		}
		return null;

	}

	public static byte[] des3DecodeCBC(byte[] key, byte[] keyiv, byte[] data)
	{
		try
		{
			SecretKey deskey = null;
			DESedeKeySpec spec = new DESedeKeySpec(key);
			SecretKeyFactory keyfactory = SecretKeyFactory
					.getInstance("desede");
			deskey = keyfactory.generateSecret(spec);

			Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
			IvParameterSpec ips = new IvParameterSpec(keyiv);

			cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

			byte[] bOut = cipher.doFinal(data);
			return bOut;
		} catch (Exception e)
		{
			System.err.println("3DES Decrypt Error!");
			e.printStackTrace();
		}
		return null;

	}
	
	@SuppressLint("NewApi")
	static String Encrypt(String strData, String passwd)
	{
		if(passwd.length() == 0)
		{
			return strData;
		}
		byte[] byteinputMd5 = GetMD5Code(passwd.getBytes());
		byte[] byvi = GetMD5Code(byteinputMd5);
		byte[] ba = byteMerger(byteinputMd5, byvi);
		byte[] des3key = Arrays.copyOfRange(ba, 0, 0x18);
		byte[] des3keyvi = Arrays.copyOfRange(ba, 0x18, 0x20);
		byte[] bydes3endata = des3EncodeCBC(des3key, des3keyvi, strData.getBytes());
		if(null != bydes3endata)
		{
			try
			{
				return  new String(Base64.encode(bydes3endata,  Base64.DEFAULT), "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
		
	}

	@SuppressLint("NewApi")
	static String Decrypt(String strEnData, String passwd)
	{
		if(passwd.length() == 0)
		{
			return strEnData;
		}
		byte[] byteinputMd5 = GetMD5Code(passwd.getBytes());
		byte[] byvi = GetMD5Code(byteinputMd5);
		byte[] ba = byteMerger(byteinputMd5, byvi);
		byte[] des3key = Arrays.copyOfRange(ba, 0, 0x18);
		byte[] des3keyvi = Arrays.copyOfRange(ba, 0x18, 0x20);
		byte[] bydes3dedata = des3DecodeCBC(des3key, des3keyvi,
				Base64.decode(strEnData.getBytes(), Base64.DEFAULT));
		if(null != bydes3dedata)
		{
			try
			{
				return new String(bydes3dedata, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	static public SQLiteDatabase Database(Context context, int raw_id)
	{
		 try {
			 int BUFFER_SIZE = 100000;
	        	String DB_NAME = "windnote.db"; 
	        	String PACKAGE_NAME = context.getPackageName();
	        	// 保存到 /data/data 私有目录下
//	        	String DB_PATH = "/data"
//	                + Environment.getDataDirectory().getAbsolutePath() + "/"
//	                + PACKAGE_NAME+"/databases/";
	        	//保存到 sdcard 目录中
	        	String DB_PATH = Environment.getExternalStorageDirectory() + "/.appdata/" 
	        			+ PACKAGE_NAME + "/databases/";
	        	File destDir = new File(DB_PATH);
	        	  if (!destDir.exists()) {
	        	   destDir.mkdirs();
	        	  }
	        	String file=DB_PATH+DB_NAME;
	        	if (!(new File(file).exists())) {
	                InputStream is = context.getResources().openRawResource(
	                        raw_id);
	                FileOutputStream fos = new FileOutputStream(file);
	                byte[] buffer = new byte[BUFFER_SIZE];
	                int count = 0;
	                while ((count = is.read(buffer)) > 0) {
	                    fos.write(buffer, 0, count);
	                }
	                fos.close();
	                is.close();
	            }
	            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file,null);
	            return db;
	        } catch (FileNotFoundException e) {
	            Log.e("Database", "File not found");
	            e.printStackTrace();
	        } catch (IOException e) {
	            Log.e("Database", "IO exception");
	            e.printStackTrace();
	        }
	        return null;
	}
	static public String getCursorDecryptString(Cursor cursor, String columnName)
	{
		String Endata = cursor.getString(cursor.getColumnIndex(columnName));
		return Decrypt(Endata, Main.notePasswd);
	}
	static public void updateDataBase(Context context, String newPasswd)
	{
		SQLiteDatabase db = Database(context, R.raw.windnote);
		Cursor cursor=db.rawQuery("select id,n_title,n_content,n_time,n_count,n_lock,julianday(date('now','localtime'))-julianday(date(n_postdate)) as n_postday from notes order by n_postdate "+"desc", null);
		while(cursor.moveToNext())
		{
			int n_id=cursor.getInt(cursor.getColumnIndex("id"));
			//String n_title=getCursorDecryptString(cursor,"n_title");
			String n_title=cursor.getString(cursor.getColumnIndex("n_title"));
			String n_content=getCursorDecryptString(cursor,"n_content");
			if(newPasswd.length() == 0)
			{
				db.execSQL("update notes set n_title=?,n_content=? where id=?",new Object[]{n_title, n_content,n_id});
			}
			else
			{
				db.execSQL("update notes set n_title=?,n_content=? where id=?",new Object[]{n_title, Encrypt(n_content, newPasswd),n_id});
			}
		}
	}
	static public String getPasswdHash(Context context)
	{
		String strPasswd = "";
		SQLiteDatabase db = Database(context, R.raw.windnote);
		Cursor cursor = db.query("passwd", new String[]{"passwd"}, null, null, null, null, null);
		if(cursor.moveToNext())
		{
			strPasswd = cursor.getString(cursor.getColumnIndex("passwd"));
		}
		return strPasswd;
	}
	static public void setPasswd(Context context, String passwd)
	{
		String passwdHash = "";
		if(passwd.length() > 0)
		{
			passwdHash = GetSHA512Code(passwd.getBytes());
		}
		SQLiteDatabase db = Database(context, R.raw.windnote);
		db.execSQL("update passwd set passwd=?",new Object[]{passwdHash});
		updateDataBase(context, passwd);
		Main.updatePasswd(passwd);
		
	}

}
