package axmleditor.editor;

import android.util.Log;
import axmleditor.decode.AXMLDoc;
import axmleditor.decode.BTagNode;
import axmleditor.decode.BXMLNode;
import axmleditor.decode.StringBlock;
import axmleditor.decode.BTagNode.Attribute;
import axmleditor.utils.TypedValue;

import java.util.List;

import org.antlr.grammar.v3.ANTLRParser.alternative_return;

/**
 * 添加或修改 meta-data 信息
 *
 * MetaDataEditor metaDataEditor = new MetaDataEditor(doc);
 * metaDataEditor.setEditorInfo(new MetaDataEditor.EditorInfo("UMENG_CHANNEL", "apkeditor")); // meta-data  name 和value
 * metaDataEditor.commit();
 *
 * Created by zl on 15/9/8.
 */
public class ActivityEditor extends BaseEditor<ActivityEditor.EditorInfo> {

    public ActivityEditor(AXMLDoc doc) {
        super(doc);
        setEditor(NAME,VALUE);
    }

    private int activity;


    @Override
    public String getEditorName() {
        return NODE_ACTIVITY;
    }

    @Override
    protected void editor() {
        BXMLNode application = doc.getApplicationNode(); //manifest node
        List<BXMLNode> children = application.getChildren();

        BTagNode activity = (BTagNode) findNode();

        //如果有  直接修改
        if(activity != null){
            activity.setAttrStringForKey(attr_label, editorInfo.activityValue_Index);
        }else{
            BTagNode.Attribute name_attr = new BTagNode.Attribute(namespace, attr_name, TypedValue.TYPE_STRING);
            name_attr.setString(editorInfo.activityName_Index);
            BTagNode.Attribute value_attr = new BTagNode.Attribute(namespace, attr_label, TypedValue.TYPE_STRING);
            value_attr.setString(editorInfo.activityValue_Index);

            //没有  新建节点插入
            activity = new BTagNode(-1, this.activity);
            activity.setAttribute(name_attr);
            activity.setAttribute(value_attr);
            children.add(activity);
        }
        doc.getStringBlock().setString(editorInfo.activityValue_Index, editorInfo.activitylabel);
    }
    @Override
    protected BXMLNode findNode() {
        BXMLNode application = doc.getApplicationNode(); //manifest node
        List<BXMLNode> children = application.getChildren();

        BTagNode meta_data = null;
       
    
        end:for(BXMLNode node : children){
            BTagNode m = (BTagNode)node;
//            Attribute[] att =  m.getAttribute();
//            for(Attribute a: att)
//            {
//            //	Log.e("Activity", m.getAttrStringForKey(a.mName));
//            //	if(this.activity == m.getName()) 
//            		Log.e("Activity", doc.getStringBlock().getStringFor(a.mName) + ":" + doc.getStringBlock().getStringFor(a.mString) );
//            }
            //it's a risk that the value for "android:name" maybe not String
            if((this.activity == m.getName()) && (m.getAttrStringForKey(attr_name) == editorInfo.activityName_Index)){
                meta_data = m;
                break end;
            }
        }
//        List<BXMLNode> children2 = children.get(0).getChildren().get(0).getChildren();
//        for(BXMLNode node2 : children2)
//        {
//        	BTagNode m = (BTagNode)node2;
//            Attribute[] att =  m.getAttribute();
//            for(Attribute a: att)
//            {
//            //	Log.e("Activity", m.getAttrStringForKey(a.mName));
//            //	if(this.activity == m.getName()) 
//            		Log.e("subActivity", doc.getStringBlock().getStringFor(a.mName) + ":" + doc.getStringBlock().getStringFor(a.mString) );
//            }
//        }
        
        return meta_data;
    }
    
    public String findMainActivity() {
        BXMLNode application = doc.getApplicationNode(); //manifest node
        List<BXMLNode> children = application.getChildren();

        BTagNode meta_data = null;
        int activityindex = doc.getStringBlock().putString(NODE_ACTIVITY);
        int activityaliasindex = doc.getStringBlock().putString(NODE_ACTIVITY_ALIAS);
        end:for(BXMLNode node : children){
            BTagNode m = (BTagNode)node;
            //it's a risk that the value for "android:name" maybe not String
            if(activityindex == m.getName() || activityaliasindex == m.getName())
            {
            	List<BXMLNode> children2 = node.getChildren();
            	if(children2 == null)
            	{
            		continue;
            	}
            	for(BXMLNode node2 : children2)
            	{
            		boolean bmain = false;
        			boolean blauncher = false;
            		List<BXMLNode> children3 = node2.getChildren();
            		if(children3 == null)
                	{
                		continue;
                	}
            		for(BXMLNode node3 : children3)
            		{
            			BTagNode m3 = (BTagNode)node3;
                        Attribute[] att3 =  m3.getAttribute();
                        for(Attribute a: att3)
                        {
                        //	Log.e("findMainActivity", doc.getStringBlock().getStringFor(a.mName) + ":" + doc.getStringBlock().getStringFor(a.mString) );
                        	if("name".equals(doc.getStringBlock().getStringFor(a.mName)) &&
                        			"android.intent.category.LAUNCHER".equals(doc.getStringBlock().getStringFor(a.mString)))
                        	{
                        		blauncher = true;
                        	}
                        	else if("name".equals(doc.getStringBlock().getStringFor(a.mName)) &&
                        			"android.intent.action.MAIN".equals(doc.getStringBlock().getStringFor(a.mString)))
                        	{
                        		bmain = true;
                        	}
                        }
            		}
            	//	Log.e("findMainActivity", "bmain:" + bmain + "blauncher:" + blauncher);
            		if(bmain && blauncher)
                    {
            			meta_data = m;
                 		break end;
                    }
            	}
            	
            }
        }
        if(meta_data != null)
        {
	        Attribute[] att =  meta_data.getAttribute();
	        for(Attribute a: att)
	        {
	        	if("name".equals(doc.getStringBlock().getStringFor(a.mName)))
	        	{
	        		return doc.getStringBlock().getStringFor(a.mString);
	        	}
	        }
        }
        return "";
    }


    @Override
    protected void registStringBlock(StringBlock sb) {
        namespace = sb.putString(NAME_SPACE);
        activity = sb.putString(NODE_ACTIVITY);

        attr_name = sb.putString(NAME);
        attr_label = sb.putString(LABEL);

//        if(metaName_Value != null)
//        meta_name = sb.putString(metaName_Value);


        editorInfo.activityName_Index=sb.putString(editorInfo.acitivtyName);

        editorInfo.activityValue_Index=sb.addString(editorInfo.activitylabel);

//        if(metaName_Value !=null && meta_value == -1){
//            if(metaValue_Value == null){
//                metaValue_Value="";
//            }
//
//            meta_value = sb.addString(metaValue_Value);//now we have a seat in StringBlock
//        }
    }

    public static class EditorInfo{
        private String acitivtyName;
        private String activitylabel;

        private int activityName_Index;
        private int activityValue_Index;

        private boolean activityNameHasEditor;
        private boolean activityValueHasEditor;

        public EditorInfo(){}

        public EditorInfo(String metaName, String metaValue) {
            this.acitivtyName = metaName;
            this.activitylabel = metaValue;
        }
    }
}
