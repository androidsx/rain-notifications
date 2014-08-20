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
 * <p>
 * Always use {@link #startWelcomeActivity} to launch this activity.
 */
public class WelcomeActivity extends FragmentActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private static final String NUM_PAGES_EXTRA = "num_pages";
    /** Note: increasing this limit requires changes in the XML and some code in this class. */
    private static final int MAX_NUM_PAGES = 4;

    private int numPages;
    
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    
    /**
     * Starts the welcome activity.
     * 
     * @param context typically the activity where we launch from
     * @param comesFrom for tracking purposes only
     * @param numPages number of pages to display. Must be lower than {@link #MAX_NUM_PAGES}
     */
    public static void startWelcomeActivity(Context context, String comesFrom, int numPages) {
        Log.i(TAG, "Start the welcome activity from \"" + comesFrom + "\"");
        //ApplicationHelper.Flurry.reportHelp(comesFrom);
        Intent intent = new Intent(context, WelcomeActivity.class);
        if (numPages < 1 || numPages > MAX_NUM_PAGES) {
            throw new IllegalArgumentException("Wrong number of pages for the welcome tutorial: " + numPages);
        }
       	intent.putExtra(NUM_PAGES_EXTRA, numPages);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_slide);
        
        numPages = getIntent().getIntExtra(NUM_PAGES_EXTRA, MAX_NUM_PAGES);
        configureRadioButtonVisibility();

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
                if (position == numPages - 1) {
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

    /**
     * Configures the visibility of the radio buttons. There are {@link #MAX_NUM_PAGES} of them hardcoded in the XML.
     * <p>
     * Careful: assumes {@link #MAX_NUM_PAGES} is exactly 4.
     */
    private void configureRadioButtonVisibility() {
        if (numPages < MAX_NUM_PAGES) {
            findViewById(R.id.radio3).setVisibility(View.GONE);
        }
        if (numPages < 3) {
            findViewById(R.id.radio2).setVisibility(View.GONE);
        }
        if (numPages < 2) {
            findViewById(R.id.radio1).setVisibility(View.GONE);
        }
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
            case 2 :
                button = (RadioButton) findViewById(R.id.radio2);
                break;
            case 3 :
                button = (RadioButton) findViewById(R.id.radio3);
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
            return numPages;
        }
    }
}
