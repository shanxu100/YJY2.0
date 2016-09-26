package com.luluteam.yjy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luluteam.yjy.manager.BrowseFileAdapter;
import com.luluteam.yjy.base.Constant;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yjy.luluteam.com.yjy.R;

public class BrowseFileActivity extends Activity {


    private Button back_btn; // 返回按钮
    private Button function_btn;
    private TextView mPath; // 标题栏显示的当前路径

    private Button select_all_btn; // 全选按钮
    private Button upload_btn; // 上传按钮
    private List<String> items = null; // 文件
    private List<String> paths = null; // 文件路径
    private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String curPath = null; // 当前路径
    private String parentPath = null; // 当前父路径

    private ListView file_list; // 列表
    private int checkNum = 0; // 记录选中数量
    private BrowseFileAdapter adapter;
    private boolean selectall_flag = true;//是否全选的flag
    private List<File> lfile = null;
    private String TAG="BrowseFileActivity";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_file);
        Init();
    }

    private void Init()
    {
        mContext=this;

        curPath = rootPath; // 当前路径为根目录
        mPath = (TextView) this.findViewById(R.id.ui_title_word_tv);
        back_btn = (Button) findViewById(R.id.ui_title_back_btn);
        function_btn=(Button)findViewById(R.id.ui_title_function_btn) ;
        file_list = (ListView) this.findViewById(R.id.file_list);
        select_all_btn = (Button) findViewById(R.id.select_all_btn);
        upload_btn = (Button) findViewById(R.id.upload_btn);

        mPath.setText(curPath);

        getFileDir(rootPath);

        select_all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setListView(file_list);
                List<Integer> itemsIndex = new ArrayList<Integer>();
                if(selectall_flag)
                {
                    //全选
                    int temp = 0;
                    for (int i = 0; i < paths.size(); i++) {
                        File f = new File(paths.get(i));
                        if (!f.isDirectory()) {
                            //列表中，第i个item状态为“选中true”
                            adapter.getIsSelected().put(i, true);
                            temp++;
                            itemsIndex.add(i);
                        }
                    }
                    if (temp != 0) {
                        checkNum = temp;
                        upload_btn.setText("上传" + checkNum);
                        upload_btn.setTextColor(Color.RED);
                        select_all_btn.setText("取消全选");

                    }
                    selectall_flag = false;

                }else {
                    //取消全选
                    int temp = 0;
                    for (int i = 0; i < paths.size(); i++) {
                        File f = new File(paths.get(i));
                        if (!f.isDirectory()) {
                            //列表中，第i个item状态为“未选中false”
                            adapter.getIsSelected().put(i, false);
                            itemsIndex.add(i);
                        }
                    }
                    checkNum = 0;
                    upload_btn.setText("上传");
                    upload_btn.setTextColor(Color.WHITE);
                    select_all_btn.setText("全选");
                    selectall_flag = true;
                }
                // 刷新ListView数据
                adapter.notifyDataSetChanged();

            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> ufPath = new ArrayList<String>();

                for (int i = 0; i < paths.size(); i++) {
                    File f = new File(paths.get(i));
                    if(f.isFile()&&adapter.getIsSelected().get(i))
                    {
                        //如果f是文件，且被选中。则执行以下步骤
                        ufPath.add(paths.get(i));// 记录选中文件的路径
                        Log.e(TAG,paths.get(i));

                    }

                }

                if (ufPath.size() != 0 && ufPath.get(0) != null) {
                    Intent intent = new Intent();
                    intent.setClass(BrowseFileActivity.this,
                            TransFileActivity.class);
                    //设置参数
                    intent.putExtra(Constant.STRING_WHAT, Constant.STRING_UPLOAD);
                    intent.putExtra(Constant.STRING_PATHS, (Serializable) ufPath);

                    startActivity(intent);
                    BrowseFileActivity.this.finish();


                } else {
                    Toast.makeText(BrowseFileActivity.this,"请选择",Toast.LENGTH_SHORT).show();
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBack();
            }
        });

        file_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View v,
                                    int position, long id) {
                // TODO Auto-generated method stub
                File file = new File(paths.get(position));
                if (file.isDirectory()) {
                    // 如果是文件夹就再执行getFileDir()
                    checkNum = 0;
                    upload_btn.setText("上传");
                    upload_btn.setTextColor(Color.WHITE);
                    select_all_btn.setText("全选");
                    selectall_flag = true;
                    getFileDir(paths.get(position));
                } else {
                    // 如果是文件，则选中或取消选择
                    BrowseFileAdapter.ViewHolder_BrowseFileItem holder = (BrowseFileAdapter.ViewHolder_BrowseFileItem) v.getTag();
                    holder.item_cb.toggle();
                    adapter.getIsSelected().put(position,
                            holder.item_cb.isChecked());

                    if (holder.item_cb.isChecked() == true) {
                        checkNum++;
                    } else {
                        checkNum--;
                    }
                    if (checkNum != 0) {
                        upload_btn.setText("上传" + checkNum);
                        upload_btn.setTextColor(Color.RED);
                    } else {
                        upload_btn.setText("上传");
                        upload_btn.setTextColor(Color.WHITE);
                    }
                }
            }
        });


    }



    /**
     * 列出当前目录下文件列表
     *
     * @param filePath 当前路径
     *
     */
    public void getFileDir(String filePath) {

        items = new ArrayList<String>();
        List<String> items1 = new ArrayList<String>();
        List<String> items2 = new ArrayList<String>();
        paths = new ArrayList<String>();
        List<String> paths1 = new ArrayList<String>();
        List<String> paths2 = new ArrayList<String>();
        File f = new File(filePath);
        //f.listFiles().
        File[] files = f.listFiles();
        if (!filePath.equals(rootPath)) {
            parentPath = f.getParent();
        }
        curPath = filePath;
        if (curPath.length() > 20) {
            curPath = curPath.substring(0, 7)
                    + "..."
                    + curPath.substring(curPath.lastIndexOf("/"),
                    curPath.length());
        }
        mPath.setText(curPath);
        for (int i = files.length - 1; i >= 0; i--) {
            File file = files[i];
            String name = file.getName();
            if (file.isDirectory()) {
                if (name.charAt(0) != '.') {
                    if (name.length() > 16) {
                        items1.add(name.substring(0, 4)
                                + "..."
                                + name.substring(name.length() - 6,
                                name.length()));
                    } else {
                        items1.add(name);
                    }
                    paths1.add(file.getPath());
                }
            } else {
                if (name.charAt(0) != '.') {
                    if (name.length() > 16) {
                        items2.add(name.substring(0, 4)
                                + "..."
                                + name.substring(name.length() - 6,
                                name.length()));
                    } else {
                        items2.add(name);
                    }
                    paths2.add(file.getPath());
                }
            }
        }
        Collections.sort(items1, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(paths1, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(items2, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(paths2, String.CASE_INSENSITIVE_ORDER);
        items.addAll(items1);
        items.addAll(items2);
        paths.addAll(paths1);
        paths.addAll(paths2);
        adapter = new BrowseFileAdapter(this, items, paths);
        file_list.setAdapter(adapter);
    }

    public void doBack()
    {
        if (curPath.equals(rootPath)) {
            BrowseFileActivity.this.finish();
        } else {
            checkNum = 0;
            upload_btn.setText("上传");
            upload_btn.setTextColor(Color.WHITE);
            getFileDir(parentPath);
        }
    }



    // 返回键添加监听,返回主界面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doBack();
        }
        return true;
    }


}
