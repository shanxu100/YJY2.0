package com.luluteam.yjy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.luluteam.yjy.base.Constant;
import com.luluteam.yjy.manager.OkHttpManager;
import com.luluteam.yjy.manager.TransFileAdapter;
import com.luluteam.yjy.utils.DialogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yjy.luluteam.com.yjy.R;

public class TransFileActivity extends Activity {

    private Button back_btn;
    private TextView title_string;
    private Button function_btn;
    private ListView trans_list;
    private TransFileAdapter adapter;
    private List<String> names=new ArrayList<>();
    private List<Integer> sizes=new ArrayList<>();
    private List<Integer> curSizes=new ArrayList<>();
    private List<Integer> ids=new ArrayList<>();
    private List<String> fTypes=new ArrayList<>();
    private String TAG = "TransFileActivity";

    AlertDialog dlg;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int curSize = msg.getData().getInt(Constant.STRING_CURSIZE);
            int id = msg.getData().getInt(Constant.STRING_ID);
            adapter.setCurrentSize(id,curSize);
            adapter.notifyDataSetChanged();
            Log.e(TAG,"id:"+id+"\t"+curSize*100/sizes.get(id)+"%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_file);
        Init();
    }



    private void Init() {


        //标题栏三个控件
        back_btn = (Button) this.findViewById(R.id.ui_title_back_btn);
        function_btn = (Button) this.findViewById(R.id.ui_title_function_btn);
        title_string = (TextView) this.findViewById(R.id.ui_title_word_tv);

        trans_list = (ListView) this.findViewById(R.id.trans_list);

        //设置标题栏
        back_btn.setVisibility(View.VISIBLE);
        function_btn.setVisibility(View.GONE);
        title_string.setText("粤教云客户端——文件传输列表");

        //返回按钮监听事件
        back_btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                doBack();

            }

        });

        //辨别该界面是下载还是上传
        String what = getIntent().getStringExtra(Constant.STRING_WHAT);
        if (what.equals(Constant.STRING_DOWNLOAD)) {
            DownloadFile();
        } else if (what.equals(Constant.STRING_UPLOAD)) {
            UploadFile();
        }






    }

    public void DownloadFile()
    {

        //获取参数
        String downloadUrl=getIntent().getStringExtra(Constant.STRING_FILEUrl);
        String filename=getIntent().getStringExtra(Constant.STRING_FILEORIGINALNAME);
        String fType=getIntent().getStringExtra(Constant.STRING_FILETYPE);
        final int size=getIntent().getIntExtra(Constant.STRING_FILESIZE,0);

        Log.e(TAG,"fileurl:"+downloadUrl);
        Log.e(TAG,"filename:"+filename);
        Log.e(TAG,"size:"+size);
        Log.e(TAG,"fType:"+fType);

        //由于是单文件下载，所以这里参数是固定的
        ids.add(0);
        names.add(filename);
        sizes.add(size);
        curSizes.add(0);
        fTypes.add(fType);


        adapter=new TransFileAdapter(this,ids,names,sizes,curSizes,fTypes);
        trans_list.setAdapter(adapter);

        //下载。。。。。。。。。。。
        OkHttpManager.Download(downloadUrl, Constant.DOWNLOADDIR, filename, new OkHttpManager.myProgressListener() {
            @Override
            public void onProgress(long totalBytes, long count, boolean done, int id) {
                //调用的时候写死了，一下三个参数不起作用：totalBytes= -1，done=false，id= -1；
                //只有count是起作用的
                //totalBytes=size;
                //Log.v(TAG,"id:"+id+"\t"+count * 100 / totalBytes + "%");
                Message msg=Message.obtain();
                msg.getData().putInt(Constant.STRING_CURSIZE,(int)count);
                msg.getData().putInt(Constant.STRING_ID,0);
                mHandler.sendMessage(msg);

            }
        });
    }

    public void UploadFile()
    {
        // 用Serializable方法在activity之间传递List<Object>参数
        //获取“保存多个文件的路径的list”的参数
        List<String> paths = (List<String>) getIntent().getSerializableExtra(Constant.STRING_PATHS);

        File[] files=new File[paths.size()];
        String[] filekeys=new String[paths.size()];


        //这个参数为测试使用，是个临时的
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constant.STRING_OWNERID, Constant.OWNERID);

        for(int i=0;i<paths.size();i++)
        {
            File tmp_f=new File(paths.get(i));
            files[i]=tmp_f;
            filekeys[i]="file";
            //filekeys[i]=tmp_f.getOriginalName();

            ids.add(i);
            names.add(tmp_f.getName());
            curSizes.add(0);
            sizes.add((int) tmp_f.length());

            String temp = tmp_f.getName().toString();
            String fType = temp.substring(temp.lastIndexOf(".") + 1, temp.length()).toLowerCase();
            fTypes.add(fType);

        }

        adapter=new TransFileAdapter(this,ids,names,sizes,curSizes,fTypes);
        trans_list.setAdapter(adapter);
        OkHttpManager.Upload(Constant.URL_UPLOAD, files, filekeys, params, new OkHttpManager.myProgressListener() {
            @Override
            public void onProgress(long totalBytes, long count, boolean done, int id) {
                //Log.v(TAG,"id:"+id+"\t"+count * 100 / totalBytes + "%");
                Message msg=Message.obtain();//使用这样的方式获取一个空的Message
                msg.getData().putInt(Constant.STRING_CURSIZE,(int)count);
                msg.getData().putInt(Constant.STRING_ID,id);
                mHandler.sendMessage(msg);
            }
        });


    }

    public void doBack()
    {

        View.OnClickListener suerlistener= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
                TransFileActivity.this.finish();
            }
        };

        View.OnClickListener canclelistener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
            }
        };

        dlg= DialogUtils.getYNDialog(this,"您确定要结束传输吗?",suerlistener,canclelistener);

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
