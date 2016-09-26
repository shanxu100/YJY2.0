package com.luluteam.yjy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.luluteam.yjy.fragment.CoursewareListFragment;

import yjy.luluteam.com.yjy.R;

public class MainActivity extends FragmentActivity {


    private Fragment filelistfragment;

    public static Fragment currentfragment;

    private String TAG = "MainActivity";

    private Button start_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
    }

    private void Init() {


        start_btn = (Button) findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CoursewareListActivity.class));
            }
        });
        //显示fragment
//        this.filelistfragment = new CoursewareListFragment();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.content_layout, filelistfragment).commit();
//        currentfragment = filelistfragment;


    }


    //fragment之间进行切换
//    public void doChangeFragment(Fragment newfragment) {
//        Fragment tmp_fragment = currentfragment;
//        if (newfragment == tmp_fragment) {
//            return;
//        }
//        //每次fragment之间切换
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
//                .beginTransaction();
//
//        if (!newfragment.isAdded()) {
//            // 如果当前fragment未被添加，则添加到Fragment管理器中
//            fragmentTransaction.hide(tmp_fragment)
//                    .add(R.id.content_layout, newfragment).commit();
//        } else {
//            //如果已经添加，那就直接显示
//            fragmentTransaction.hide(tmp_fragment).show(newfragment).commit();
//        }
//    }


}
