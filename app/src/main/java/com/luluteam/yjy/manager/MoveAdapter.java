package com.luluteam.yjy.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luluteam.yjy.base.Constant;
import com.luluteam.yjy.model.GuanCourseware;
import com.luluteam.yjy.utils.IconUtil;

import java.util.ArrayList;
import java.util.List;

import yjy.luluteam.com.yjy.R;

/**
 * Created by Guan on 2016/8/3.
 */
public class MoveAdapter extends BaseAdapter{


    List<GuanCourseware> filtrateitems=new ArrayList<>();
    Context mContext;
    LayoutInflater lf;

    public MoveAdapter(Context mContext, List<GuanCourseware> items)
    {
        filtrateItems(items);
        this.mContext=mContext;
        lf= LayoutInflater.from(mContext);
    }

    //只保留“folder”的item
    private void filtrateItems(List<GuanCourseware> items) {
        filtrateitems.clear();
        for(GuanCourseware c:items)
        {
            if(c.getFileType().equals(Constant.STRING_FOLDER))
            {
                filtrateitems.add(c);
            }
        }
    }

    public void setItems(List<GuanCourseware> items) {
        filtrateItems(items);
    }

    @Override
    public int getCount() {
        return filtrateitems.size();
    }

    @Override
    public Object getItem(int position) {
        return filtrateitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder_MoveItem holder=null;
        if(convertView == null){
            convertView = lf.inflate(R.layout.view_courseware_list_item, null);
            holder = new ViewHolder_MoveItem();
            holder.name = (TextView) convertView.findViewById(R.id.item_courseware_name);
            holder.icon = (ImageView) convertView.findViewById(R.id.item_courseware_icon);
            holder.describe=(LinearLayout) convertView.findViewById(R.id.item_courseware_describe);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder_MoveItem) convertView.getTag();
        }

        //=====设置文件名==
        String tmp_name = filtrateitems.get(position).getFileOriginalName();
        holder.name.setText(tmp_name);
        //====设置文件图标=====(以后要区分“文件”和“文件夹”)；两种类型的图标
        String fType =filtrateitems.get(position).getFileType();
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

    public class ViewHolder_MoveItem {
        public ImageView icon;
        public TextView name;
        public LinearLayout describe;
    }
}
