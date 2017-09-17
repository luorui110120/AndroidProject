package com.leon.assistivetouch.main.util;

import java.util.Arrays;
import java.util.List;

/** 
 * 类名      Constan.java
 * 说明  配置文件
 * 创建日期 2012-8-21
 * 作者  LiWenLong
 * Email lendylongli@gmail.com
 * 更新时间  $Date$
 * 最后更新者 $Author$
*/
public interface Constan {

	public static final boolean isDebug = true;
	
	// 每三小时更新一次
	public static final long CHECK_UPDATE_TIME_LAG = 1000 * 60 * 60 * 3;
	
	/**
	 * Update Url
	 * 在svn的download/update/stable 目录下存放一个配置文件，包含versionCode、fileName、title、message
	 * 所以先下载这个配置文件，然后以 versionCode是作为判断是否需要更新的条件，而fileName是下载的文件名
	 * 下载地址为:http://assistivetouch.googlecode.com/files/{fileName}
	 * title 更新的标题
	 * message 更新的内容
	 * */
	public static final String APPLICATION_PROPERTIES_URL = "http://assistivetouch.googlecode.com/svn/download/update/stable/application.properties";
	public static final String APPLICATION_DOWNLOAD_URL = "http://assistivetouch.googlecode.com/files/";
	
	public static final String TOUCH_DOT_VIEW_POS_X_KEY = "touch_dot_pos_x";
	public static final String TOUCH_DOT_VIEW_POS_Y_KEY = "touch_dot_pos_y";
	
	public static final int DEFAULT_TOUCH_DOT_VIEW_POS_X = 0;
	public static final int DEFAULT_TOUCH_DOT_VIEW_POS_Y = 0;
	
	public static final int DEFAULT_TOUCH_PANEL_ITEM_SIZE = 9;
	
	public static final String INIT_APPLICATION_VERSION_CODE = "init_version_code"; 
	
	public static final String ENABLE_ASSISTIVE_KEY = "enable_assisitive";
	public static final String ENABLE_BOOT_START_KEY = "enable_boot_start";
	public static final String ENABLE_VIBRATOR_KEY = "enable_virbator";
	public static final String ENABLE_LONG_PRESS_KEY = "enable_long_press";
	public static final String ENABLE_DOUBLE_TAP_KEY = "enable_double_tap";
	public static final String ENABLE_AUTO_UPDATE_KEY = "enable_auto_update";
	public static final String ENABLE_SAVE_TOP_PKG_KEY = "enable_save_top_pkg";
	public static final String ENABLE_ITEM_TEXT_KEY = "enable_item_text";
	public static final String ENABLE_CLICK_EVENT_KEY = "enable_click_event";
	
	public static final String TOUCH_DOT_TRANSPARENCY_KEY = "touch_dot_transparency";
	public static final String TOUCH_DOT_SIZE_KEY = "touch_dot_size";
	
	public static final String KILL_WHITE_NAMES_KEY = "kill_white_names";
	
	// 按键自定义设置
	public static final String TOUCH_MAIN_ITEM_DATA = "main_item_data";
	public static final String TOUCH_MAIN_ITEM_DATA_1 = TOUCH_MAIN_ITEM_DATA + 1;
	public static final String TOUCH_MAIN_ITEM_DATA_2 = TOUCH_MAIN_ITEM_DATA + 2;
	public static final String TOUCH_MAIN_ITEM_DATA_3 = TOUCH_MAIN_ITEM_DATA + 3;
	public static final String TOUCH_MAIN_ITEM_DATA_4 = TOUCH_MAIN_ITEM_DATA + 4;
	public static final String TOUCH_MAIN_ITEM_DATA_5 = TOUCH_MAIN_ITEM_DATA + 5;
	public static final String TOUCH_MAIN_ITEM_DATA_6 = TOUCH_MAIN_ITEM_DATA + 6;
	public static final String TOUCH_MAIN_ITEM_DATA_7 = TOUCH_MAIN_ITEM_DATA + 7;
	public static final String TOUCH_MAIN_ITEM_DATA_8 = TOUCH_MAIN_ITEM_DATA + 8;
	public static final String TOUCH_MAIN_ITEM_DATA_9 = TOUCH_MAIN_ITEM_DATA + 9;

	public static final String TOUCH_MAIN_ITEM_TYPE = "main_item_type";
	public static final String TOUCH_MAIN_ITEM_TYPE_1 = TOUCH_MAIN_ITEM_TYPE + 1;
	public static final String TOUCH_MAIN_ITEM_TYPE_2 = TOUCH_MAIN_ITEM_TYPE + 2;
	public static final String TOUCH_MAIN_ITEM_TYPE_3 = TOUCH_MAIN_ITEM_TYPE + 3;
	public static final String TOUCH_MAIN_ITEM_TYPE_4 = TOUCH_MAIN_ITEM_TYPE + 4;
	public static final String TOUCH_MAIN_ITEM_TYPE_5 = TOUCH_MAIN_ITEM_TYPE + 5;
	public static final String TOUCH_MAIN_ITEM_TYPE_6 = TOUCH_MAIN_ITEM_TYPE + 6;
	public static final String TOUCH_MAIN_ITEM_TYPE_7 = TOUCH_MAIN_ITEM_TYPE + 7;
	public static final String TOUCH_MAIN_ITEM_TYPE_8 = TOUCH_MAIN_ITEM_TYPE + 8;
	public static final String TOUCH_MAIN_ITEM_TYPE_9 = TOUCH_MAIN_ITEM_TYPE + 9;
	
	// 默认 kill 白名单
	public static final List<String> KILL_WHITE_LIST = Arrays.asList("com.baidu.input_miv6", "com.anyview", "com.swimmi.windnote", "com.tencent.mm");
	// 截屏 命令
	public static final List<ScreenInfo> PHONE_MODEL_SCREEN = Arrays.asList(new ScreenInfo("Nexus 6", "sendevent /dev/input/event1 1 116 1;sendevent /dev/input/event0 0 0 0;sendevent /dev/input/event1 1 114 1;sendevent /dev/input/event1 0 0 0;sleep 1;sendevent /dev/input/event1 1 116 0;sendevent /dev/input/event0 0 0 0;sendevent /dev/input/event1 1 114 0;sendevent /dev/input/event1 0 0 0"),
																new ScreenInfo("Nexus 6P", "sendevent /dev/input/event2 1 116 1;sendevent /dev/input/event2 0 0 0;sendevent /dev/input/event2 1 114 1;sendevent /dev/input/event2 0 0 0;sleep 1;sendevent /dev/input/event2 1 116 0;sendevent /dev/input/event2 0 0 0;sendevent /dev/input/event2 1 114 0;sendevent /dev/input/event2 0 0 0"));
	public static class ScreenInfo{
		String model;
		String cmd;
		public ScreenInfo(String inModel, String inCmd)
		{
			this.model = inModel;
			this.cmd = inCmd;
		}
		public String getModel(){
			return model;
		}
		public String getCMD(){
			return cmd;
		}
	}
}
