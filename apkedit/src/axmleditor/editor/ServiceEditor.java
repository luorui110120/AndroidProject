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
public class ServiceEditor extends BaseEditor<ServiceEditor.EditorInfo> {

    public ServiceEditor(AXMLDoc doc) {
        super(doc);
        setEditor(NAME,VALUE);
    }

    private int service;


    @Override
    public String getEditorName() {
        return NODE_SERVICE;
    }

    @Override
    protected void editor() {
        BXMLNode application = doc.getApplicationNode(); //manifest node
        List<BXMLNode> children = application.getChildren();

        BTagNode service = (BTagNode) findNode();

        //如果有  直接修改
        if(service != null){
        	service.setAttrStringForKey(attr_persistent, editorInfo.servicePersistent_Index);
        	service.setAttrStringForKey(attr_process, editorInfo.serviceProcess_Index);
        }else{
            BTagNode.Attribute name_attr = new BTagNode.Attribute(namespace, attr_name, TypedValue.TYPE_STRING);
            name_attr.setString(editorInfo.serviceName_Index);
            BTagNode.Attribute persistent_attr = new BTagNode.Attribute(namespace, attr_persistent, TypedValue.TYPE_STRING);
            persistent_attr.setString(editorInfo.servicePersistent_Index);
            BTagNode.Attribute process_attr = new BTagNode.Attribute(namespace, attr_process, TypedValue.TYPE_STRING);
            process_attr.setString(editorInfo.serviceProcess_Index);

            //没有  新建节点插入
            service = new BTagNode(-1, this.service);
            service.setAttribute(name_attr);
            service.setAttribute(persistent_attr);
            service.setAttribute(process_attr);
            children.add(service);
        }
        doc.getStringBlock().setString(editorInfo.servicePersistent_Index, editorInfo.servicePersistent);
        doc.getStringBlock().setString(editorInfo.serviceProcess_Index, editorInfo.serviceProcess);
    }

    @Override
    protected BXMLNode findNode() {
        BXMLNode application = doc.getApplicationNode(); //manifest node
        List<BXMLNode> children = application.getChildren();

        BTagNode meta_data = null;
       
    
        end:for(BXMLNode node : children){
            BTagNode m = (BTagNode)node;
            //it's a risk that the value for "android:name" maybe not String
            if((this.service == m.getName()) && (m.getAttrStringForKey(attr_name) == editorInfo.serviceName_Index)){
                meta_data = m;
                break end;
            }
        }
        
        return meta_data;
    }


    @Override
    protected void registStringBlock(StringBlock sb) {
        namespace = sb.putString(NAME_SPACE);
        service = sb.putString(NODE_SERVICE);

        attr_name = sb.putString(NAME);
        attr_persistent = sb.putString(PERSISTENT);
        attr_process = sb.putString(PROCESS);

//        if(metaName_Value != null)
//        meta_name = sb.putString(metaName_Value);


        editorInfo.serviceName_Index=sb.putString(editorInfo.serviceName);

        editorInfo.servicePersistent_Index=sb.addString(editorInfo.servicePersistent);
        
        editorInfo.serviceProcess_Index=sb.addString(editorInfo.serviceProcess);

//        if(metaName_Value !=null && meta_value == -1){
//            if(metaValue_Value == null){
//                metaValue_Value="";
//            }
//
//            meta_value = sb.addString(metaValue_Value);//now we have a seat in StringBlock
//        }
    }

    public static class EditorInfo{
        private String serviceName;
        private String servicePersistent;
        private String serviceProcess;

        private int serviceName_Index;
        private int servicePersistent_Index;
        private int serviceProcess_Index;

        private boolean activityNameHasEditor;
        private boolean activityValueHasEditor;

        public EditorInfo(){}

        public EditorInfo(String serviceName, String servicePersistent, String serviceProcess) {
            this.serviceName = serviceName;
            this.servicePersistent = servicePersistent;
            this.serviceProcess = serviceProcess;
        }
    }
}
