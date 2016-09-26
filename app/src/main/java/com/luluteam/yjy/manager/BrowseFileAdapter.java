package com.luluteam.yjy.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.luluteam.yjy.utils.IconUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import yjy.luluteam.com.yjy.R;


public class BrowseFileAdapter extends BaseAdapter{
	
	private LayoutInflater lf;
	private static Bitmap floder, def, music, video, photo, doc, xls, ppt, txt, pdf;
	private List<String> names;
	private List<String> paths;
	//map中的每一个键值对，用于标记每一个item是否被选中。
	private static HashMap<Integer, Boolean> isSelected;
	private CheckBox item_cb;
	private ListView listView;
	private String TAG="BrowseFileAdapter";
	private Context mContext;

	
	public BrowseFileAdapter(Context context, List<String> it, List<String> pa){
		lf = LayoutInflater.from(context);
		names = it;
		paths = pa;
		isSelected = new HashMap<Integer, Boolean>();
		this.mContext=context;
		//初始化isSelected
		initData();
		
	}
	
	private void initData(){
		for(int i = 0; i < names.size(); i ++){
			getIsSelected().put(i, false);
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return names.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder_BrowseFileItem holder=null;
		if(convertView == null){
			convertView = lf.inflate(R.layout.view_browse_file_item, null);
			holder = new ViewHolder_BrowseFileItem();
			holder.name = (TextView) convertView.findViewById(R.id.file_name);
			holder.icon = (ImageView) convertView.findViewById(R.id.file_icon);
			holder.item_cb = (CheckBox) convertView.findViewById(R.id.item_cb);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder_BrowseFileItem) convertView.getTag();
		}
		File f = new File(paths.get(position).toString());

		//=====设置文件名==
		holder.name.setText(names.get(position));
		//====设置文件图标=====
		if(f.isDirectory()){
			//若为文件，则无选中标记
			holder.item_cb.setVisibility(View.GONE);
			holder.icon.setImageBitmap(IconUtil.getIcon(mContext,"folder"));
		}
		else{
			String temp = f.getName().toString();
			String fType = temp.substring(temp.lastIndexOf(".") + 1, temp.length()).toLowerCase();
			holder.icon.setImageBitmap(IconUtil.getIcon(mContext,fType));
			//如果是文件，则在末尾有选择图标
			holder.item_cb.setVisibility(View.VISIBLE);
			holder.item_cb.setChecked(getIsSelected().get(position));
		}
		return convertView;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}


	public void setListView(ListView listview) {
		this.listView = listview;
	}


	//获取图标类型


	//listview的每个item包含以下三部分：名称、图标、是否选中的选择框
	public class ViewHolder_BrowseFileItem {
		public TextView name;
		public ImageView icon;
		public CheckBox item_cb;
	}

}
