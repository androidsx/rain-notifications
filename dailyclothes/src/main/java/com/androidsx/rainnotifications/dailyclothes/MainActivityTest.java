package com.androidsx.rainnotifications.dailyclothes;

import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.alert.DayTemplateGenerator;
import com.androidsx.rainnotifications.backgroundservice.util.UserLocationFetcher;
import com.androidsx.rainnotifications.dailyclothes.model.Clothes;
import com.androidsx.rainnotifications.dailyclothes.widget.CustomFontTextView;
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


public class MainActivityTest extends FragmentActivity {

    private static final Duration EXPIRATION_DURATION = Duration.standardSeconds(5); // TODO: Use this Duration.standardHours(1)
    private static final int MAX_FORECAST_ITEMS = 24;
    private static final String TEMPERATURE_SYMBOL = "°";

    private enum ForecastDataState {LOADING, ERROR, DONE};

    private WeatherWrapper.TemperatureScale localeScale;
    private DecimalFormat temperatureFormat = new DecimalFormat("#");
    private ForecastDataState dataState;
    private ForecastTable forecastTable;
    private DateTime forecastTableTime;
    private Day day;
    private String forecastMessage;
    private MainImagePagerAdapter adapter;
    private List<Clothes> clothesList = new ArrayList<Clothes>();

    private boolean destroyed = false;
    private View frameMain;
    private View frameLoading;
    private View frameError;
    private CustomFontTextView nowTemperature;
    private CustomFontTextView minTemperature;
    private CustomFontTextView maxTemperature;
    private SlidingUpPanelLayout bottomSheet;
    private ViewPager imagesPager;


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
        destroyed = true;
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
                forecastMessage = null;
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
                WeatherClientFactory.requestForecastForLocation(MainActivityTest.this, location.getLatitude(), location.getLongitude(), new WeatherClientResponseListener() {
                    @Override
                    public void onForecastSuccess(ForecastTable forecastTable) {
                        MainActivityTest.this.forecastTable = forecastTable;
                        MainActivityTest.this.forecastTableTime = new DateTime();
                        MainActivityTest.this.day = new Day(forecastTable);

                        DayTemplate template = new DayTemplateGenerator(DayTemplateLoaderFactory.getDayTemplateLoader(MainActivityTest.this)).getDayTemplate(day);
                        if (template == null) {
                            MainActivityTest.this.forecastMessage = getString(R.string.default_day_message);
                        } else {
                            MainActivityTest.this.forecastMessage = template.resolveMessage(MainActivityTest.this, MainActivityTest.this.day);
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
        setContentView(R.layout.activity_main_activity_test);

        frameMain = findViewById(R.id.frame_main);
        frameLoading = findViewById(R.id.frame_loading);
        frameError = findViewById(R.id.frame_error);
        bottomSheet = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        imagesPager = (ViewPager) frameMain.findViewById(R.id.view_pager);
        nowTemperature = (CustomFontTextView) findViewById(R.id.now_temp);
        minTemperature = (CustomFontTextView) findViewById(R.id.today_min_temp);
        maxTemperature = (CustomFontTextView) findViewById(R.id.today_max_temp);


        bottomSheet.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                Log.d("TMP", "onPanelSlide: " + v);
            }

            @Override
            public void onPanelCollapsed(View view) {
                Log.d("TMP", "onPanelCollapsed");
            }

            @Override
            public void onPanelExpanded(View view) {
                Log.d("TMP", "onPanelExpanded");
            }

            @Override
            public void onPanelAnchored(View view) {
                Log.d("TMP", "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View view) {
                Log.d("TMP", "onPanelHidden");
            }
        });

        frameError.findViewById(R.id.button_error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setForecastDataState(ForecastDataState.LOADING);
            }
        });

        fillClothesViewPager();
    }

    private void updateUI() {
        // FIXME: La fuente utilizada muestra los símbolos de temperatura en negrita, Aghhh!
        if(!destroyed) {
            switch (dataState) {
                case LOADING:
                    frameLoading.setVisibility(View.VISIBLE);

                    frameError.setVisibility(View.INVISIBLE);
                    frameMain.setVisibility(View.INVISIBLE);
                    break;
                case ERROR:
                    frameError.setVisibility(View.VISIBLE);

                    frameLoading.setVisibility(View.INVISIBLE);
                    frameMain.setVisibility(View.INVISIBLE);
                    break;
                case DONE:
                    frameMain.setVisibility(View.VISIBLE);

                    frameLoading.setVisibility(View.INVISIBLE);
                    frameError.setVisibility(View.INVISIBLE);

                    nowTemperature.setText(temperatureFormat.format(forecastTable.getBaselineForecast().getWeatherWrapper().getTemperature(localeScale)) + TEMPERATURE_SYMBOL);
                    minTemperature.setText(temperatureFormat.format(day.getMinTemperature().getWeatherWrapper().getTemperature(localeScale)));
                    maxTemperature.setText(temperatureFormat.format(day.getMaxTemperature().getWeatherWrapper().getTemperature(localeScale)));
                    ((TextView)findViewById(R.id.forecast_message)).setText(forecastMessage); //TODO: Utilizar variable para esto
                    fillForecastView();


                    break;
            }
        }
    }

    private void fillForecastView() {
        ViewGroup forecastView = (ViewGroup)findViewById(R.id.hourly_forecast);
        forecastView.removeAllViews();

        for(int i=0; i < Math.min(MAX_FORECAST_ITEMS, forecastTable.getHourlyForecastList().size()); i++) {
            Forecast current = forecastTable.getHourlyForecastList().get(i);

            View view = LayoutInflater.from(this).inflate(R.layout.hourly_forecast_item, null);
            ImageView icon = (ImageView) view.findViewById(R.id.forecast_icon);
            TextView temp = (TextView) view.findViewById(R.id.forecast_temp);
            TextView hour = (TextView) view.findViewById(R.id.forecast_hour);

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

    private void fillClothesViewPager() {
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

        adapter = new MainImagePagerAdapter(getSupportFragmentManager(), clothesList);
        imagesPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class MainImagePagerAdapter extends FragmentStatePagerAdapter {

        private List<Clothes> clothesList;

        public MainImagePagerAdapter(FragmentManager fm, List<Clothes> clothesList) {
            super(fm);
            this.clothesList = clothesList;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.newInstance(clothesList.get(position).getPhoto());
        }

        @Override
        public int getCount() {
            return clothesList.size();
        }
    }

    public static class ImageFragment extends Fragment {

        private static final String ARG_IMAGE_RESOURCE = "ImageFragment:imageResource";
        private int imageResource;

        public static ImageFragment newInstance(int imageResource) {
            ImageFragment fragment = new ImageFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_IMAGE_RESOURCE, imageResource);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            imageResource = getArguments().getInt(ARG_IMAGE_RESOURCE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_image, container, false);

            ((ImageView) rootView.findViewById(R.id.image_view)).setImageResource(imageResource); //TODO: Hacer con Picasso

            return rootView;
        }
    }


}
