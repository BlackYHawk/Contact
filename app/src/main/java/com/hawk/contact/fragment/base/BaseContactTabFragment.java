package com.hawk.contact.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hawk.contact.R;
import com.hawk.contact.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyong on 16/3/15.
 */
public abstract class BaseContactTabFragment extends BaseContactFragment {

    private static final String SAVE_SELECTED_TAB = "selected_tab";
    private ViewPager mViewPager;
    private TabPagerAdapter mPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;

    private int mCurrentItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);
        mPagerAdapter = new TabPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.spacing_minor));

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.viewpager_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setTabListener(new SlidingTabLayout.TabListener() {
            @Override
            public void onTabSelected(int pos) {

            }

            @Override
            public void onTabReSelected(int pos) {
                final Fragment mFragment = mPagerAdapter.getItem(pos);
                if(mFragment instanceof ListFragment) {
                    ((ListFragment) mFragment).smoothScrollTo(0);
                }
            }
        });

        if(savedInstanceState != null) {
            mCurrentItem = savedInstanceState.getInt(SAVE_SELECTED_TAB);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSlidingTabLayout.getBackground().setAlpha(255);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurrentItem = mViewPager.getCurrentItem();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_SELECTED_TAB, mCurrentItem);
        super.onSaveInstanceState(outState);
    }

    protected void setFragments(List<Fragment> fragments) {
        mPagerAdapter.setFragments(fragments);
        mSlidingTabLayout.notifyDataSetChanged();
        mViewPager.setCurrentItem(mCurrentItem);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public TabPagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    public SlidingTabLayout getSlidingTabLayout() {
        return mSlidingTabLayout;
    }

    protected abstract String getTabTitle(int position);

    protected class TabPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> mFragments;

        private TabPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<>();
        }

        void setFragments(List<Fragment> fragments) {
            mFragments.clear();
            mFragments.addAll(fragments);
            notifyDataSetChanged();
        }

        @Override
        public final Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public final int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getTabTitle(position);
        }
    }
}
