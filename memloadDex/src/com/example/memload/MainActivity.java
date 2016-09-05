package com.example.memload;

import java.io.InputStream;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;


public class MainActivity extends Activity implements OnClickListener{
	public Button main_btn1;
	public Button main_btn2;
	private IntentFilter intentFile;

	
	protected void onCreate(Bundle save)
	{
		super.onCreate(save);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//锟斤拷去 activity 头锟斤拷志
		setContentView(R.layout.main_layout);
		main_btn1 = (Button) findViewById(R.id.main_btn1);
		main_btn2 = (Button) findViewById(R.id.main_btn2);
		main_btn1.setOnClickListener(this);
		main_btn2.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.main_btn1:
		{
			startNativeLoadDynDex();
		}
			break;
		case R.id.main_btn2:

			break;
		default:
			break;
		}
	}
	protected void onDestory()
	{
		super.onDestroy();
	}
	private void startNativeLoadDynDex(){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                String dexPath ="/data/local/tmp/" + "classes.dex";
                try{
                InputStream is = getAssets().open("classes.dex");  
                int size = is.available();  
                // Read the entire asset into a local byte buffer.  
                byte[] buffer = new byte[size];  
                is.read(buffer);  
                is.close();
                
                injectDexClassLoader(buffer);
                }catch (Exception e){
                    Log.i("jw", "error:"+Log.getStackTraceString(e));
                }
            }
        }).start();
    }

    private void injectDexClassLoader(byte[] dexContent){
//        Object currentActivityThread = RefInvoke.invokeStaticMethod(
//                "android.app.ActivityThread", "currentActivityThread",
//                new Class[] {}, new Object[] {});
//        String packageName = getPackageName();
//        ArrayMap mPackages = (ArrayMap) RefInvoke.getFieldOjbect(
//                "android.app.ActivityThread", currentActivityThread,
//                "mPackages");
//        WeakReference refToLoadedApk = (WeakReference) mPackages.get(packageName);
//        ClassLoader clzLoader = (ClassLoader) RefInvoke.getFieldOjbect("android.app.LoadedApk",
//                refToLoadedApk.get(), "mClassLoader");
        DynamicDexClassLoder dLoader = new DynamicDexClassLoder(
                getApplicationContext(),
                dexContent,
                null,
                getClassLoader(),
                getPackageResourcePath(),getDir(".dex", MODE_PRIVATE).getAbsolutePath()
        );
        try{
            Class clazzR = dLoader.findClass("firstActivity.MainActivity");
            Method method = clazzR.getMethod("memload_test", Context.class);
            method.invoke(null, getApplicationContext());
        }catch (Exception e){
            Log.i("jw", "error:"+Log.getStackTraceString(e));
        }
//        RefInvoke.setFieldOjbect("android.app.LoadedApk", "mClassLoader", refToLoadedApk.get(), dLoader);
    }
    static {
        System.loadLibrary("loaddex");
    }
}
