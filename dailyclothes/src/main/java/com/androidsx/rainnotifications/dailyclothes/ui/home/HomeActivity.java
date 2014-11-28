package com.androidsx.rainnotifications.dailyclothes.ui.home;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidsx.rainnotifications.alert.DayTemplateGenerator;
import com.androidsx.rainnotifications.backgroundservice.util.UserLocationFetcher;
import com.androidsx.rainnotifications.dailyclothes.R;
import com.androidsx.rainnotifications.dailyclothes.model.Clothes;
import com.androidsx.rainnotifications.dailyclothes.ui.widget.customfont.CustomTextView;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientDailyResponseListener;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientHourlyResponseListener;
import com.androidsx.rainnotifications.model.DailyForecast;
import com.androidsx.rainnotifications.model.DailyForecastTable;
import com.androidsx.rainnotifications.model.Day;
import com.androidsx.rainnotifications.model.DayTemplate;
import com.androidsx.rainnotifications.model.DayTemplateLoaderFactory;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.WeatherWrapper;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.weatherclientfactory.WeatherClientFactory;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class HomeActivity extends FragmentActivity {

    private static final Duration FORECAST_DATA_EXPIRATION_DURATION = Duration.standardMinutes(30);
    private static final long FORECAST_DATA_DONE_DELAY = 1000;
    private static final long PANEL_DEMO_EXPANDED_DURATION = 1500;
    private static final long HEART_BUTTON_ANIMATION_DURATION = 200;
    private static final int MAX_FORECAST_ITEMS = 24;
    private static final int COLOR_TRANSITION_DURATION = 100;
    private static final int WEEK_FORECAST_DAYS = 5;

    private enum ForecastDataState {LOADING, ERROR_LOCATION, ERROR_FORECAST, LOADED, DONE};

    private enum PanelScrollValue {
        COLLAPSED(0f),
        EXPANDED(1f);

        private float scrollValue;

        private PanelScrollValue(float scrollValue) {
            this.scrollValue = scrollValue;
        }

        public float getScrollValue() {
            return scrollValue;
        }
    }

    private ForecastDataState dataState;
    private DateTime forecastTableTime;
    private ForecastTable forecastTable;
    private Day day;
    private String forecastSummaryMessage;
    private String city;
    private DailyForecastTable dailyForecastTable;

    private boolean activityDestroyed = false; // Panic mode. It's used for not modify any view.
    private View frameLoading;
    private View frameError;
    private CustomTextView errorMessage;
    private Button errorButton;
    private SlidingUpPanelLayout slidingPanel;
    private View slidingPanelToday;
    private View slidingPanelWeek;
    private CustomTextView slidingPanelSummary;
    private CustomTextView nowTemperature;
    private CustomTextView nowTemperatureButton;
    private CustomTextView minTemperature;
    private CustomTextView maxTemperature;
    private View heartButton;
    private ViewPager clothesPager;
    private LinearLayout hourlyLinear;
    private HorizontalScrollView hourlyScroll;

    private DailyForecastAdapter dailyAdapter;
    private CustomTextView cityLabel;

    private Integer todayCollapsedBackgroundColor;
    private Integer todayCollapsedPrimaryColor;
    private Integer todayCollapsedSecondaryColor;
    private Integer todayExpandedBackgroundColor;
    private Integer todayExpandedPrimaryColor;
    private Integer todayExpandedSecondaryColor;
    private View todayDivider;
    private CustomTextView temperatureSymbol;
    private ImageView todayMinTemperatureIcon;
    private ImageView todayMaxTemperatureIcon;
    private ArrayList<TextView> hourlyTextViews;
    private ArrayList<ImageView> hourlyIcons;
    private PanelListener panelListener;

    private float positionHeartPanelHidden = 0f;
    private float positionHeartPanelCollapsed;
    private float positionHeartPanelAnchored;
    private float positionHeartPanelExpanded;

    private WeatherWrapper.TemperatureScale localeScale;
    private DecimalFormat temperatureFormat = new DecimalFormat("#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localeScale  = WeatherWrapper.TemperatureScale.getLocaleScale(this);
        setupUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkDataState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityDestroyed = true;
    }

    private void checkDataState() {
        if(dataState == null || dataState.equals(ForecastDataState.ERROR_LOCATION) || dataState.equals(ForecastDataState.ERROR_FORECAST)) {
            setForecastDataState(ForecastDataState.LOADING);
        }
        else if(dataState.equals(ForecastDataState.DONE) && new Duration(forecastTableTime, new DateTime()).isLongerThan(FORECAST_DATA_EXPIRATION_DURATION)) {
            setForecastDataState(ForecastDataState.LOADING);
        }
    }

    private void setForecastDataState(ForecastDataState newState) {
        dataState = newState;
        switch (newState) {
            case LOADING:
                forecastTable = null;
                forecastTableTime = null;
                day = null;
                forecastSummaryMessage = null;
                getForecastData();
                break;
            case ERROR_LOCATION:
                break;
            case ERROR_FORECAST:
                break;
            case LOADED:
                break;
            case DONE:
                break;
        }
        updateUI();
    }

    private void getForecastData() {
        // FIXME: we do exactly the same in the weather service. grr..
        UserLocationFetcher.getUserLocation(this, new UserLocationFetcher.UserLocationResultListener() {

            private boolean hourlyDone = false;
            private boolean dailyDone = false;

            @Override
            public void onLocationSuccess(final Location location) {

                // FIXME: This happens on UI Thread and skips frames.
                HomeActivity.this.city = UserLocationFetcher.getLocationAddress(HomeActivity.this, location.getLatitude(), location.getLongitude());

                WeatherClientFactory.requestHourlyForecastForLocation(HomeActivity.this, location.getLatitude(), location.getLongitude(), new WeatherClientHourlyResponseListener() {
                    @Override
                    public void onForecastSuccess(ForecastTable forecastTable) {

                        // FIXME: This happens on UI Thread and skips frames.
                        HomeActivity.this.forecastTable = forecastTable;
                        HomeActivity.this.forecastTableTime = new DateTime();
                        HomeActivity.this.day = new Day(forecastTable);

                        DayTemplate template = new DayTemplateGenerator(DayTemplateLoaderFactory.getDayTemplateLoader(HomeActivity.this)).getDayTemplate(day);
                        if (template == null) {
                            HomeActivity.this.forecastSummaryMessage = getString(R.string.default_day_message);
                        } else {
                            HomeActivity.this.forecastSummaryMessage = template.resolveMessage(HomeActivity.this, HomeActivity.this.day);
                        }

                        hourlyDone = true;
                        checkBothRequestDone();
                    }

                    @Override
                    public void onForecastFailure(WeatherClientException exception) {
                        Timber.e(exception, "Failed to get hourly forecast");
                        setForecastDataState(ForecastDataState.ERROR_FORECAST);
                    }
                });

                WeatherClientFactory.requestDailyForecastForLocation(HomeActivity.this, location.getLatitude(), location.getLongitude(), new WeatherClientDailyResponseListener() {
                    @Override
                    public void onForecastSuccess(DailyForecastTable dailyForecastTable) {
                        HomeActivity.this.dailyForecastTable = dailyForecastTable;
                        dailyDone = true;
                        checkBothRequestDone();
                    }

                    @Override
                    public void onForecastFailure(WeatherClientException weatherClientException) {
                        Timber.e(weatherClientException, "Failed to get daily forecast");
                        setForecastDataState(ForecastDataState.ERROR_FORECAST);
                    }
                });
            }

            @Override
            public void onLocationFailure(UserLocationFetcher.UserLocationException exception) {
                Timber.e(exception, "Failed to get the location");
                setForecastDataState(ForecastDataState.ERROR_LOCATION);
            }

            private void checkBothRequestDone() {
                if(hourlyDone && dailyDone) {
                    setForecastDataState(ForecastDataState.LOADED);
                }
            }
        });
    }

    private void setupUI() {
        setContentView(R.layout.activity_main);

        frameLoading = findViewById(R.id.frame_loading);
        frameError = findViewById(R.id.frame_error);
        errorMessage = (CustomTextView) findViewById(R.id.frame_error_message);
        errorButton = (Button) findViewById(R.id.frame_error_button);
        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        slidingPanelToday = findViewById(R.id.sliding_panel_today);
        slidingPanelWeek = findViewById(R.id.sliding_panel_week);
        slidingPanelSummary = (CustomTextView) findViewById(R.id.sliding_panel_summary);
        nowTemperature = (CustomTextView) findViewById(R.id.now_temp);
        nowTemperatureButton = (CustomTextView) findViewById(R.id.temperature_button_label);
        minTemperature = (CustomTextView) findViewById(R.id.today_min_temp);
        maxTemperature = (CustomTextView) findViewById(R.id.today_max_temp);
        temperatureSymbol = (CustomTextView) findViewById(R.id.today_symbol_temp);
        heartButton = findViewById(R.id.heart_button);
        hourlyLinear = (LinearLayout) findViewById(R.id.hourly_forecast);
        hourlyScroll = (HorizontalScrollView) findViewById(R.id.hourly_scroll);
        cityLabel = (CustomTextView) findViewById(R.id.week_forecast_city);

        todayDivider = findViewById(R.id.today_forecast_divider);
        todayMinTemperatureIcon = (ImageView) findViewById(R.id.today_min_temp_icon);
        todayMaxTemperatureIcon = (ImageView) findViewById(R.id.today_max_temp_icon);

        todayCollapsedBackgroundColor = getResources().getColor(R.color.today_collapsed_background);
        todayCollapsedPrimaryColor = getResources().getColor(R.color.today_collapsed_primary_color);
        todayCollapsedSecondaryColor = getResources().getColor(R.color.today_collapsed_secondary_color);
        todayExpandedBackgroundColor = getResources().getColor(R.color.today_expanded_background);
        todayExpandedPrimaryColor = getResources().getColor(R.color.today_expanded_primary_color);
        todayExpandedSecondaryColor = getResources().getColor(R.color.today_expanded_secondary_color);

        positionHeartPanelExpanded = getResources().getDimension(R.dimen.floating_button_heart_translate_out_screen);

        setupClothesViewPager();
        setupWeekForecastList();

        panelListener = new PanelListener();
        slidingPanel.setPanelSlideListener(panelListener);

        findViewById(R.id.sliding_panel_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!activityDestroyed) {
                    computeSlidingPanelSizes();
                }
            }
        });

        TextView feedback = (TextView) findViewById(R.id.frame_error_support);
        feedback.setText(Html.fromHtml(getString(R.string.support)));
        feedback.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupClothesViewPager() {
        clothesPager = (ViewPager) findViewById(R.id.clothes_view_pager);
        List<Clothes> clothesList = new ArrayList<Clothes>();

        clothesList.add(new Clothes(R.drawable.lucky_3));
        clothesList.add(new Clothes(R.drawable.lucky_1));
        clothesList.add(new Clothes(R.drawable.lucky_2));
        clothesList.add(new Clothes(R.drawable.lucky_4));
        clothesList.add(new Clothes(R.drawable.lucky_5));
        clothesList.add(new Clothes(R.drawable.ann_taylor_1));
        clothesList.add(new Clothes(R.drawable.ann_taylor_2));
        clothesList.add(new Clothes(R.drawable.ann_taylor_3));
        clothesList.add(new Clothes(R.drawable.ann_taylor_4));
        clothesList.add(new Clothes(R.drawable.ann_taylor_5));
        clothesList.add(new Clothes(R.drawable.blogger_1));
        clothesList.add(new Clothes(R.drawable.blogger_2));
        clothesList.add(new Clothes(R.drawable.blogger_3));
        clothesList.add(new Clothes(R.drawable.blogger_4));
        clothesList.add(new Clothes(R.drawable.blogger_5));
        clothesList.add(new Clothes(R.drawable.blogger_6));
        clothesList.add(new Clothes(R.drawable.blogger_7));
        clothesList.add(new Clothes(R.drawable.blogger_8));
        clothesList.add(new Clothes(R.drawable.blogger_9));
        clothesList.add(new Clothes(R.drawable.blogger_10));

        clothesPager.setAdapter(new ClothesPagerAdapter(getSupportFragmentManager(), clothesList));
        clothesPager.setOnPageChangeListener(new ClothesPagerListener());
    }

    private void setupWeekForecastList() {
        ListView weekList = (ListView) findViewById(R.id.week_forecast_list_view);
        dailyAdapter = new DailyForecastAdapter(getLayoutInflater(), null);
        weekList.setAdapter(dailyAdapter);
        weekList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSlidingPanelClick(null);
            }
        });
    }

    private void computeSlidingPanelSizes() {
        slidingPanel.setPanelHeight(slidingPanelToday.getMeasuredHeight());
        slidingPanel.setAnchorPoint((float) slidingPanelSummary.getMeasuredHeight() / (slidingPanelSummary.getMeasuredHeight() + slidingPanelWeek.getMeasuredHeight()));

        positionHeartPanelCollapsed = - slidingPanelToday.getMeasuredHeight()
                - getResources().getDimension(R.dimen.default_margin_padding)
                + getResources().getDimension(R.dimen.floating_button_heart_margin_bottom);
        positionHeartPanelAnchored = positionHeartPanelCollapsed - slidingPanelSummary.getMeasuredHeight();
    }

    private void updateUI() {
        if(!activityDestroyed) {
            switch (dataState) {
                case LOADING:
                    frameLoading.setVisibility(View.VISIBLE);
                    frameError.setVisibility(View.INVISIBLE);
                    break;

                case ERROR_LOCATION:
                    errorMessage.setText(getString(R.string.status_frame_error_location));
                    errorButton.setText(getString(R.string.status_frame_settings_button));
                    frameError.setVisibility(View.VISIBLE);
                    frameLoading.setVisibility(View.INVISIBLE);
                    break;

                case ERROR_FORECAST:
                    errorMessage.setText(getString(R.string.status_frame_error_forecast));
                    errorButton.setText(getString(R.string.status_frame_retry_button));
                    frameError.setVisibility(View.VISIBLE);
                    frameLoading.setVisibility(View.INVISIBLE);
                    break;

                case LOADED:
                    String temperature = temperatureFormat.format(forecastTable.getBaselineForecast().getWeatherWrapper().getTemperature(localeScale));
                    nowTemperature.setText(temperature);
                    nowTemperatureButton.setText(temperature + getString(R.string.temperature_symbol));
                    minTemperature.setText(temperatureFormat.format(day.getMinTemperature().getWeatherWrapper().getTemperature(localeScale)));
                    maxTemperature.setText(temperatureFormat.format(day.getMaxTemperature().getWeatherWrapper().getTemperature(localeScale)));
                    slidingPanelSummary.setText(forecastSummaryMessage);
                    updateHourlyForecastList();
                    updateDailyForecastList();

                    slidingPanel.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            demoPanel();
                        }
                    }, FORECAST_DATA_DONE_DELAY/2);

                    slidingPanel.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setForecastDataState(ForecastDataState.DONE);
                        }
                    }, FORECAST_DATA_DONE_DELAY);
                    break;

                case DONE:
                    frameLoading.setVisibility(View.INVISIBLE);
                    frameError.setVisibility(View.INVISIBLE);

                    animateHeartButton(positionHeartPanelHidden);
                    slidingPanel.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hidePanel();
                        }
                    }, PANEL_DEMO_EXPANDED_DURATION);
                    break;
            }
        }
    }

    private void updateHourlyForecastList() {
        hourlyLinear.removeAllViews();
        hourlyTextViews = new ArrayList<TextView>();
        hourlyIcons = new ArrayList<ImageView>();

        String temperatureSymbol = getString(R.string.temperature_symbol);

        for(int i=0; i < Math.min(MAX_FORECAST_ITEMS, forecastTable.getHourlyForecastList().size()); i++) {
            Forecast current = forecastTable.getHourlyForecastList().get(i);

            View view = LayoutInflater.from(this).inflate(R.layout.item_hourly_forecast, null);
            ImageView icon = (ImageView) view.findViewById(R.id.forecast_icon);
            TextView temp = (TextView) view.findViewById(R.id.forecast_temp);
            TextView hour = (TextView) view.findViewById(R.id.forecast_hour);

            hourlyTextViews.add(temp);
            hourlyTextViews.add(hour);
            hourlyIcons.add(icon);

            Picasso.with(this).load(getWeatherIcon(current.getWeatherWrapper().getWeatherType())).into(icon);
            temp.setText(temperatureFormat.format(current.getWeatherWrapper().getTemperature(localeScale)) + temperatureSymbol);
            hour.setText(UiUtil.getReadableHour(current.getInterval().getStart()));

            hourlyLinear.addView(view);
        }
    }

    private void updateDailyForecastList() {
        cityLabel.setText(city.toUpperCase());
        dailyAdapter.updateForecast(dailyForecastTable.getDailyForecastList());
    }

    private int getWeatherIcon(WeatherType type) {
        // TODO: Pensar que hacer con las versiones night.
        switch (type) {
            case CLEAR:
                return R.drawable.ic_clear;
            case CLOUDY:
                return R.drawable.ic_cloudy;
            case RAIN:
                return R.drawable.ic_rain;
            case SNOW:
                // FIXME: Falta el icono de SNOW
                return R.drawable.ic_rain;
            default:
                // FIXME: No tenemos icono default, aunque en realidad aquí no pueden llegar tipos de tiempo desconocidos porque los elimina ForecastTable.
                return R.drawable.ic_clear;
        }
    }

    private void animateColors(PanelScrollValue panelScrollValue) {
        boolean isToExpanded = panelScrollValue.equals(PanelScrollValue.EXPANDED);
        Integer backgroundColorFrom = isToExpanded ? todayCollapsedBackgroundColor : todayExpandedBackgroundColor;
        Integer primaryColorFrom = isToExpanded ? todayCollapsedPrimaryColor : todayExpandedPrimaryColor;
        Integer secondaryColorFrom = isToExpanded ? todayCollapsedSecondaryColor : todayExpandedSecondaryColor;
        Integer backgroundColorTo = isToExpanded ? todayExpandedBackgroundColor : todayCollapsedBackgroundColor;
        Integer primaryColorTo = isToExpanded ? todayExpandedPrimaryColor : todayCollapsedPrimaryColor;
        Integer secondaryColorTo = isToExpanded ? todayExpandedSecondaryColor : todayCollapsedSecondaryColor;

        ValueAnimator backgroundAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), backgroundColorFrom, backgroundColorTo);
        backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Integer value = (Integer) animator.getAnimatedValue();
                slidingPanelToday.setBackgroundColor(value);
            }
        });

        ValueAnimator primaryAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), primaryColorFrom, primaryColorTo);
        primaryAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Integer value = (Integer) animator.getAnimatedValue();
                nowTemperature.setTextColor(value);
                temperatureSymbol.setTextColor(value);
                todayDivider.setBackgroundColor(value);
                for (TextView tv : hourlyTextViews) {
                    tv.setTextColor(value);
                }
            }
        });

        ValueAnimator secondaryAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), secondaryColorFrom, secondaryColorTo);
        secondaryAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Integer value = (Integer) animator.getAnimatedValue();
                minTemperature.setTextColor(value);
                maxTemperature.setTextColor(value);
                todayMinTemperatureIcon.setColorFilter(value);
                todayMaxTemperatureIcon.setColorFilter(value);
                for (ImageView iv : hourlyIcons) {
                    iv.setColorFilter(value);
                }
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(backgroundAnimator, primaryAnimator, secondaryAnimator);
        animatorSet.setDuration(COLOR_TRANSITION_DURATION);
        animatorSet.start();

        // This is for change the scrollbar color. It uses reflection, if it fails there is nothing to do.
        int scrollDrawableRef = isToExpanded ? R.drawable.scrollbar_light : R.drawable.scrollbar_dark;
        try
        {
            Field mScrollCacheField = View.class.getDeclaredField("mScrollCache");
            mScrollCacheField.setAccessible(true);
            Object mScrollCache = mScrollCacheField.get(hourlyScroll);
            Field scrollBarField = mScrollCache.getClass().getDeclaredField("scrollBar");
            scrollBarField.setAccessible(true);
            Object scrollBar = scrollBarField.get(mScrollCache);
            Method method = scrollBar.getClass().getDeclaredMethod("setHorizontalThumbDrawable", Drawable.class);
            method.setAccessible(true);
            method.invoke(scrollBar, getResources().getDrawable(scrollDrawableRef));
        }
        catch(Exception e) {
            // Reflection fail, so the scrollbar remains in the original color.
        }
    }

    private void animateHeartButton(float translationY) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(heartButton, "translationY", translationY);
        anim.setDuration(HEART_BUTTON_ANIMATION_DURATION);
        anim.start();
    }

    private void showPanel() {
        slidingPanel.showPanel();
    }

    private void demoPanel() {
        animateColors(PanelScrollValue.EXPANDED);
        slidingPanel.expandPanel(PanelScrollValue.EXPANDED.getScrollValue());
    }

    private void hidePanel() {
        panelListener.stopListening();

        if(slidingPanel.isPanelExpanded() || slidingPanel.isPanelAnchored()) {
            animateColors(PanelScrollValue.COLLAPSED);
        }
        slidingPanel.hidePanel();
    }

    private class PanelListener extends SlidingUpPanelLayout.SimplePanelSlideListener {
        private boolean listening = false;
        private float lastScroll;

        public void startListening(PanelScrollValue panelScrollValue) {
            this.lastScroll = panelScrollValue.getScrollValue();
            listening = true;
        }

        public void stopListening() {
            listening = false;
        }

        @Override
        public void onPanelSlide(View view, float scrollValue) {

            if(listening) {
                if(scrollValue > PanelScrollValue.COLLAPSED.getScrollValue() && lastScroll == PanelScrollValue.COLLAPSED.getScrollValue()) {
                    animateColors(PanelScrollValue.EXPANDED);
                }
                else if(scrollValue == PanelScrollValue.COLLAPSED.getScrollValue() && lastScroll > PanelScrollValue.COLLAPSED.getScrollValue()) {
                    animateColors(PanelScrollValue.COLLAPSED);
                }
                lastScroll = scrollValue;
            }
        }

        @Override
        public void onPanelCollapsed(View view) {
            startListening(PanelScrollValue.COLLAPSED);
            animateHeartButton(positionHeartPanelCollapsed);
        }

        @Override
        public void onPanelExpanded(View view) {
            animateHeartButton(positionHeartPanelExpanded);
        }

        @Override
        public void onPanelAnchored(View view) {
            animateHeartButton(positionHeartPanelAnchored);
        }

        @Override
        public void onPanelHidden(View view) {
            animateHeartButton(positionHeartPanelHidden);
        }
    }

    private class ClothesPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageSelected(int position) { }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(state == ViewPager.SCROLL_STATE_IDLE) {
                hidePanel();
            }
        }
    }

    private class DailyForecastAdapter extends BaseAdapter {
        private List<DailyForecast> dailyForecasts;
        private LayoutInflater inflater;

        public DailyForecastAdapter(LayoutInflater inflater, List<DailyForecast> dailyForecasts) {
            this.inflater = inflater;
            this.dailyForecasts = dailyForecasts;
        }

        public void updateForecast(List<DailyForecast> dailyForecasts) {
            this.dailyForecasts = dailyForecasts;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dailyForecasts != null ? Math.min(WEEK_FORECAST_DAYS, dailyForecasts.size() - 1) : 0; // First is today
        }

        @Override
        public Object getItem(int position) {
            return dailyForecasts.get(position + 1); // First is today
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_daily_forecast, null);
                convertView.setTag(new DailyForecastHolder(convertView));
            }

            ((DailyForecastHolder) convertView.getTag()).update(dailyForecasts.get(position + 1)); // First is today

            return convertView;
        }

        private class DailyForecastHolder {
            private ImageView icon;
            private TextView day;
            private TextView minTemperature;
            private TextView maxTemperature;

            public DailyForecastHolder(View v) {
                icon = (ImageView) v.findViewById(R.id.daily_forecast_icon);
                day = (TextView) v.findViewById(R.id.daily_forecast_day);
                minTemperature = (TextView) v.findViewById(R.id.daily_forecast_min_temperature);
                maxTemperature = (TextView) v.findViewById(R.id.daily_forecast_max_temperature);
            }

            public void update(DailyForecast dailyForecast) {
                icon.setImageResource(getWeatherIcon(dailyForecast.getWeatherWrapper().getWeatherType()));
                day.setText(getDayOfWeek(dailyForecast.getDay()));
                minTemperature.setText(temperatureFormat.format(dailyForecast.getWeatherWrapper().getMinTemperature(localeScale)));
                maxTemperature.setText(temperatureFormat.format(dailyForecast.getWeatherWrapper().getMaxTemperature(localeScale)));
            }
        }
    }

    private String getDayOfWeek(DateTime day) {
        switch (day.getDayOfWeek()) {
            case DateTimeConstants.MONDAY:
                return getString(R.string.day_monday);
            case DateTimeConstants.TUESDAY:
                return getString(R.string.day_tuesday);
            case DateTimeConstants.WEDNESDAY:
                return getString(R.string.day_wednesday);
            case DateTimeConstants.THURSDAY:
                return getString(R.string.day_thursday);
            case DateTimeConstants.FRIDAY:
                return getString(R.string.day_friday);
            case DateTimeConstants.SATURDAY:
                return getString(R.string.day_saturday);
            case DateTimeConstants.SUNDAY:
                return getString(R.string.day_sunday);
            default:
                return ""; // Impossible
        }
    }

    /** Linked from the XML. */
    public void onErrorRetry(View v) {
        if(dataState.equals(ForecastDataState.ERROR_LOCATION)) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        else if(dataState.equals(ForecastDataState.ERROR_FORECAST)) {
            setForecastDataState(ForecastDataState.LOADING);
        }
    }

    /** Linked from the XML. */
    public void onHeartClick(View v) {
        // Nothing for the moment.
    }

    /** Linked from the XML. */
    public void onTemperatureButtonClick(View v) {
        showPanel();
    }

    /** Linked from the XML. */
    public void onClothesClick(View v) {
        if(slidingPanel.isPanelExpanded() || slidingPanel.isPanelAnchored()) {
            slidingPanel.collapsePanel();
        }
        else {
            clothesPager.setCurrentItem((clothesPager.getCurrentItem() + 1) % clothesPager.getAdapter().getCount());
        }
    }

    /** Linked from the XML. */
    public void onSlidingPanelClick(View v) {
        if(slidingPanel.isPanelAnchored() || slidingPanel.isPanelExpanded()) {
            slidingPanel.collapsePanel();
        }
        else {
            slidingPanel.anchorPanel();
        }
    }
}
