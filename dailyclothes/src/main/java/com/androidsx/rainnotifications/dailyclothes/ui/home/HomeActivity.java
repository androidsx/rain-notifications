package com.androidsx.rainnotifications.dailyclothes.ui.home;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidsx.rainnotifications.alert.DayTemplateGenerator;
import com.androidsx.rainnotifications.backgroundservice.util.UserLocationFetcher;
import com.androidsx.rainnotifications.dailyclothes.R;
import com.androidsx.rainnotifications.dailyclothes.model.Clothes;
import com.androidsx.rainnotifications.dailyclothes.model.MockDailyForecast;
import com.androidsx.rainnotifications.dailyclothes.ui.widget.customfont.CustomTextView;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
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
import org.joda.time.Duration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class HomeActivity extends FragmentActivity {

    private static final Duration EXPIRATION_DURATION = Duration.standardSeconds(5); // TODO: Use this Duration.standardHours(1)
    private static final int MAX_FORECAST_ITEMS = 24;
    private static final String TEMPERATURE_SYMBOL = "°";
    private static final int COLOR_TRANSITION_DURATION = 100;

    private enum ForecastDataState {LOADING, ERROR, DONE};

    private ForecastDataState dataState;
    private DateTime forecastTableTime;
    private ForecastTable forecastTable;
    private Day day;
    private String forecastSummaryMessage;

    private boolean activityDestroyed = false; // Panic mode. It's used for not modify any view.
    private View frameLoading;
    private View frameError;
    private SlidingUpPanelLayout slidingPanel;
    private View slidingPanelToday;
    private View slidingPanelWeek;
    private CustomTextView slidingPanelSummary;
    private CustomTextView nowTemperature;
    private CustomTextView minTemperature;
    private CustomTextView maxTemperature;
    private View heartButton;
    private ViewPager clothesPager;

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
        if(dataState == null || dataState.equals(ForecastDataState.ERROR)) {
            setForecastDataState(ForecastDataState.LOADING);
        }
        else if(dataState.equals(ForecastDataState.DONE) && new Duration(forecastTableTime, new DateTime()).isLongerThan(EXPIRATION_DURATION)) {
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
            case ERROR:
                break;
            case DONE:
                break;
        }

        updateUI();
    }

    private void getForecastData() {
        // FIXME: we do exactly the same in the weather service. grr..
        UserLocationFetcher.getUserLocation(this, new UserLocationFetcher.UserLocationResultListener() {
            @Override
            public void onLocationSuccess(final Location location) {
                WeatherClientFactory.requestForecastForLocation(HomeActivity.this, location.getLatitude(), location.getLongitude(), new WeatherClientResponseListener() {
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

                        setForecastDataState(ForecastDataState.DONE);
                    }

                    @Override
                    public void onForecastFailure(WeatherClientException exception) {
                        Timber.e(exception, "Failed to get the forecast");
                        setForecastDataState(ForecastDataState.ERROR);
                    }
                });
            }

            @Override
            public void onLocationFailure(UserLocationFetcher.UserLocationException exception) {
                Timber.e(exception, "Failed to get the location");
                setForecastDataState(ForecastDataState.ERROR);
            }
        });
    }

    private void setupUI() {
        // TODO: Enable support email link.
        setContentView(R.layout.activity_main);

        frameLoading = findViewById(R.id.frame_loading);
        frameError = findViewById(R.id.frame_error);
        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        slidingPanelToday = findViewById(R.id.sliding_panel_today);
        slidingPanelWeek = findViewById(R.id.sliding_panel_week);
        slidingPanelSummary = (CustomTextView) findViewById(R.id.sliding_panel_summary);
        nowTemperature = (CustomTextView) findViewById(R.id.now_temp);
        minTemperature = (CustomTextView) findViewById(R.id.today_min_temp);
        maxTemperature = (CustomTextView) findViewById(R.id.today_max_temp);
        temperatureSymbol = (CustomTextView) findViewById(R.id.today_symbol_temp);
        heartButton = findViewById(R.id.heart_button);

        todayDivider = findViewById(R.id.today_forecast_divider);
        todayMinTemperatureIcon = (ImageView) findViewById(R.id.today_min_temp_icon);
        todayMaxTemperatureIcon = (ImageView) findViewById(R.id.today_max_temp_icon);

        todayCollapsedBackgroundColor = getResources().getColor(R.color.today_collapsed_background);
        todayCollapsedPrimaryColor = getResources().getColor(R.color.today_collapsed_primary_color);
        todayCollapsedSecondaryColor = getResources().getColor(R.color.today_collapsed_secondary_color);
        todayExpandedBackgroundColor = getResources().getColor(R.color.today_expanded_background);
        todayExpandedPrimaryColor = getResources().getColor(R.color.today_expanded_primary_color);
        todayExpandedSecondaryColor = getResources().getColor(R.color.today_expanded_secondary_color);

        setupClothesViewPager();
        setupWeekForecastList();

        findViewById(R.id.sliding_panel_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!activityDestroyed) {
                    computeSlidingPanelSizes();
                    //repositionHeartButton();
                }
            }
        });

        slidingPanel.setPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener(){
            private float lastScroll = 0;

            @Override
            public void onPanelSlide(View view, float v) {
                if(v > 0 && lastScroll == 0) {
                    animateColors(true);
                }
                else if(v == 0 && lastScroll > 0) {
                    animateColors(false);
                }
                lastScroll = v;
            }
        });
    }

    private void setupClothesViewPager() {
        clothesPager = (ViewPager) findViewById(R.id.clothes_view_pager);
        List<Clothes> clothesList = new ArrayList<Clothes>();

        clothesList.add(new Clothes(R.drawable.lucky_1));
        clothesList.add(new Clothes(R.drawable.lucky_2));
        clothesList.add(new Clothes(R.drawable.lucky_3));
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
    }

    private void setupWeekForecastList() {
        ListView weekList = (ListView) findViewById(R.id.week_forecast_list_view);

        weekList.setAdapter(new DailyForecastAdapter(getLayoutInflater(), MockDailyForecast.getMockList()));
        weekList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSlidingPanelClick(null);
            }
        });
    }

    private void updateUI() {
        if(!activityDestroyed) {
            switch (dataState) {
                case LOADING:
                    frameLoading.setVisibility(View.VISIBLE);
                    frameError.setVisibility(View.INVISIBLE);
                    break;
                case ERROR:
                    frameError.setVisibility(View.VISIBLE);
                    frameLoading.setVisibility(View.INVISIBLE);
                    break;
                case DONE:
                    nowTemperature.setText(temperatureFormat.format(forecastTable.getBaselineForecast().getWeatherWrapper().getTemperature(localeScale)));
                    minTemperature.setText(temperatureFormat.format(day.getMinTemperature().getWeatherWrapper().getTemperature(localeScale)));
                    maxTemperature.setText(temperatureFormat.format(day.getMaxTemperature().getWeatherWrapper().getTemperature(localeScale)));
                    slidingPanelSummary.setText(forecastSummaryMessage);
                    updateHourlyForecastList();

                    frameLoading.setVisibility(View.INVISIBLE);
                    frameError.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    private void computeSlidingPanelSizes() {
        slidingPanel.setPanelHeight(slidingPanelToday.getMeasuredHeight());
        slidingPanel.setAnchorPoint((float) slidingPanelSummary.getMeasuredHeight() / (slidingPanelSummary.getMeasuredHeight() + slidingPanelWeek.getMeasuredHeight()));
    }

    /**
     * Reposition the heart on top of the panel. Ideally, it would be R.dimen.default_margin_padding
     * north of the solid color. But, it's not too bad as it is now aligned with the whole panel
     * (that has a transparent band on top).
     */
    private void repositionHeartButton() {
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) heartButton.getLayoutParams();
        int newBottomMargin = slidingPanelToday.getMeasuredHeight();
        layoutParams.setMargins(layoutParams.leftMargin,
                layoutParams.topMargin,
                layoutParams.rightMargin,
                newBottomMargin);
        heartButton.setLayoutParams(layoutParams);
    }

    private void updateHourlyForecastList() {
        ViewGroup forecastView = (ViewGroup)findViewById(R.id.hourly_forecast);
        forecastView.removeAllViews();
        hourlyTextViews = new ArrayList<TextView>();
        hourlyIcons = new ArrayList<ImageView>();

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
            temp.setText(temperatureFormat.format(current.getWeatherWrapper().getTemperature(localeScale)) + TEMPERATURE_SYMBOL);
            hour.setText(UiUtil.getReadableHour(current.getInterval().getStart()));

            forecastView.addView(view);
        }
    }

    private int getWeatherIcon(WeatherType type) {
        TypedArray iconTypedArray = getResources().obtainTypedArray(R.array.weatherIcons);

        switch (type) {
            case CLEAR:
                return iconTypedArray.getResourceId(0,0); //TODO: Que usamos como default?
            case CLOUDY:
                return iconTypedArray.getResourceId(1,0);
            case RAIN:
                return iconTypedArray.getResourceId(2,0);
            case SNOW:
                // FIXME: Falta el icono
                return iconTypedArray.getResourceId(3,0);
            default:
                // TODO: Consultar con Pablo y/o Omar que hacer en este caso.
                return 0;
        }
    }

    private void animateColors(boolean toExpanded) {
        Integer backgroundColorFrom = toExpanded ? todayCollapsedBackgroundColor : todayExpandedBackgroundColor;
        Integer primaryColorFrom = toExpanded ? todayCollapsedPrimaryColor : todayExpandedPrimaryColor;
        Integer secondaryColorFrom = toExpanded ? todayCollapsedSecondaryColor : todayExpandedSecondaryColor;
        Integer backgroundColorTo = toExpanded ? todayExpandedBackgroundColor : todayCollapsedBackgroundColor;
        Integer primaryColorTo = toExpanded ? todayExpandedPrimaryColor : todayCollapsedPrimaryColor;
        Integer secondaryColorTo = toExpanded ? todayExpandedSecondaryColor : todayCollapsedSecondaryColor;

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
    }

    /** Linked from the XML. */
    public void onErrorRetry(View v) {
        setForecastDataState(ForecastDataState.LOADING);
    }

    /** Linked from the XML. */
    public void onHeartClick(View v) {
        // Nothing for the moment.
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

    /** Linked from the XML. */
    public void onArrowButtonClick(View v) {
        if(slidingPanel.isPanelExpanded()) {
            slidingPanel.collapsePanel();
        }
        else {
            slidingPanel.expandPanel();
        }
    }
}
