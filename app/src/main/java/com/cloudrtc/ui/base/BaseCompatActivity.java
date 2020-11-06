package com.cloudrtc.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.cloudrtc.ui.util.StatusBarUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


/**
 * ========================================
 * <p>
 * 版 权：CZF
 * <p>
 * 作 者：CZF
 * <p>
 * 微 信：18326932812
 * <p>
 * Q  Q：279154451
 * <p>
 * 创建日期：2019/3/11  下午3:04
 * <p>
 * 描 述：
 * <p>
 * ========================================
 */
public abstract class BaseCompatActivity extends AppCompatActivity {
    public String TAG;
    protected Context mContext;

    static {
        //5.0以下兼容vector
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();
        //1、设置状态栏主题颜色
        StatusBarUtils.StatusBarLightMode(this);

        //2、引入布局，设置布局Id
        setContentView(getLayoutId());

        //4、设置状态栏透明色
        StatusBarUtils.setTransparent(this);

        //5、设置屏幕显示的方向，此处为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //6、初始化数据
        initData();

        //8、子类初始化数据
        initView(savedInstanceState);
    }

    /**
     * 获取当前layouty的布局ID,用于设置当前布局
     * 交由子类实现
     *
     * @return layout Id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化view
     * <p>
     * 子类实现 控件绑定、视图初始化等内容
     *
     * @param savedInstanceState savedInstanceState
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 初始化数据
     * <p>
     * 子类可以复写此方法初始化子类数据
     */
    protected void initData() {
        mContext = this;
    }


    protected void openStartActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
