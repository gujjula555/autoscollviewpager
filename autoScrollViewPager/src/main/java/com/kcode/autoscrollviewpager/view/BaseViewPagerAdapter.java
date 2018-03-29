package com.kcode.autoscrollviewpager.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kcode.autoscrollviewpager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caik on 2016/10/11.
 */

public abstract class BaseViewPagerAdapter<T> extends PagerAdapter implements ViewPager.OnPageChangeListener{

    private static final String TAG = "BaseViewPagerAdapter";

    private List<T> data = new ArrayList<>();

    private Context mContext;
    private AutoViewPager mView;

    private OnAutoViewPagerItemClickListener listener;

    public BaseViewPagerAdapter(List<T> t) {
        this.data = t;
    }

    public BaseViewPagerAdapter(Context context) {
        this.mContext = context;
    }


    public BaseViewPagerAdapter(Context context, OnAutoViewPagerItemClickListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    public BaseViewPagerAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.data = data;
    }

    public BaseViewPagerAdapter(Context context, List<T> data,OnAutoViewPagerItemClickListener listener) {
        this.mContext = context;
        this.data = data;
        this.listener = listener;
    }

    public void init(AutoViewPager viewPager,BaseViewPagerAdapter adapter){
        mView = viewPager;
        mView.setAdapter(this);
        mView.addOnPageChangeListener(this);

        if (data == null || data.size() == 0){
            return;
        }
        //Set the initial middle, so that you can slide to the left
        int position = Integer.MAX_VALUE/2 - (Integer.MAX_VALUE/2) % getRealCount();
        mView.setCurrentItem(position);

        if (!mView.isStart()) {
            mView.start();
            mView.updatePointView(getRealCount());
        }

    }

    public void setListener(OnAutoViewPagerItemClickListener listener) {
        this.listener = listener;
    }

    public void add(T t){

        if (mView.getAdapter() == null) {
            throw new RuntimeException("Must set Adapter first");
        }

        if (data == null) {
            data = new ArrayList<>();
        }
        int currentItem = mView.getCurrentItem() % getRealCount();
        data.add(t);
        //Stop auto-scroll first
        mView.onStop();
        Log.d(TAG, "Current display" + currentItem + "/" + mView.getCurrentItem());
//        notifyDataSetChanged();
        mView.updatePointView(getRealCount(),currentItem);
        //Restart automatic scrolling
        mView.onResume();

    }

    public void add(List<T> list){
        if (data == null) {
            data = new ArrayList<>();
        }

        data.addAll(list);

        notifyDataSetChanged();

        mView.start();
        mView.updatePointView(getRealCount());

    }

    public void init(List<T> list){
        if (data == null) {
            data = new ArrayList<>();
        }

        data.clear();
        data.addAll(list);

        notifyDataSetChanged();

        if (!mView.isStart()) {
            mView.start();
            mView.updatePointView(getRealCount());
        }


    }

    @Override
    public int getCount() {
        return (data == null || data.size() == 0 ) ? 0 : Integer.MAX_VALUE;
    }

    public int getRealCount(){
        return data == null ? 0 : data.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView view = (ImageView) object;
        container.removeView(view);
        view = null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView view = (ImageView) LayoutInflater.from(mContext)
                .inflate(R.layout.imageview,container,false);
        loadImage(view,position, data.get(position % getRealCount()));
        container.addView(view);

        //Set the title (do not know why the title is not on the picture, so do the following processing, there is a big God to see help...)
        if (mView.getSubTitle() != null){
            if (position == 0){
                setSubTitle(mView.getSubTitle(),position,data.get((getRealCount() - 1)));
            }else {
                setSubTitle(mView.getSubTitle(),position,data.get((position - 1) % getRealCount()));
            }

        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mView.onStop();
                if (listener != null) {
                    listener.onItemClick(position % getRealCount(),data.get(position % getRealCount()));
                }
                mView.onResume();
            }
        });

        return view;
    }

    public abstract void loadImage(ImageView view,int position,T t);
    public abstract void setSubTitle(TextView textView,int position,T t);

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mView.onPageSelected(position % getRealCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface OnAutoViewPagerItemClickListener<T> {
        void onItemClick(int position,T t);
    }
}
