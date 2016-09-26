package com.luluteam.yjy.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luluteam.yjy.utils.IconUtil;

import java.util.List;

import yjy.luluteam.com.yjy.R;

/**
 * Created by Guan on 2016/7/29.
 */
public class TransFileAdapter extends BaseAdapter {

    private LayoutInflater lf;
    private Bitmap def, music, video, photo, doc, xls, ppt, txt, pdf;
    private List<String> names;
    private List<Integer> sizes;
    private List<Integer> curSizes;
    private List<Integer> ids;
    private List<String> fTypes;
    private ListView listView;
    private Context mContext;

    ViewHolder_TransFileItem viewTransFile;

    public TransFileAdapter(Context context, List<Integer> ids, List<String> names,
                            List<Integer> sizes, List<Integer> curSizes,List<String> fTypes) {
        lf = LayoutInflater.from(context);
        this.ids = ids;
        this.names = names;
        this.sizes = sizes;
        this.curSizes = curSizes;
        this.fTypes=fTypes;
        mContext = context;

    }

    public void updateView(int id, int curSize, int size) {
        this.curSizes.set(id, curSize);
        this.sizes.set(id, size);
    }

    public void setCurrentSize(int id, int curSize) {
        this.curSizes.set(id, curSize);
    }


    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ids.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = lf.inflate(R.layout.view_trans_file_item, null);
            viewTransFile = new ViewHolder_TransFileItem();
            viewTransFile.name = (TextView) convertView.findViewById(R.id.file_name);
            viewTransFile.icon = (ImageView) convertView.findViewById(R.id.file_icon);
            viewTransFile.uploadbar = (ProgressBar) convertView.findViewById(R.id.uploadbar);
            viewTransFile.result = (TextView) convertView.findViewById(R.id.result);
            convertView.setTag(viewTransFile);
        } else {
            viewTransFile = (ViewHolder_TransFileItem) convertView.getTag();
        }
        viewTransFile.uploadbar.setMax((int) sizes.get(position));
        viewTransFile.uploadbar.setProgress(curSizes.get(position));
        float num = (float) curSizes.get(position) / (float) sizes.get(position);
        int result = (int) (num * 100);
        if (viewTransFile.uploadbar.getProgress() == viewTransFile.uploadbar.getMax()) {
            viewTransFile.result.setText("完成");
        } else if (viewTransFile.uploadbar.getProgress() == 0) {
            viewTransFile.result.setText("等待");
        } else {
            viewTransFile.result.setText(result + "%");
        }
        String name = names.get(position);
        if (name.length() > 16) {
            viewTransFile.name.setText(name.substring(0, 6) + "..." +
                    name.substring(name.length() - 6, name.length()));
        } else {
            viewTransFile.name.setText(name);
        }

        //设置文件图标
        String temp = names.get(position).toString();
        //获取文件的类型。现在使用的是文件的后缀。
        //String fType = temp.substring(temp.lastIndexOf(".") + 1, temp.length()).toLowerCase();
        //以后要使用type字段
        String fType=fTypes.get(position).toString();
        viewTransFile.icon.setImageBitmap(IconUtil.getIcon(mContext, fType));
        return convertView;

    }


    private class ViewHolder_TransFileItem {
        public TextView name;
        public ImageView icon;
        public ProgressBar uploadbar;
        public TextView result;
    }


}
