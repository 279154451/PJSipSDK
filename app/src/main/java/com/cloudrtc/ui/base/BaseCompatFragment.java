package com.cloudrtc.ui.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * Created by Horrarndoo on 2017/9/26.
 * <p>
 */

public abstract class BaseCompatFragment extends Fragment {

    protected String TAG;
    protected Context mContext;
    protected Activity mActivity;


    public View mRootView = null;

    public boolean mIsSHowHistoryView = false;

    @Override
    public void onAttach(Context context) {
        mActivity = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        if(mIsSHowHistoryView){
            mRootView = setShowHistoryStatus(mRootView);
        }

        if(mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }
        if(mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if(parent != null) {
                parent.removeView(mRootView);
            }
        }

        return mRootView;
    }

    /******************懒加载*****************/

    protected boolean isViewInitiated;
    protected boolean isVisibleToUser;
    protected boolean isDataInitiated;
    protected boolean isFragmentHidden;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        prepareFetchData();
        if(isVisibleToUser){
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isFragmentHidden= hidden;
        if(!hidden){
        }
    }

    public boolean prepareFetchData() {
        return prepareFetchData(false);
    }

    public boolean prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            lazyLoad();
            isDataInitiated = true;
            return true;
        }
        return false;
    }

    /**
     * 加载要显示的数据
     */
    protected abstract void lazyLoad();

    /*************************************/

    public View setShowHistoryStatus(View view){
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeAllViews();
            }
            return view;
        }
        return null;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TAG = getClass().getSimpleName();
        getBundle(getArguments());
        initData();

        isViewInitiated = true;
        prepareFetchData();

        initUI(view, savedInstanceState);
    }

    public void openStartActivity(Class clazz) {
        Intent intent = new Intent(this.getActivity(), clazz);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @LayoutRes
    public abstract int getLayoutId();

    /**
     * 得到Activity传进来的值
     */
    public void getBundle(Bundle bundle) {
    }

    /**
     * 初始化UI
     */
    public abstract void initUI(View view, @Nullable Bundle savedInstanceState);

    /**
     * 在监听器之前把数据准备好
     */
    public void initData() {
        mContext = getContext();
    }
}
