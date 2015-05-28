package com.kd.lockscreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyLock extends Activity{
	private DevicePolicyManager policyManager;
	private ComponentName componentName;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //获取设备管理服务
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        
        //AdminReceiver 继承自 DeviceAdminReceiver
        componentName = new ComponentName(this, AdminReceiver.class);
        
    	boolean active = policyManager.isAdminActive(componentName);
    	
    	if(!active){//若无权限
            // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            
            //权限列表
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

            //描述(additional explanation)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "------ 锁屏 ------");

            startActivityForResult(intent, 0);
    			
    		policyManager.lockNow();//并锁屏 
    	
    	}else{
        	
            policyManager.lockNow();//直接锁屏
        }
    	finish();
 
    //  killMyself ，锁屏之后就立即kill掉我们的Activity，避免资源的浪费;   
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
