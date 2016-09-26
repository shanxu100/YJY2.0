package com.luluteam.yjy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luluteam.yjy.base.Constant;
import com.luluteam.yjy.manager.OkHttpManager;
import com.luluteam.yjy.model.GuanCourseware;
import com.luluteam.yjy.model.GuanCoursewareList;
import com.luluteam.yjy.utils.DialogUtils;
import com.luluteam.yjy.utils.IconUtil;
import com.luluteam.yjy.utils.JSONUtil;

import java.util.HashMap;

import yjy.luluteam.com.yjy.R;

public class DetailFileActivity extends Activity {

    private int position=0;
    private ImageView coursewareicon;
    private TextView name;
    private TextView uploader;
    private TextView level;
    private EditText cost;
    private Button cancle_btn;
    private Button sure_btn;

    private Button function_btn;// 添加课件按钮
    private TextView title_string;
    private Button back_btn;

    private GuanCourseware courseware;
    private Intent intent;

    private String TAG="DetailFileActivity";
    private AlertDialog dlg;
    private Context mContext;

    Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            String result=msg.getData().getString(Constant.STRING_RESPONSE);
            GuanCoursewareList list= JSONUtil.forCoursewareList(result);
            if(list.getResult())
            {
                //成功
                //把存有courseware的intent传递回去。这个intent和传过来的intent是同一个。
                //但里面的内容在doRename()等方法中被更新了
                intent.putExtra(Constant.STRING_COURSEWARE,courseware);
                DetailFileActivity.this.setResult(Activity.RESULT_OK,getIntent());
                DetailFileActivity.this.finish();
            }else {
                Toast.makeText(mContext,"修改失败",Toast.LENGTH_SHORT);
            }

        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_courseware);
        Init();
    }

    private void Init()
    {
        mContext=this;
        intent=getIntent();
        courseware=(GuanCourseware) intent.getExtras().get(Constant.STRING_COURSEWARE);

        if(null==courseware)
        {
            return;
        }

        coursewareicon = (ImageView) findViewById(R.id.edit_icon);
        name = (TextView) findViewById(R.id.edit_name);
        uploader = (TextView) findViewById(R.id.edit_uploder);
        level = (TextView) findViewById(R.id.edit_level);
        cost = (EditText) findViewById(R.id.edit_cost);

        cancle_btn = (Button) findViewById(R.id.edit_cancle);
        sure_btn = (Button) findViewById(R.id.edit_sure);

        this.function_btn = (Button) findViewById(R.id.ui_title_function_btn);
        this.title_string = (TextView) findViewById(R.id.ui_title_word_tv);
        this.back_btn = (Button) findViewById(R.id.ui_title_back_btn);


        //===设置主体内容=========
        String temp = courseware.getFileOriginalName().toString();
        //获取文件的类型。现在使用的是文件的后缀。
        //String fType = temp.substring(temp.lastIndexOf(".") + 1, temp.length()).toLowerCase();
        //以后要使用courseware的type字段
        String fType=courseware.getFileType().toString();
        name.setText(temp);
        coursewareicon.setImageBitmap(IconUtil.getIcon(this,fType));


        //===设置标题====
        back_btn.setVisibility(View.VISIBLE);
        function_btn.setVisibility(View.GONE);
        title_string.setText("粤教云客户端——管理课件");


        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRename(name.getText().toString());}
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBack();
            }
        });
        sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSure();

            }
        });
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBack();
            }
        });



    }


    //在点击确定按钮后，统一更新各种信息
    private void doSure()
    {

        View.OnClickListener surelistenser=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用okhttp，向上提交修改
                HashMap<String,String> param=new HashMap<>();
                //需要更新的属性依次往下面写
                param.put(Constant.STRING_FILEID,courseware.getFileId());
                param.put(Constant.STRING_FILEORIGINALNAME,name.getText().toString());//重命名
                //param.put("originalName",name.getText().toString());
                //param.put("originalName",name.getText().toString());//重命名
                Log.e(TAG,name.getText().toString());

                OkHttpManager.CommonPostAsyn(Constant.URL_UPDATEFILE, param, new OkHttpManager.myCallback() {
                    @Override
                    public void onCallBack(String result) {
                        Log.e(TAG,result);
                        Message msg=Message.obtain();
                        msg.getData().putString(Constant.STRING_RESPONSE,result);
                        mHandler.sendMessage(msg);
                    }
                });
                dlg.cancel();

            }
        };

        View.OnClickListener canclelistener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
            }
        };

        dlg= DialogUtils.getYNDialog(this,"您确定保存修改吗？",surelistenser,canclelistener);


    }

    private void doBack()
    {

        View.OnClickListener suerlistener= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
                DetailFileActivity.this.finish();
            }
        };

        View.OnClickListener canclelistener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
            }
        };

        dlg=DialogUtils.getYNDialog(this,"您确定放弃编辑吗?",suerlistener,canclelistener);

    }

    //重命名，该方法并不是更新服务器端的数据。而是更改显示的数据
    private void doRename(String oldname)
    {

        View.OnClickListener surelistener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newname=((EditText)dlg.getWindow().findViewById(R.id.alert_singleline_content)).getText().toString();
                dlg.cancel();
                /*
                *注意，要更新控件所显示的名字，同时也要更新内存中courseware实体中的信息。成对的写，以免遗漏
                 */
                name.setText(newname);//设置显示新名字
                courseware.setFileOriginalName(newname);//修改内存中保存的实体courseware的文件名
            }
        };

        View.OnClickListener canclelistener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
            }
        };

        dlg=DialogUtils.getSingleLineInput(this,oldname,surelistener,canclelistener);
    }

    //更新其他属性
    private void doXxx(String xxx)
    {
        /*
         *注意，要更新控件所显示的名字，同时也要更新内存中courseware实体中的信息。成对的写，以免遗漏
         */
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            doBack();
        }

        return true;
    }




}
