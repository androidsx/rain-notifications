package com.androidsx.rainnotifications.ui.welcome;

import com.androidsx.rainnotifications.R;
//import com.androidsx.rainnotifications.utils.ApplicationHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Activity that shows the welcome pages.
 */
public class WelcomeActivity extends FragmentActivity {
    /** Number of pages for the tutorial. Not configurable. */
    private static final int NUM_PAGES = 2;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_slide);
        
        final TextView skipButton = (TextView) findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        final TextView goToAppButton = (TextView) findViewById(R.id.go_to_app_button);
        goToAppButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setRadioPosition(position);
                if (position == NUM_PAGES - 1) {
                    skipButton.setVisibility(View.INVISIBLE);
                    goToAppButton.setVisibility(View.VISIBLE);
                } else {
                    skipButton.setVisibility(View.VISIBLE);
                    goToAppButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private void setRadioPosition(int position) {
        RadioButton button = (RadioButton) findViewById(R.id.radio0);

        switch (position) {
            case 0 :
                button = (RadioButton) findViewById(R.id.radio0);
                break;
            case 1 :
                button = (RadioButton) findViewById(R.id.radio1);
                break;
        }

        button.setChecked(true);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return WelcomeFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
