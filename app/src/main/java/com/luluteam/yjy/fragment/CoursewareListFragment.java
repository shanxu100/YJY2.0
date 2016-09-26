package com.luluteam.yjy.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.luluteam.yjy.activity.BrowseFileActivity;
import com.luluteam.yjy.activity.DetailFileActivity;
import com.luluteam.yjy.activity.TransFileActivity;
import com.luluteam.yjy.base.Constant;
import com.luluteam.yjy.manager.CoursewareListAdapter;
import com.luluteam.yjy.manager.OkHttpManager;
import com.luluteam.yjy.model.Courseware;
import com.luluteam.yjy.utils.DialogUtils;
import com.luluteam.yjy.utils.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import yjy.luluteam.com.yjy.R;

/**
 * Created by Guan on 2016/7/29.
 *
 * 这个fragment已经废弃不用。上面的内容已经交由CoursewareListActivity来完成
 */

public class CoursewareListFragment extends Fragment {

    /*
    private ListView filelist;

    private List<Courseware> items;
    private CoursewareListAdapter adapter;
    private Stack<HashMap<String,String>> parentfolder ;//建立一个栈，用于存储父文件夹中表示目录结构的JSON
    private String currtenJson="";
    private String currtenFolder="/";


    private Button function_btn;// 添加课件按钮
    private TextView title_string;
    private Button back_btn;



    private String TAG = "CoursewareListFragment";
    private AlertDialog dlg;

    //获取文件列表的Handler
    private Handler mHandler = new Handler() {
        //用于获取文件列表的handler
        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString("response");
            String folder=msg.getData().getString("folder");
            Log.e(TAG + "mHandler", result);
            try {
                if (null == result) {
                    return;
                }
                JSONObject jo = (JSONObject) new JSONTokener(result).nextValue();
                if (jo.getString("result").equals("true")) {
                    items = JSONUtil.forCoursewarelist(jo);
                    if (null == adapter) {
                        //第一次初始化adapter
                        adapter = new CoursewareListAdapter(getContext(), items);
                        filelist.setAdapter(adapter);
                    } else {
                        //以后更新整个列表时，不用再new一个adapter，而是更新数据集就好
                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();
                    }
                    //此时已经成功显示了列表。所以接下来就是把这个能表示该目录结构的Json入栈
                    currtenJson=result;
                    currtenFolder=folder;
                    title_string.setText(currtenFolder);
                    //parentfolder.push(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    //更新文件列表中某一个item的Handler
    Handler mHandler2 = new Handler() {
        //用于处理“删除文件”的handler
        @Override
        public void handleMessage(Message msg) {
            try {
                String result = msg.getData().getString("response");
                int position = msg.getData().getInt("position");

                JSONObject jo = (JSONObject) (new JSONTokener(result)).nextValue();
                if (jo.getString("result").equals("true")) {
                    //更新adapter中的数据列表，即把列表中第position个item删除。
                    doSingleData(position, null);
                    Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);
        InitView(view);
        return view;

    }


    //获取文件列表
    private void doFillData() {
        //这个参数是测试时使用，所以现在就写死了
        HashMap<String, String> param = new HashMap<>();
        param.put("levelId", "2");
        OkHttpManager.CommonPostAsyn(Constant.URL_COURSEWARELIST, param, new OkHttpManager.myCallback() {
            @Override
            public void onCallBack(String result) {
                //Log.e(TAG, result);
                Message msg = new Message();
                msg.getData().putString("response", result);
                msg.getData().putString("folder",currtenFolder);
                mHandler.sendMessage(msg);
            }
        });
    }

    //更新单个文件
    private void doSingleData(int position, Courseware courseware) {
        adapter.setItem(position, courseware);
        adapter.notifyDataSetChanged();
    }


    private void InitView(View v) {
        parentfolder = new Stack<>();

        filelist = (ListView) v.findViewById(R.id.list_filelist);
        this.function_btn = (Button) v.findViewById(R.id.ui_title_function_btn);
        this.title_string = (TextView) v.findViewById(R.id.ui_title_word_tv);
        this.back_btn = (Button) v.findViewById(R.id.ui_title_back_btn);

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

                final PopupMenu popmenu = new PopupMenu(getActivity(), function_btn);
                getActivity().getMenuInflater().inflate(R.menu.menu_title_function, popmenu.getMenu());
                popmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.menu_upload:
                                popmenu.dismiss();
                                startActivity(new Intent(getActivity(), BrowseFileActivity.class));
                                break;
                            case R.id.menu_search:
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

        //为list中的每一个item添加监听器。长按item会弹出一个alertdialog，上面有几个按钮，每个按钮对应一个动作
        filelist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
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

        //为每一个item添加点击的监听
        filelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (items.get(position).getType().equals("folder")) {
                    //如果点击的item是文件夹
                    //以docId为参数，调用okHttp来获取该文件夹下的目录结构。
                    //但此次写成静态的，用来测试
                    Message msg = Message.obtain();
                    msg.getData().putString("response", Constant.staticJSON2);
                    msg.getData().putString("folder",items.get(position).getOriginalName());
                    mHandler.sendMessage(msg);

                    HashMap<String,String> tmp_map=new HashMap<String, String>();
                    tmp_map.put("folder",currtenFolder);
                    tmp_map.put("json",currtenJson);
                    parentfolder.push(tmp_map);//发送新的字符串来显示，同时把当前的字符串入栈
                    //title_string.setText(items.get(position).getOriginalName());
                }
            }
        });


    }

    private void doDelete(final int position) {
        View.OnClickListener surelistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> param = new HashMap<>();
                param.put("docId", items.get(position).getDocId());
                Log.e(TAG, items.get(position).getDocId());
                OkHttpManager.CommonPostAsyn(Constant.URL_DELETEFILE, param, new OkHttpManager.myCallback() {
                    @Override
                    public void onCallBack(String result) {
                        Log.e(TAG, result);
                        Message msg = Message.obtain();
                        msg.getData().putString("response", result);
                        msg.getData().putInt("position", position);
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

        dlg = DialogUtils.getYNDialog(getActivity(), "您确定要删除吗？", surelistener, canclelistener);


    }

    private void doDetail(int position) {
        Intent intent = new Intent(getActivity(), DetailFileActivity.class);
        intent.putExtra("courseware", (Serializable) items.get(position));
        //getActivity().startActivity(intent);
        startActivityForResult(intent, Activity.RESULT_FIRST_USER);
    }

    private void doDownload(int position) {
        Intent intent = new Intent(getActivity(),
                TransFileActivity.class);
        intent.putExtra("what", Constant.STRING_DOWNLOAD);
        intent.putExtra("downloadurl", items.get(position).getDownPath());
        intent.putExtra("filename", items.get(position).getOriginalName());
        intent.putExtra("size", items.get(position).getDocSize());
        intent.putExtra("fType", items.get(position).getType());
        getActivity().startActivity(intent);
    }

    private void doBack() {

        if (!parentfolder.isEmpty()) {
            //查看此时的栈顶元素
            HashMap<String,String> tmp_map= parentfolder.pop();
            currtenJson=tmp_map.get("json");
            currtenFolder=tmp_map.get("folder");
            title_string.setText(currtenFolder);
            Message msg = Message.obtain();
            msg.getData().putString("response", currtenJson);
            msg.getData().putString("folder", currtenFolder);
            mHandler.sendMessage(msg);
            Log.e(TAG, "栈非空");
        }


    }

    //这仅仅是为了刷新而刷新。为测试用
    public void doRefresh() {
        doFillData();
    }

    //这里仅仅是为了获取静态JSON而写，为测试用
    public void doStaticJSON() {
        Message msg = Message.obtain();
        msg.getData().putString("folder",currtenFolder);
        msg.getData().putString("response", Constant.staticJSON);
        mHandler.sendMessage(msg);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e(TAG, "onActivityResult:" + resultCode);
        if (resultCode == Constant.CODE_DODETAIL_SUCCESS) {
            int position = data.getExtras().getInt("position");
            Courseware courseware = (Courseware) data.getExtras().get("courseware");
            Log.e(TAG, courseware.getOriginalName());
            doSingleData(position, courseware);
        }

    }
*/

}
