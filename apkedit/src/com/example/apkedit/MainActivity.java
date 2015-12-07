package com.example.apkedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.jf.baksmali.baksmalimain;
import org.jf.smali.smalimain;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import axmleditor.decode.AXMLDoc;
import axmleditor.editor.ActivityEditor;
import axmleditor.editor.ApplicationInfoEditor;
import axmleditor.editor.MetaDataEditor;
import axmleditor.editor.PackageInfoEditor;
import axmleditor.editor.PermissionEditor;
import axmleditor.editor.ServiceEditor;

public class MainActivity extends Activity
{

	public EditText edit;
	public Button btn_enter;

	@SuppressLint("NewApi")
	protected void onCreate(Bundle save)
	{
		super.onCreate(save);
		setContentView(R.layout.main_layout);
		edit = (EditText) findViewById(R.id.main_edit);
		btn_enter = (Button) findViewById(R.id.main_btn);
		//NewApplicat.initcall(this);
		btn_enter.setOnClickListener(new OnClickListener()
		{

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v)
			{
			//	NewApplication.initcall(v.getContext());
				try
				{
					//反编译 dex文件
					String [] args_baksmali  = {"-d", "/system/framework", "-x", "/data/local/tmp/test.odex", "-o", "/data/local/tmp/classes"};
					String [] args_smali = {"-o", "/data/local/tmp/classes.dex", "/data/local/tmp/classes"};
				//	baksmalimain.main(args_baksmali);
				//	smalimain.main(args_smali);
					//对 apk 进行签名
				//	apksigner.Main.main();
					//编译 axml 文件
					patchAXML("/data/local/tmp/AXML.xml");
					
	                
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	private void patchAXML(String path) throws FileNotFoundException, Exception
	{
		String appName = "mypatchname";
		File newXML=new File(path);
		//修改app name
        AXMLDoc doc = new AXMLDoc();
        doc.parse(new FileInputStream(newXML));


        ApplicationInfoEditor applicationInfoEditor = new ApplicationInfoEditor(doc);
        System.out.println("getApplicationName:"+ applicationInfoEditor.getApplicationName());
        applicationInfoEditor.setEditorInfo(new ApplicationInfoEditor.EditorInfo(appName, false));
        applicationInfoEditor.commit();

        //更多修改可以在下面添加
        
        PackageInfoEditor packageInfoEditor = new PackageInfoEditor(doc);
        System.out.println("getPackageName:"+ packageInfoEditor.getPackageName());
        packageInfoEditor.setEditorInfo(new PackageInfoEditor.EditorInfo(12563, "abcde", null));
        packageInfoEditor.commit();
        


        PermissionEditor permissionEditor = new PermissionEditor(doc);
        permissionEditor.setEditorInfo(new PermissionEditor.EditorInfo()
                        .with(new PermissionEditor.PermissionOpera("android.permission.ACCESS_FINE_LOCATION").remove())
                        .with(new PermissionEditor.PermissionOpera("android.permission.WRITE_SETTINGS").remove())
                        .with(new PermissionEditor.PermissionOpera("android.permission.INTERNET").add())
        );
        permissionEditor.commit();

        MetaDataEditor metaDataEditor = new MetaDataEditor(doc);
        metaDataEditor.setEditorInfo(new MetaDataEditor.EditorInfo("UMENG_CHANNEL2", "apkeditor123"));
        metaDataEditor.commit();
        
        //add activity
        ActivityEditor activityEditor = new ActivityEditor(doc);
        System.out.println("MainActivity:" + activityEditor.findMainActivity());
        activityEditor.setEditorInfo(new ActivityEditor.EditorInfo("com.exp.myactivity", "mylabel"));
        activityEditor.commit();
        
        
        //add service
        ServiceEditor serviceEditor = new ServiceEditor(doc);
        serviceEditor.setEditorInfo(new ServiceEditor.EditorInfo("com.InitService", "true", ":init"));
        serviceEditor.commit();
       
        doc.build(new FileOutputStream(newXML));
        doc.release();
	}


}
