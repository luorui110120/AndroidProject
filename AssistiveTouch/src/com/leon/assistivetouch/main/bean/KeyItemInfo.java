package com.leon.assistivetouch.main.bean;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import com.leon.assistivetouch.main.R;
import com.leon.assistivetouch.main.util.KeyAction;
import com.leon.assistivetouch.main.util.L;
import com.leon.assistivetouch.main.util.MemoryCache;
import com.leon.assistivetouch.main.util.ToolAction;
import com.leon.assistivetouch.main.util.Util;

/** 
 * 类名      KeyItemInfo.java
 * 说明   description of the class
 * 创建日期 2012-8-20
 * 作者  LiWenLong
 * Email lendylongli@gmail.com
 * 更新时间  $Date$
 * 最后更新者 $Author$
 */
public class KeyItemInfo {
	private String title;
	private String summary;
	private Drawable icon;
	private Drawable icon_pressed;
	private int type;
	private String data;
	
	public static final int TYPE_NONE = 0;
	public static final int TYPE_APP = 1;
	public static final int TYPE_TOOL = 2;
	public static final int TYPE_KEY = 3;
	
	public static final int KEY_HOME = 1;
	public static final int KEY_BACK = 2;
	public static final int KEY_RECENT = 3;
	public static final int KEY_MENU = 4;
	public static final int KEY_SEARCH = 5;
	public static final int KEY_HIDE = 6;
	public static final int KEY_POWER = 7;
	public static final int KEY_VOLUME_UP = 8;
	public static final int KEY_VOLUME_DOWN = 9;
	
	public static final int TOOL_SCREENSHOT = 1;
	public static final int TOOL_KILL_PROCESS = 2;
	public static final int TOOL_NETWORK_MOBILE = 3;
	
	public KeyItemInfo () {}
	
	public KeyItemInfo (Context context, String title, int icon, int type, String data) {
		this(title, context.getResources().getDrawable(icon), type, data);
	}
	
	public KeyItemInfo (Context context, String title, int icon, int icon_pressed, int type, String data) {
		this(title, context.getResources().getDrawable(icon), context.getResources().getDrawable(icon_pressed), type, data);
	}
	
	public KeyItemInfo (String title, Drawable icon, Drawable icon_pressed, int type, String data) {
		this.title = title;
		this.icon = icon;
		this.icon_pressed = icon_pressed;
		this.type = type;
		this.data = data;
	}
	
	public KeyItemInfo (String title, Drawable icon, int type, String data) {
		this.title = title;
		this.icon = icon;
		this.type = type;
		this.data = data;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	public Drawable getIconPressed() {
		return icon_pressed;
	}
	public void setIconPressed(Drawable icon_pressed) {
		this.icon_pressed = icon_pressed;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public static void doKeyEvent (String data) {
		int key = Integer.parseInt(data);
		switch(key) {
		case KEY_BACK:
			KeyAction.doBackAction();
			break;
		case KEY_HOME:
			KeyAction.doHomeAction();
			break;
		case KEY_MENU:
			KeyAction.doMenuAction();
			break;
		case KEY_RECENT:
			KeyAction.doRecentAction();
			break;
		case KEY_SEARCH:
			KeyAction.doSearchAction();
			break;
		case KEY_POWER:
			KeyAction.doPowerAction();
			break;
		case KEY_VOLUME_UP:
			KeyAction.doUpAction();
			break;
		case KEY_VOLUME_DOWN:
			KeyAction.doDownAction();
		default:
			return;
		}
	}
	public static void doToolEvent (Context context, String index, Object data)
	{
		int key = Integer.parseInt(index);
		switch(key) {
		case TOOL_SCREENSHOT:
		{
			/*
			View vPanel = (View)data;
			while( View.VISIBLE == vPanel.getVisibility())
			{
				try
				{
					Thread.currentThread().sleep(100);	//添加一个延迟时间
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			*/
			ToolAction.doScreenShot();
//			String path = Util.getSdDirectory() + "/Pictures/Screenshots/Screenshot_" + 
//						Util.getCourrentDateString() + ".png";
//			if(Util.isFolderAndMkdirs(Util.getNetDiskPathFromPath(path)))
//			{
//				ToolAction.doScreenShot(path);
//				L.Toast("save: " + path, context, Toast.LENGTH_LONG);
//			}
			break;
		}
		case TOOL_KILL_PROCESS:
			ToolAction.doKillProcess(context);
			break;
		case TOOL_NETWORK_MOBILE:
		{
			boolean bNetWork = Util.isNetWorkMobile(context);
			ToolAction.doNetWorkSwitch(!bNetWork);
			if(!bNetWork)
			{
				L.Toast(R.string.tool_network_mobile_on, context);
			}
			else
			{
				L.Toast(R.string.tool_network_mobile_off, context);
			}
			
		}
			break;
		default:
			break;
		}
	}
	public static boolean isEquals (KeyItemInfo info1, KeyItemInfo info2) {
		if (info1 == null && info2 == null) {
			return true;
		}
		
		if (info1 != null && info2 != null && 
				info1.type == info2.type && 
				info1.data.equals(info2.data)) {
			return true;
		}
		return false;
	}
	
	public static int getKeyResource (String data) {
		int key = Integer.getInteger(data, 0);
		int res = -1;
		switch(key) {
		case KEY_BACK:
			res = R.drawable.ic_sysbar_back;
			break;
		case KEY_HOME:
			res = R.drawable.ic_sysbar_home;
			break;
		case KEY_MENU:
			res = R.drawable.ic_sysbar_menu;
			break;
		case KEY_RECENT:
			res = R.drawable.ic_sysbar_recent;
			break;
		case KEY_SEARCH:
			res = R.drawable.ic_sysbar_search;
			break;
		case KEY_POWER:
			res = R.drawable.ic_lock_power_off;
			break;
		case KEY_VOLUME_UP:
			res = R.drawable.ic_volume_up;
			break;
		case KEY_VOLUME_DOWN:
			res = R.drawable.ic_volume_down;
		}
		return res;
	}
	
	public static KeyItemInfo getKeyItemInfo (Context context, int type, String data) {
		if (Util.isStringNull(data)) {
			return null;
		}
		
		if (type == TYPE_APP) {
			String d[] = data.split(":");
			if (d == null || d.length != 2) {
				return null;
			}
			PackageManager pm = context.getPackageManager();
			ResolveInfo info = MemoryCache.getResolveInfoFromActivityName(pm, d[1]);
			if (info == null) {
				return null;
			}
			String title = (String) info.loadLabel(pm);
			Drawable icon = info.loadIcon(pm);
			return new KeyItemInfo(title, icon, type, data);
		}
		else if (type == TYPE_KEY) {
			int key = Integer.parseInt(data);
			int res = -1;
			String title = "";
			switch(key) {
			case KEY_BACK:
				title = context.getString(R.string.key_back_name);
				res = R.drawable.ic_sysbar_back;
				break;
			case KEY_HOME:
				title = context.getString(R.string.key_home_name);
				res = R.drawable.ic_sysbar_home;
				break;
			case KEY_MENU:
				title = context.getString(R.string.key_menu_name);
				res = R.drawable.ic_sysbar_menu;
				break;
			case KEY_RECENT:
				title = context.getString(R.string.key_recent_name);
				res = R.drawable.ic_sysbar_recent;
				break;
			case KEY_SEARCH:
				title = context.getString(R.string.key_search_name);
				res = R.drawable.ic_sysbar_search;
				break;
			case KEY_HIDE:
				res = R.drawable.ic_home;
				break;
			case KEY_POWER:
				title = context.getString(R.string.key_power_name);
				res = R.drawable.ic_lock_power_off;
				break;
			case KEY_VOLUME_UP:
				title = context.getString(R.string.key_volume_up_name);
				res = R.drawable.ic_volume_up;
				break;
			case KEY_VOLUME_DOWN:
				title = context.getString(R.string.key_volume_down_name);
				res = R.drawable.ic_volume_down;
				break;
			}
			return new KeyItemInfo(context, title, res, type, data);
		}
		else if(type == TYPE_TOOL)
		{
			int key = Integer.parseInt(data);
			int res = -1, res_pressed=-1;
			String title = "";
			switch(key) {
			case TOOL_SCREENSHOT:
				title = context.getString(R.string.key_screenshot_name);
				res = R.drawable.ic_screenshot_normal;
				break;
			case TOOL_KILL_PROCESS:
				title = context.getString(R.string.key_kill_process_name);
				res = R.drawable.ic_kill_process;
				break;
			case TOOL_NETWORK_MOBILE:
				title = context.getString(R.string.key_network_mobile_name);
				res = R.drawable.ic_network_data;
				res_pressed = R.drawable.ic_network_data_pressed;
				break;
			default:
				break;
			}
			if(res_pressed == -1)
			{
				return new KeyItemInfo(context, title, res, type, data);
			}
			else
			{
				return new KeyItemInfo(context, title, res, res_pressed, type, data);
			}
		}
		return null;
	}
}
