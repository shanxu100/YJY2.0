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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.luluteam.yjy.base.Constant;
import com.luluteam.yjy.manager.CoursewareListAdapter;
import com.luluteam.yjy.manager.OkHttpManager;
import com.luluteam.yjy.model.GuanCourseware;
import com.luluteam.yjy.model.GuanCoursewareList;
import com.luluteam.yjy.utils.DialogUtils;
import com.luluteam.yjy.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import yjy.luluteam.com.yjy.R;

public class CoursewareListActivity extends Activity {

    private ListView filelist;

    //private List<Courseware> items;
    private List<GuanCourseware> items;
    private CoursewareListAdapter adapter;
    private Stack<HashMap<String, String>> parentfolder;//建立一个栈，用于存储父文件夹中表示目录结构的JSON
    private String currtenJson = "";
    private String currtenFolder = "/";

    private String rootJson = "";


    private Button function_btn;// 弹出功能菜单按钮
    private TextView title_string;
    private Button back_btn;


    private String TAG = "CoursewareListActivity";
    private AlertDialog dlg;
    private Context mContext;


    //获取文件列表的Handler
    private Handler mHandler = new Handler() {
        //用于获取文件列表的handler
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString(Constant.STRING_RESPONSE);
            String folder = msg.getData().getString(Constant.STRING_FOLDER);
            boolean clickin = msg.getData().getBoolean(Constant.STRING_CLICKIN);
            Log.e(TAG + "mHandler:\t", result);

            //如果没有接收到数据，直接返回
            if (null == result) {
                return;
            }

            GuanCoursewareList G_list = JSONUtil.forCoursewareList(result);

            if (G_list.getResult()) {
                items = G_list.getFiles();
                if (null == adapter) {
                    //第一次初始化adapter
                    adapter = new CoursewareListAdapter(mContext, items);
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

                //在修改当前显示的json和当前所在的folder
                currtenJson = result;
                currtenFolder = folder;
                title_string.setText(currtenFolder);
            }


        }
    };


    //更新文件列表中某一个item的Handler
    Handler mHandler2 = new Handler() {
        //用于处理“删除文件”的handler
        @Override
        public void handleMessage(Message msg) {

            String result = msg.getData().getString(Constant.STRING_RESPONSE);
            int position = msg.getData().getInt(Constant.STRING_POSITION);

            //解析返回的串中result是否显示成功
            if (JSONUtil.forOperationResult(result).getResult()) {
                //更新adapter中的数据列表，即把列表中第position个item删除。
                doSingleData(position, null);
                Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT);

            } else {
                Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT);
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courseware_list);

        Init();
    }

    //获取文件列表
    //使用时机：第一次打开这个Activity时加载
    private void doFillData() {
        //这个参数是测试时使用，所以现在就写死了
        //获取文件列表需要参数ownerid
        HashMap<String, String> param = new HashMap<>();
        param.put(Constant.STRING_OWNERID, Constant.OWNERID);
        OkHttpManager.CommonPostAsyn(Constant.URL_COURSEWARELIST, param, new OkHttpManager.myCallback() {
            @Override
            public void onCallBack(String result) {
                //Log.e(TAG, result);
                Message msg = new Message();
                msg.getData().putString(Constant.STRING_RESPONSE, result);
                msg.getData().putString(Constant.STRING_FOLDER, currtenFolder);
                rootJson = result;
                mHandler.sendMessage(msg);
            }
        });
    }

    //更新单个文件
    private void doSingleData(int position, GuanCourseware courseware) {
        adapter.setItem(position, courseware);
        adapter.notifyDataSetChanged();
    }


    private void Init() {

        mContext = this;
        parentfolder = new Stack<>();
        filelist = (ListView) findViewById(R.id.list_filelist);
        this.function_btn = (Button) findViewById(R.id.ui_title_function_btn);
        this.title_string = (TextView) findViewById(R.id.ui_title_word_tv);
        this.back_btn = (Button) findViewById(R.id.ui_title_back_btn);

        //获取文件列表
        doFillData();

        //为标题栏的按钮添加监听
        title_string.setText(currtenFolder);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBack();
            }
        });
        function_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu popmenu = new PopupMenu(mContext, function_btn);
                getMenuInflater().inflate(R.menu.menu_title_function, popmenu.getMenu());
                popmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.menu_upload:
                                //popmenu.dismiss();
                                doUpload();
                                break;
                            case R.id.menu_newfolder:
                                //新建文件夹
                                doNewFolder();
                                break;
                            case R.id.menu_filtrate:
                                break;
                            case R.id.menu_refresh:
                                //这里写死了。刷新功能以后可以通过第三方的“下拉刷新”控件来实现。
                                //这里仅供测试。此行代码无价值。
                                doRefresh();
                                break;
                            case R.id.menu_static:
                                //这里写死了。这里仅供测试文件按照“文件夹”来分类的功能。
                                //传递一个静态的JSON串。此行代码无价值。
                                doStaticJSON();
                                break;
                        }

                        return true;
                    }
                });
                popmenu.show();


            }
        });

        //为list中的每一个item添加“长点击”监听器。
        //长按item会弹出一个alertdialog，上面有几个按钮，每个按钮对应一个动作
        filelist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final AlertDialog dlg = new AlertDialog.Builder(mContext).create();
                dlg.setCanceledOnTouchOutside(true);//设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                dlg.show();
                Window window = dlg.getWindow();
                window.setContentView(R.layout.alert_courseware_list_item);

                Button download_btn = (Button) window.findViewById(R.id.alert_courseware_download_btn);
                Button move_btn = (Button) window.findViewById(R.id.alert_courseware_move_btn);
                Button detail_btn = (Button) window.findViewById(R.id.alert_courseware_detail_btn);
                Button delete_btn = (Button) window.findViewById(R.id.alert_courseware_delete_btn);
                Button cancle_btn = (Button) window.findViewById(R.id.alert_courseware_cancle_btn);


                View.OnClickListener onclicklistener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.alert_courseware_cancle_btn:
                                //取消键
                                dlg.cancel();
                                break;
                            case R.id.alert_courseware_detail_btn:
                                //详细信息
                                doDetail(position);
                                dlg.cancel();
                                break;
                            case R.id.alert_courseware_download_btn:
                                //下载
                                doDownload(position);
                                dlg.cancel();
                                break;
                            case R.id.alert_courseware_move_btn:
                                //移动
                                doMove(position);
                                dlg.cancel();
                                break;
                            case R.id.alert_courseware_delete_btn:
                                //删除
                                doDelete(position);
                                dlg.cancel();
                                break;
                            default:
                                Log.e(TAG, "DEFAULT");
                        }
                    }
                };

                download_btn.setOnClickListener(onclicklistener);
                move_btn.setOnClickListener(onclicklistener);
                detail_btn.setOnClickListener(onclicklistener);
                cancle_btn.setOnClickListener(onclicklistener);
                delete_btn.setOnClickListener(onclicklistener);
                return true;
            }
        });

        //为每一个item添加"短点击"的监听，即实现点击进入“文件夹”
        filelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (items.get(position).getFileType().equals(Constant.STRING_FOLDER)) {
                    //如果点击的item是文件夹
                    //以fileId为参数，调用okHttp来获取该文件夹下的目录结构——一个JSON的串。
                    //但此次写成静态的，用来测试
                    Message msg = Message.obtain();
                    msg.getData().putString(Constant.STRING_RESPONSE, Constant.getStaticJSON());//以后这里要改成新获得的Json
                    msg.getData().putString(Constant.STRING_FOLDER, items.get(position).getFileOriginalName());//这里表示所点击的那个文件夹的名字
                    msg.getData().putBoolean(Constant.STRING_CLICKIN, true);//标记是否要点进去
                    mHandler.sendMessage(msg);

                    //把表示“当前所在文件夹”的名字和表示“当前显示的目录结构的JSon”入栈
                    //此处有bug，因为这里并没有检测返回的json串是否显示“操作成功”就入栈了
                    //以后一定要改，bug很严重


                }
            }
        });


    }

    //=============================================================================
    //=============================================================================
    //以下方法对应单一课件菜单的按钮
    private void doDelete(final int position) {
        View.OnClickListener surelistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> param = new HashMap<>();
                param.put(Constant.STRING_FILEID, items.get(position).getFileId());
                Log.e(TAG, items.get(position).getFileId());
                OkHttpManager.CommonPostAsyn(Constant.URL_DELETEFILE, param, new OkHttpManager.myCallback() {
                    @Override
                    public void onCallBack(String result) {
                        Log.e(TAG, result);
                        Message msg = Message.obtain();
                        msg.getData().putString(Constant.STRING_RESPONSE, result);
                        msg.getData().putInt(Constant.STRING_POSITION, position);
                        mHandler2.sendMessage(msg);
                    }
                });
                dlg.cancel();
            }
        };

        View.OnClickListener canclelistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
            }
        };

        dlg = DialogUtils.getYNDialog(mContext, "您确定要删除吗？", surelistener, canclelistener);


    }

    private void doDetail(int position) {
        Intent intent = new Intent(mContext, DetailFileActivity.class);
        intent.putExtra(Constant.STRING_COURSEWARE, items.get(position));

        //修改文件信息时调用了startActivityForResult
        startActivityForResult(intent, Constant.CODE_DODETAIL);
    }

    private void doDownload(int position) {
        Intent intent = new Intent(mContext,
                TransFileActivity.class);
        //告诉另一个Activity，我们要下载
        intent.putExtra(Constant.STRING_WHAT, Constant.STRING_DOWNLOAD);
        //文件的下载地址
        intent.putExtra(Constant.STRING_FILEUrl, items.get(position).getFileUrl());
        //文件的名称
        intent.putExtra(Constant.STRING_FILEORIGINALNAME, items.get(position).getFileOriginalName());//这一这个参数传递的正确定
        //文件大小
        intent.putExtra(Constant.STRING_FILESIZE, items.get(position).getFileSize());
        //文件类型：doc、txt、ppt……
        intent.putExtra(Constant.STRING_FILETYPE, items.get(position).getFileType());
        startActivity(intent);
    }

    private void doMove(int position) {
        Intent intent = new Intent(mContext, MoveActivity.class);
        intent.putExtra(Constant.STRING_ROOTJSON, rootJson);
        intent.putExtra(Constant.STRING_FILEID, items.get(position).getFileId());
        intent.putExtra(Constant.STRING_POSITION, position);

        startActivityForResult(intent, Constant.CODE_DOMOVE);
    }


    //=============================================================
    //=============================================================
    //以下方法为标题功能键的菜单按钮
    private void doUpload() {
        startActivity(new Intent(mContext, BrowseFileActivity.class));
    }

    //
    private void doNewFolder() {
        View.OnClickListener surelistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取新文件夹的名字
                String newfoldername = ((EditText) dlg.getWindow().findViewById(R.id.alert_singleline_content)).getText().toString();
                HashMap<String, String> param = new HashMap<>();
                param.put(Constant.STRING_FILEORIGINALNAME, newfoldername);
                param.put(Constant.STRING_PARENTFOLDER, currtenFolder);
                //调用okhttpmanager，来获取返回的串
                /*
                OkHttpManager.CommonPostAsyn(Constant.URL_NEWFOLDER, param, new OkHttpManager.myCallback() {
                    @Override
                    public void onCallBack(String result) {
                        Log.e(TAG,result);
                        Message msg=Message.obtain();
                        msg.getData().putString("response",result);
                        msg.getData().putString("folder",currtenFolder);
                        mHandler.sendMessage(msg);
                    }
                });
                */
                dlg.cancel();
            }
        };

        View.OnClickListener canclelistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
            }
        };
        dlg = DialogUtils.getSingleLineInput(this, "", surelistener, canclelistener);

    }

    //这仅仅是为了刷新而刷新。为测试用
    private void doRefresh() {
        //这个参数是测试时使用，所以现在就写死了
        //获取文件列表需要参数ownerid
        HashMap<String, String> param = new HashMap<>();
        param.put(Constant.STRING_OWNERID, Constant.OWNERID);
        OkHttpManager.CommonPostAsyn(Constant.URL_COURSEWARELIST, param, new OkHttpManager.myCallback() {
            @Override
            public void onCallBack(String result) {
                //Log.e(TAG, result);
                Message msg = new Message();
                msg.getData().putString(Constant.STRING_RESPONSE, result);
                msg.getData().putString(Constant.STRING_FOLDER, currtenFolder);
                mHandler.sendMessage(msg);
            }
        });
    }

    //这里仅仅是为了获取静态JSON而写，为测试用
    private void doStaticJSON() {
        String currtenJson = "";
        String currtenFolder = "/";
        Message msg = Message.obtain();
        msg.getData().putString(Constant.STRING_FOLDER, "/");
        msg.getData().putString(Constant.STRING_RESPONSE, Constant.getStaticJSON());
        rootJson = msg.getData().getString(Constant.STRING_RESPONSE);
        mHandler.sendMessage(msg);
    }

    //=========================================================================
    //=========================================================================

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e(TAG, "onActivityResult:\t requestCode:" + requestCode + "\tresultCode:" + resultCode);
        if (requestCode == Constant.CODE_DODETAIL && resultCode == Activity.RESULT_OK) {

            //调用“doDetail”，并且返回ok
            int position = data.getExtras().getInt(Constant.STRING_POSITION);
            GuanCourseware courseware = (GuanCourseware) data.getExtras().get(Constant.STRING_COURSEWARE);
            Log.e(TAG, courseware.getFileOriginalName());
            doSingleData(position, courseware);
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT);

        } else if (requestCode == Constant.CODE_DOMOVE && resultCode == Activity.RESULT_OK) {

            //调用“doMove”，并且返回ok
            int position = data.getExtras().getInt(Constant.STRING_POSITION);
            Log.e(TAG, position + "");
            doSingleData(position, null);
            Toast.makeText(this, "移动成功", Toast.LENGTH_SHORT);
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
