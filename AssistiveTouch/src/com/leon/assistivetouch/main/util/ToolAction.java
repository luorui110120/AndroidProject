package com.leon.assistivetouch.main.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.leon.assistivetouch.main.R;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;
import eu.chainfire.libsuperuser.Shell;

public class ToolAction
{
//	private static String[] g_kill_white_list = {"com.baidu.input_miv6", "com.anyview", "com.swimmi.windnote", "com.tencent.mm"};
	 /** avoid showing ads on ads */
    private static List<String> mKillWhiteList = Constan.KILL_WHITE_LIST;
    public static void setKillWhiteList(String whiteStr)
    {
    	String[] whiteStrs = whiteStr.split(";");
    	mKillWhiteList = new ArrayList<String>();
    	for(String i : whiteStrs)
    	{
    		mKillWhiteList.add(i);
    	}
    }
    public static String getKillWhiteList()
    {
    	String res= "";
    	for(String i : mKillWhiteList)
    	{
    		res +=i + ";";
    	}
    	return res;
    }
	// 直接使用截屏命令
	public static void doScreenShot(String Path)
	{
		RootContext.getInstance().runCommand("screencap -p " + Path);
	}
	// 模拟 [电源]+[音量-]  实现截屏
	public static void doScreenShot()
	{
		/*
		//  getevent 打印 手机的所有事件操作 有点像windows skype 工具 , 
		//	然后在通过这些 操作来模拟点击 ,  下面是 Nexus 6 截屏 实例,
		//  通过下面的结果我们已经知道应该怎么 模拟截屏了吧, 就是这个命令
		//  "sendevent /dev/input/event1 1 116 1;sendevent /dev/input/event0 0 0 0;sendevent /dev/input/event1 1 114 1;sendevent /dev/input/event1 0 0 0;sleep 1;sendevent /dev/input/event1 1 116 0;sendevent /dev/input/event0 0 0 0;sendevent /dev/input/event1 1 114 0;sendevent /dev/input/event1 0 0 0"
		root@shamu:/sdcard/Pictures/Screenshots # getevent
		add device 1: /dev/input/event4
		  name:     "apq8084-taiko-tfa9890_stereo_co Headset Jack"
		add device 2: /dev/input/event3
		  name:     "apq8084-taiko-tfa9890_stereo_co Button Jack"
		add device 3: /dev/input/event1
		  name:     "qpnp_pon"
		could not get driver version for /dev/input/mice, Not a typewriter
		add device 4: /dev/input/event2
		  name:     "gpio-keys"
		add device 5: /dev/input/event0
		  name:     "atmel_mxt_ts"
		/dev/input/event1: 0001 0074 00000001		//注意这里 0074  其实是 16 进制
		/dev/input/event1: 0000 0000 00000000
		/dev/input/event1: 0001 0072 00000001
		/dev/input/event1: 0000 0000 00000000
		/dev/input/event1: 0001 0074 00000000
		/dev/input/event1: 0000 0000 00000000
		/dev/input/event1: 0001 0072 00000000
		/dev/input/event1: 0000 0000 00000000
		*/
//		String screnncap_n6 = "sendevent /dev/input/event1 1 116 1;sendevent /dev/input/event0 0 0 0;sendevent /dev/input/event1 1 114 1;sendevent /dev/input/event1 0 0 0;sleep 1;sendevent /dev/input/event1 1 116 0;sendevent /dev/input/event0 0 0 0;sendevent /dev/input/event1 1 114 0;sendevent /dev/input/event1 0 0 0";
//		String screnncap_n6p = "sendevent /dev/input/event2 1 116 1;sendevent /dev/input/event2 0 0 0;sendevent /dev/input/event2 1 114 1;sendevent /dev/input/event2 0 0 0;sleep 1;sendevent /dev/input/event2 1 116 0;sendevent /dev/input/event2 0 0 0;sendevent /dev/input/event2 1 114 0;sendevent /dev/input/event2 0 0 0";
		for(Constan.ScreenInfo info : Constan.PHONE_MODEL_SCREEN)
		{
			if(info.getModel().equals(android.os.Build.MODEL))
			{
				RootContext.getInstance().runCommand(info.getCMD());
			}
		}
	}

	// 流量开关
	public static void doNetWorkSwitch(boolean bflags)
	{
		if(bflags)
		{
			RootContext.getInstance().runCommand("svc data enable");
		}
		else
		{
			RootContext.getInstance().runCommand("svc data disable");
		}
	}
	// 飞行模式
	public static void doAirplaneModeSwitch(boolean bflags)
	{
		if(bflags)
		{
			RootContext.getInstance().runCommand("settings put global airplane_mode_on 1;am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");
		}
		else
		{
			RootContext.getInstance().runCommand("settings put global airplane_mode_on 0;am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false");
		}
	}
	public static void doKillPackName(String PackName)
	{
		RootContext.getInstance().runCommand("am force-stop " + PackName);
	}
	public static void doKillProcess(Context context)
	{
		List<AppEntity> list = getAndroidProcess(context);
		String save_top_pkg_str = "";
		if(Settings.getInstance().isSaveTopPkg())
		{
			List<String> actlist = Shell.SU.run("dumpsys activity | grep mFocusedActivity");
			if(actlist != null && actlist.size() > 0)
			{
				String acts =  actlist.get(0);//RootContext.do_exec("su -c /data/data/" + context.getPackageName() + "/toppkg.sh");
				if(!Util.isStringNull(acts))
				{
					String [] strs = acts.split(" ");
					
					int i = 0;
					for(i = 0; i < strs.length; i++)
					{
						if(strs[i].indexOf('/') > 0)
						{
							break;
						}
					}
					if(i > 0 && i != strs.length)
					{
					//	L.Toast("kill: " + strs[i], context);
						save_top_pkg_str = strs[i].split("/")[0];
					}
					
				}
			}
		}
		if(list != null)
		{
			for(AppEntity i : list)
			{
				if(!mKillWhiteList.contains(i.getPackageName()) 
						&& !save_top_pkg_str.equals(i.getPackageName()))
				{
					doKillPackName(i.getPackageName());
					L.Toast("kill: " + i.getAppName(), context);
				}
			}
			L.Toast(R.string.tool_kill_process_finish, context);
		}
		else
		{
			L.Toast(R.string.tool_kill_process_fail, context);
		}
		
	}
	 /** 
     * 这个方法获取最近运行应用的包名,<br> 
     *  
     * @param context 
     *            上下文对象 
     * @return 返回包名,如果出现异常或者获取失败返回 null
     */  
	public static List<AppEntity> getAndroidProcess(Context context) {  
        List<AppEntity> resule = new ArrayList<AppEntity>();  
        List<String> listPkg = new ArrayList<String>();  
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        PackageManager pm = context.getPackageManager();  
        AppUtils proutils = new AppUtils(context);  
        //List<AndroidAppProcess> listInfo = ProcessManager.getRunningAppProcesses();  
        List<NewApiProcessManager.Process> listInfo= NewApiProcessManager.getRunningApps(); 
        if(listInfo.isEmpty() || listInfo.size() == 0){  
            return null;  
        }  
        for (NewApiProcessManager.Process info : listInfo) {  
            ApplicationInfo app = proutils.getApplicationInfo(info.getPackageName());  
            // 过滤自己当前的应用  
            if (app == null || context.getPackageName().equals(app.packageName) || listPkg.contains(app.packageName)) {  
                continue;  
            }  
            // 过滤系统的应用  
            if ((app.flags & app.FLAG_SYSTEM) > 0) {  
                continue;  
            }  
            AppEntity ent = new AppEntity();  
            ent.setAppIcon(app.loadIcon(pm));//应用的图标  
            ent.setAppName(app.loadLabel(pm).toString());//应用的名称  
            ent.setPackageName(app.packageName);//应用的包名  
            // 计算应用所占内存大小  
            int[] myMempid = new int[] { info.pid };  
            Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);  
            double memSize = memoryInfo[0].dalvikPrivateDirty / 1024.0;  
            int temp = (int) (memSize * 100);  
            memSize = temp / 100.0;  
            ent.setMemorySize(memSize);//应用所占内存的大小  
            listPkg.add(ent.getPackageName());
            resule.add(ent);  
        }  
        return resule;  
    }  
	public static class AppUtils {  
		  
	    private List<ApplicationInfo> appList;  
	  
	    public AppUtils(Context context) {  
	        // 通过包管理器，检索所有的应用程序  
	        PackageManager pm = context.getPackageManager();  
	        appList = pm  
	                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);  
	    }  
	  
	    /** 
	     * 通过包名返回一个应用的Application对象 
	     *  
	     * @param name 
	     * @return ApplicationInfo 
	     */  
	    public ApplicationInfo getApplicationInfo(String pkgName) {  
	        if (pkgName == null) {  
	            return null;  
	        }  
	        for (ApplicationInfo appinfo : appList) {  
	            if (pkgName.equals(appinfo.processName)) {  
	                return appinfo;  
	            }  
	        }  
	        return null;  
	    }  
	  
	} 
}
