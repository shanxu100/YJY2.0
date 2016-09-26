package com.luluteam.yjy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luluteam.yjy.base.Constant;
import com.luluteam.yjy.manager.MoveAdapter;
import com.luluteam.yjy.manager.OkHttpManager;
import com.luluteam.yjy.model.GuanCourseware;
import com.luluteam.yjy.model.GuanCoursewareList;
import com.luluteam.yjy.model.OperationResult;
import com.luluteam.yjy.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import yjy.luluteam.com.yjy.R;

public class MoveActivity extends Activity {

    private ListView filelist;

    private List<GuanCourseware> items;
    private MoveAdapter adapter;
    private Stack<HashMap<String, String>> parentfolder;//建立一个栈，用于存储父文件夹中表示目录结构的JSON
    private String currtenJson = "";
    private String currtenFolder = "/";
    private String rootJson = "";


    private Button function_btn;// 弹出功能菜单按钮
    private TextView title_string;
    private Button back_btn;
    private Button move_sure_btn;
    private Button move_cancle_btn;


    private String TAG = "MoveActivity";
    private AlertDialog dlg;
    private Context mContext;
    private String fileId;


    //获取文件列表的Handler
    private Handler mHandler = new Handler() {
        //用于获取文件列表的handler
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString(Constant.STRING_RESPONSE);//json串
            String folder = msg.getData().getString(Constant.STRING_FOLDER);
            boolean clickin = msg.getData().getBoolean(Constant.STRING_CLICKIN);
            Log.e(TAG + "mHandler:\t", result);

            //如果没有接收到数据，直接返回
            if (null == result) {
                return;
            }
            GuanCoursewareList list = JSONUtil.forCoursewareList(result);

            if (list.getResult()) {
                items = list.getFiles();
                if (null == adapter) {
                    //第一次初始化adapter
                    adapter = new MoveAdapter(mContext, items);
                    filelist.setAdapter(adapter);
                } else {
                    //以后更新整个列表时，不用再new一个adapter，而是更新数据集就好
                    adapter.setItems(items);
                    adapter.notifyDataSetChanged();
                }

                //判断动作是“点进去”，则入栈
                if (clickin) {
                    HashMap<String, String> tmp_map = new HashMap<String, String>();
                    tmp_map.put(Constant.STRING_FOLDER, currtenFolder);
                    tmp_map.put(Constant.STRING_JSON, currtenJson);
                    parentfolder.push(tmp_map);
                }

                //入栈成功之后，在修改当前显示的json和当前所在的folder
                currtenJson = result;
                currtenFolder = folder;
                title_string.setText(currtenFolder);
            }


        }
    };

    //相应“移动文件”的接口返回的Handler
    private Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString(Constant.STRING_RESPONSE);
            OperationResult or = JSONUtil.forOperationResult(result);
            //解析result
            if (or.getResult())//这里改成解析result显示的“是否移动成功”
            {
                MoveActivity.this.setResult(Activity.RESULT_OK, getIntent());
                MoveActivity.this.finish();
            } else {
                Toast.makeText(mContext, "移动失败", Toast.LENGTH_SHORT);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);
        Init();
    }

    private void Init() {
        Intent intent = getIntent();
        rootJson = intent.getStringExtra(Constant.STRING_ROOTJSON);
        fileId = intent.getStringExtra(Constant.STRING_FILEID);//记录所要移动的文件的Id

        Log.e(TAG, "currentfolder:" + currtenFolder + " fileId:" + fileId + " currentjson:" + currtenJson);

        mContext = this;
        parentfolder = new Stack<>();
        filelist = (ListView) findViewById(R.id.move_file_list);
        this.function_btn = (Button) findViewById(R.id.ui_title_function_btn);
        this.title_string = (TextView) findViewById(R.id.ui_title_word_tv);
        this.title_string.setTextColor(Color.RED);
        this.back_btn = (Button) findViewById(R.id.ui_title_back_btn);
        this.move_sure_btn = (Button) findViewById(R.id.move_sure_btn);
        this.move_cancle_btn = (Button) findViewById(R.id.move_cancle_btn);

        //获取文件列表
        doFillData();

        //为每一个item添加点击的监听
        filelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (items.get(position).getFileType().equals(Constant.STRING_FOLDER)) {
                    //如果点击的item是文件夹
                    //以docId为参数，调用okHttp来获取该文件夹下的目录结构。
                    //但此次写成静态的，用来测试
                    Message msg = Message.obtain();
                    msg.getData().putString(Constant.STRING_RESPONSE, Constant.getStaticJSON());//以后这里要改成新获得的Json
                    msg.getData().putString(Constant.STRING_FOLDER, items.get(position).getFileOriginalName());//这里表示所点击的那个文件夹的名字
                    msg.getData().putBoolean(Constant.STRING_CLICKIN, true);//标记是“点进去”这个动作
                    mHandler.sendMessage(msg);

                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBack();
            }
        });

        move_cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveActivity.this.setResult(Activity.RESULT_CANCELED);
                MoveActivity.this.finish();
            }
        });

        move_sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //连接服务器，进行移动
                HashMap<String, String> param = new HashMap<String, String>();
                param.put(Constant.STRING_FILEID, fileId);
                param.put(Constant.STRING_PARENTFOLDER, currtenFolder);
                OkHttpManager.CommonPostAsyn(Constant.URL_MOVE, param, new OkHttpManager.myCallback() {
                    @Override
                    public void onCallBack(String result) {
                        Log.e(TAG, result);
                        Message msg = Message.obtain();
                        msg.getData().putString(Constant.STRING_RESPONSE, result);
                        mHandler2.sendMessage(msg);
                    }
                });


            }
        });


    }

    //获取文件列表
    private void doFillData() {
        Message msg = Message.obtain();
        msg.getData().putString(Constant.STRING_RESPONSE, rootJson);//显示所在文件夹中的列表的Json
        msg.getData().putString(Constant.STRING_FOLDER, currtenFolder);//当前所在的文件夹
        mHandler.sendMessage(msg);
    }


    private void doBack() {

        if (!parentfolder.isEmpty()) {
            //查看此时的栈顶元素
            HashMap<String, String> tmp_map = parentfolder.pop();
            currtenJson = tmp_map.get(Constant.STRING_JSON);
            currtenFolder = tmp_map.get(Constant.STRING_FOLDER);
            title_string.setText(currtenFolder);
            Message msg = Message.obtain();
            msg.getData().putString(Constant.STRING_RESPONSE, currtenJson);
            msg.getData().putString(Constant.STRING_FOLDER, currtenFolder);
            mHandler.sendMessage(msg);
            Log.e(TAG, "栈非空" + parentfolder.size());
        } else {
            Log.e(TAG, "栈空");
            this.finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doBack();
        }
        return true;
    }


}
