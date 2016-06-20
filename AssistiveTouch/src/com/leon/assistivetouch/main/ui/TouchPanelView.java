package com.leon.assistivetouch.main.ui;

import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leon.assistivetouch.main.R;
import com.leon.assistivetouch.main.bean.KeyItemInfo;
import com.leon.assistivetouch.main.util.L;
import com.leon.assistivetouch.main.util.Util;

/** 
 * 类名      TouchMainView.java
 * 说明   description of the class
 * 创建日期 2012-8-21
 * 作者  LiWenLong
 * Email lendylongli@gmail.com
 * 更新时间  $Date$
 * 最后更新者 $Author$
*/
public class TouchPanelView extends LinearLayout{

	private static final String TAG = "TouchMainView";
	
	private Context mContext;
	
	private View mKeysLayout;
	private View mKeysMain;
	private GridView mKeyGridView;
	private OnKeyClickListener mOnKeyClickListener;
	private KeyGridAdapter mAdapter;
	private boolean isEnableItemText;
	
	public TouchPanelView(Context context) {
		super(context);
		mContext = context;
		init ();
	}
	
	public TouchPanelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init ();
	}
	
	private void init() {
		inflate(mContext, R.layout.touch_panel_view, this);
		mKeysMain = findViewById(R.id.top_view_keys_main);
		mKeysLayout = findViewById(R.id.top_view_keys_layout);
		mKeyGridView = (GridView) findViewById(R.id.key_grid_view);
		// 当点击外面空白部分时直接 隐藏 主窗口
		mKeysMain.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// TODO Auto-generated method stub
				int x = (int) event.getX();
                int y = (int) event.getY();
                Rect rect = new Rect();
                mKeysMain.getGlobalVisibleRect(rect);
                if (rect.contains(x, y)) {
                    if (mOnKeyClickListener != null) {
        				mOnKeyClickListener.onClick(0, new KeyItemInfo("", null, KeyItemInfo.TYPE_KEY, String.valueOf(KeyItemInfo.KEY_HIDE)));
        			}
                }
				return false;
			}
		});
	}
	
	public void setKeyList (List<KeyItemInfo> list) {
		mAdapter = new KeyGridAdapter(mContext, list);
		mKeyGridView.setAdapter(mAdapter);
	}

	public void setOnKeyClickListener (OnKeyClickListener listener) {
		mOnKeyClickListener = listener;
	}
	
	public void notifyDataSetChanged () {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void setEnableItemText (boolean enable) {
		isEnableItemText = enable;
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private class OnKeyItemClickListener implements View.OnClickListener {

		private KeyItemInfo mInfo;
		private int mPosition;
		public OnKeyItemClickListener (int postion, KeyItemInfo info) {
			this.mInfo = info;
			this.mPosition = postion;
		}
		
		@Override
		public void onClick(View v) {
			if (mOnKeyClickListener != null) {
				mOnKeyClickListener.onClick(mPosition, mInfo);
			}
		}
		
	} 

	public abstract interface OnKeyClickListener {
		public abstract void onClick(int position, KeyItemInfo info);
	}
	
	private class KeyGridAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<KeyItemInfo> mList;
		public KeyGridAdapter (Context context, List<KeyItemInfo> list) {
			mList = list;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// 下面这段为了 横竖屏切换
			if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			{  
				//此处相当于布局文件中的Android:layout_gravity属性  
				LinearLayout.LayoutParams lp = (LayoutParams) mKeysLayout.getLayoutParams();
				lp.gravity = Gravity.CENTER;  
				mKeysLayout.setLayoutParams(lp); 
			}  
			else
			{
				//此处相当于布局文件中的Android:layout_gravity属性  
				LinearLayout.LayoutParams lp = (LayoutParams) mKeysLayout.getLayoutParams();
				lp.gravity = Gravity.RIGHT;  
				mKeysLayout.setLayoutParams(lp); 
			}
			
			return mList.size();
		}

		@Override
		public KeyItemInfo getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			final ViewHolder holder;
			if (convertView == null || convertView.getTag() == null) {
				convertView = inflater.inflate(R.layout.key_grid_item, null, false);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.key_item_title_text);
				holder.icon = (ImageView) convertView.findViewById(R.id.key_item_icon_img);
				holder.layout = convertView.findViewById(R.id.key_grid_item_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			KeyItemInfo info = getItem(position);
			holder.title.setVisibility(isEnableItemText ? View.VISIBLE :View.GONE);
			if (info == null) {
				holder.title.setText("");
				holder.icon.setImageBitmap(null);
			} else {
				holder.layout.setOnClickListener(new OnKeyItemClickListener(position, info));
				holder.layout.setBackgroundResource(R.drawable.selector_sysbar_bg);
				if (Util.isStringNull(info.getTitle())) {
					holder.title.setVisibility(View.GONE);
				} else {
					holder.title.setText(info.getTitle());
				}
				//根据不同手机状态显示 不同图标
				if(info.getType() == KeyItemInfo.TYPE_TOOL &&
				Integer.parseInt(info.getData()) == KeyItemInfo.TOOL_NETWORK_MOBILE &&
				Util.isNetWorkMobile(mContext))
				{
					holder.icon.setImageDrawable(info.getIconPressed());
				}
				else
				{
					holder.icon.setImageDrawable(info.getIcon());
				}
			}
			return convertView;
		}
		
		private class ViewHolder {
			TextView title;
			ImageView icon;
			View layout;
		}
	}
}
