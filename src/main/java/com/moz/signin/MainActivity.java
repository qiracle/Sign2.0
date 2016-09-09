package com.moz.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import qq.qiracle.Fragment.MenuFragment;
import qq.qiracle.Fragment.StudentSignFragment;
import qq.qiracle.Fragment.TeacherSignFragment;
//import com.moz.signin.fragment.MenuFragment;

public class MainActivity extends SlidingFragmentActivity implements OnClickListener{
    SlidingMenu sm;
    private Fragment mContent;
    private ImageView topButton;
    private TextView topView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setBehindContentView(R.layout.sliding_menu);
        setContentView(R.layout.activity_main);
        initSlidingMenu(savedInstanceState);


        topView = (TextView) findViewById(R.id.topTv);
        topButton = (ImageView) findViewById(R.id.topButton);
        topButton.setOnClickListener(this);


    }

    /**
     * 初始化侧边栏
     */
    private void initSlidingMenu(Bundle savedInstanceState) {
        // 如果保存的状态不为空则得到之前保存的Fragment，否则实例化MyFragment
//        if (savedInstanceState != null) {
//            mContent = getSupportFragmentManager().getFragment(
//                    savedInstanceState, "mContent");
//        }

//        if (mContent == null) {
//            mContent = new StudentSignFragment();
//        }

        MenuFragment menuFragment = new MenuFragment();
        // 设置左侧滑动菜单
        setBehindContentView(R.layout.menu_frame_left);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, menuFragment).commit();


        Intent intent = getIntent();
       int type = intent.getIntExtra("Type",0);

        if (type==1){
            Bundle bundle = new Bundle();
            bundle.putInt("Flag", 1);
            menuFragment.setArguments(bundle);
            StudentSignFragment sf  = new StudentSignFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, sf).commit();
        }
        if(type ==2){
            Bundle bundle = new Bundle();
            bundle.putInt("Flag", 2);
            menuFragment.setArguments(bundle);
            TeacherSignFragment tf  = new TeacherSignFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, tf).commit();
        }


        // 实例化滑动菜单对象
        sm = getSlidingMenu();
        // 设置可以左右滑动的菜单
        sm.setMode(SlidingMenu.LEFT);
        // 设置滑动阴影的宽度
      //  sm.setShadowWidthRes(R.dimen.shadow_width);
        // 设置滑动菜单阴影的图像资源
        sm.setShadowDrawable(null);
        // 设置滑动菜单视图的宽度
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        sm.setFadeDegree(0.35f);
        // 设置触摸屏幕的模式,这里设置为全屏
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置下方视图的在滚动时的缩放比例
        sm.setBehindScrollScale(0.0f);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
//    }

    public void switchContent(Fragment fragment,String title) {
        mContent = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
        topView.setText(title);
        sm.toggle();// 自动切换

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topButton:
                toggle();
                break;
            default:
                break;
        }
    }
}