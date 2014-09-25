package com.androidsx.rainnotifications.dailyclothes;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends FragmentActivity {

    private static final int NUM_PAGES = 3;

    private Random random = new Random();
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateUI();
    }

    private void updateUI() {
        fillForecastView((ViewGroup)findViewById(R.id.hourly_forecast), 8, 24);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private void fillForecastView(ViewGroup forecastView, int startHour, int endHour) {
        int maxTemp = 0;
        for(int i=startHour; i < endHour; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.hourly_forecast_item, null);
            ImageView icon = (ImageView) view.findViewById(R.id.forecast_icon);
            TextView temp = (TextView) view.findViewById(R.id.forecast_temp);
            TextView hour = (TextView) view.findViewById(R.id.forecast_hour);
            icon.setImageDrawable(getResources().getDrawable(getRandomWeatherIcon()));
            int auxTemp = getRandomBetweenNumbers(60, 67);
            temp.setText(auxTemp + "º");
            hour.setText(i+"am");
            forecastView.addView(view);
            if(auxTemp > maxTemp) {
                maxTemp = auxTemp;
            }
        }
        TextView forecastMessage = (TextView) findViewById(R.id.forecast_message);
        TextView alertMessage = (TextView) findViewById(R.id.alert_message);
        forecastMessage.setText(Html.fromHtml(String.format(getString(R.string.forecast_message), maxTemp)));
        alertMessage.setText(Html.fromHtml(String.format(getString(R.string.alert_message))));
    }

    private int getRandomBetweenNumbers(int minValue, int maxValue) {
        return random.nextInt((maxValue + 1) - minValue) + minValue;
    }

    private int getRandomWeatherIcon() {
        final TypedArray mascotTypedArray = getResources().obtainTypedArray(R.array.weatherIcons);
        final int mascotIndex = random.nextInt(mascotTypedArray.length());
        return mascotTypedArray.getResourceId(mascotIndex, -1);
    }

    /**
     * A simple pager adapter that represents 3 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlidePageFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public static class ScreenSlidePageFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.clothes_image, container, false);

            ImageView image = (ImageView) rootView.findViewById(R.id.photo);
            image.setImageDrawable(getResources().getDrawable(R.drawable.model));
            return rootView;
        }
    }
}
