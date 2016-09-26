package com.luluteam.yjy.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luluteam.yjy.model.GuanCourseware;
import com.luluteam.yjy.utils.IconUtil;

import java.util.List;

import yjy.luluteam.com.yjy.R;

/**
 * Created by Guan on 2016/7/29.
 */
public class CoursewareListAdapter extends BaseAdapter {

    List<GuanCourseware> items;
    Context mContext;
    LayoutInflater lf;

    public CoursewareListAdapter(Context mContext, List<GuanCourseware> items)
    {
        this.items=items;
        this.mContext=mContext;

        lf= LayoutInflater.from(mContext);
    }

    public void setItem(int position, GuanCourseware courseware) {
        if (null==courseware)
        {
            items.remove(position);
        }else {
            items.set(position, courseware);
        }
    }

    public void setItems(List<GuanCourseware> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder_CoursewareListItem holder=null;
        if(convertView == null){
            convertView = lf.inflate(R.layout.view_courseware_list_item, null);
            holder = new ViewHolder_CoursewareListItem();
            holder.name = (TextView) convertView.findViewById(R.id.item_courseware_name);
            holder.icon = (ImageView) convertView.findViewById(R.id.item_courseware_icon);
            holder.describe=(LinearLayout) convertView.findViewById(R.id.item_courseware_describe);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder_CoursewareListItem) convertView.getTag();
        }

        //=====设置文件名==
        String tmp_name = items.get(position).getFileOriginalName();
        holder.name.setText(tmp_name);
        //====设置文件图标=====(以后要区分“文件”和“文件夹”)；两种类型的图标
        String fType =items.get(position).getFileType();
        if(fType.equals("folder"))
        {
            //如果是文件夹类型的文件，则布局下方用于描述的layout不可见。只有具体的单个文件才有描述的layout
            holder.describe.setVisibility(View.INVISIBLE);
        }else
        {
            holder.describe.setVisibility(View.VISIBLE);
        }

        holder.icon.setImageBitmap(IconUtil.getIcon(mContext,fType));

        return convertView;

    }

    public class ViewHolder_CoursewareListItem {
        public ImageView icon;
        public TextView name;
        public LinearLayout describe;
    }
}
