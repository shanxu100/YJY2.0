package com.luluteam.yjy.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import yjy.luluteam.com.yjy.R;

/**
 * Created by Guan on 2016/7/30.
 */
public class DialogUtils {

    public static AlertDialog getYNDialog(Context mContex, String contentstring, View.OnClickListener surelistenser,
                                          View.OnClickListener canclelistener)
    {
        AlertDialog dlg=new AlertDialog.Builder(mContex).create();
        dlg.show();
        Window window=dlg.getWindow();
        window.setContentView(R.layout.alert_yesorno);
        TextView content=(TextView)window.findViewById(R.id.alert_yn_content);
        Button sure_btn=(Button)window.findViewById(R.id.alert_yn_sure_btn);
        Button cancle_btn=(Button)window.findViewById(R.id.alert_yn_cancel_btn);
        content.setText(contentstring);
        sure_btn.setOnClickListener(surelistenser);
        cancle_btn.setOnClickListener(canclelistener);
        return dlg;
    }

    public static AlertDialog getSingleLineInput(Context mContex, String contentstring, View.OnClickListener surelistenser,
                                                 View.OnClickListener canclelistener)
    {
        AlertDialog dlg=new AlertDialog.Builder(mContex).create();
        dlg.show();
        Window window=dlg.getWindow();
        //加上这一句就能显示小键盘了。。。重要重要
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.alert_singleline_input);
        EditText content=(EditText)window.findViewById(R.id.alert_singleline_content);
        Button sure_btn=(Button)window.findViewById(R.id.alert_singleline_sure_btn);
        Button cancle_btn=(Button)window.findViewById(R.id.alert_singleline_cancel_btn);
        content.setText(contentstring);
        sure_btn.setOnClickListener(surelistenser);
        cancle_btn.setOnClickListener(canclelistener);
        return dlg;
    }
}
