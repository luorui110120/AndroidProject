package axmleditor.editor;

import axmleditor.decode.AXMLDoc;
import axmleditor.decode.BXMLNode;
import axmleditor.decode.StringBlock;

/**
 * Created by zl on 15/9/8.
 */
public abstract class BaseEditor<T> implements XEditor {

    public BaseEditor(AXMLDoc doc){
        this.doc=doc;
    }

    protected AXMLDoc doc;

    protected String attrName;
    protected String arrrValue;

    protected int namespace;

    protected int attr_name;
    protected int attr_label;
    protected int attr_value;
    protected int attr_persistent;
    protected int attr_process;

    protected T editorInfo;

    public void setEditorInfo(T editorInfo) {
        this.editorInfo = editorInfo;
    }

    @Override
    public void setEditor(String attrName, String attrValue) {
        this.attrName=attrName;
        this.arrrValue=attrValue;
    }

    @Override
    public void commit() {
        if(editorInfo != null) {
            registStringBlock(doc.getStringBlock());
            editor();
        }
    }

    public abstract String  getEditorName();

    protected abstract void editor();

    protected abstract BXMLNode findNode();

    protected abstract void registStringBlock(StringBlock block);
}
