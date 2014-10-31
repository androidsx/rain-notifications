package com.androidsx.rainnotifications.dailyclothes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.alert.DayTemplateGenerator;
import com.androidsx.rainnotifications.backgroundservice.util.NotificationHelper;
import com.androidsx.rainnotifications.backgroundservice.util.UserLocationFetcher;
import com.androidsx.rainnotifications.dailyclothes.model.Clothes;
import com.androidsx.rainnotifications.dailyclothes.quickreturn.QuickReturnHelper;
import com.androidsx.rainnotifications.dailyclothes.quickreturn.QuickReturnListView;
import com.androidsx.rainnotifications.dailyclothes.widget.CustomFontTextView;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
import com.androidsx.rainnotifications.model.DayTemplateLoaderFactory;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.weatherclientfactory.WeatherClientFactory;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends Activity {

    private final static Duration EXPIRATION_DURATION = Duration.standardHours(1);
    private final static int MAX_FORECAST_ITEMS = 24;

    private enum ForecastDataState {LOADING, ERROR, DONE};

    private ForecastDataState dataState;
    private ForecastTable forecastTable;
    private DateTime forecastTableTime;
    private String forecastMessage;
    private List<Clothes> clothesList = new ArrayList<Clothes>();
    private CustomListAdapter adapter;
    private boolean destroyed = false;

    private View frameMain;
    private View frameLoading;
    private View frameError;
    private QuickReturnListView mListView;
    private CustomFontTextView nowTemperature;

    private int maxTemp = 0;
    private int todayNumClicks = 0;
    private static final int CLICKS_FOR_FIRST_MESSAGE = 3;
    private static final int CLICKS_FOR_SECOND_MESSAGE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                WeatherClientFactory.requestForecastForLocation(MainActivity.this, location.getLatitude(), location.getLongitude(), new WeatherClientResponseListener() {
                    @Override
                    public void onForecastSuccess(ForecastTable forecastTable) {
                        MainActivity.this.forecastTable = forecastTable;
                        MainActivity.this.forecastTableTime = new DateTime();
                        MainActivity.this.forecastMessage = new DayTemplateGenerator(DayTemplateLoaderFactory.getDayTemplateLoader(MainActivity.this))
                                .generateMessage(MainActivity.this, forecastTable, getString(R.string.default_day_message));

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

        frameMain = findViewById(R.id.frame_main);
        frameLoading = findViewById(R.id.frame_loading);
        frameError = findViewById(R.id.frame_error);

        nowTemperature = (CustomFontTextView) frameMain.findViewById(R.id.now_temp);
        CustomFontTextView mQuickReturnView = (CustomFontTextView) frameMain.findViewById(R.id.forecast_message);
        View mPlaceHolder = frameMain.findViewById(R.id.layout_weather);
        mListView = (QuickReturnListView) frameMain.findViewById(R.id.clothes_list_view);

        frameError.findViewById(R.id.button_error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setForecastDataState(ForecastDataState.LOADING);
            }
        });

        fillClothesListView();
        QuickReturnHelper.configureQuickReturn(mQuickReturnView, mListView, mPlaceHolder);
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

                    nowTemperature.setText(forecastTable.getBaselineForecast().getWeatherWrapper().getReadableTemperature(this));
                    ((TextView)findViewById(R.id.forecast_message)).setText(forecastMessage);
                    fillForecastView();

                    break;
            }
        }
    }

    public void showNotification(View v) {
        ++todayNumClicks;
        if(todayNumClicks == CLICKS_FOR_FIRST_MESSAGE) {
            showNotificationMessage(1);
        } else if (todayNumClicks == CLICKS_FOR_SECOND_MESSAGE) {
            showNotificationMessage(2);
        }
    }

    private void showNotificationMessage(final int messageId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (messageId == 1) {
                    NotificationHelper.displayStandardNotification(
                            MainActivity.this,
                            new Intent(MainActivity.this, MainActivity.class),
                            Html.fromHtml(String.format(getString(R.string.forecast_first_message), maxTemp)),
                            BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                } else if (messageId == 2) {
                    NotificationHelper.displayStandardNotification(
                            MainActivity.this,
                            new Intent(MainActivity.this, MainActivity.class),
                            Html.fromHtml(String.format(getString(R.string.forecast_second_message), maxTemp)),
                            BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                    todayNumClicks = 0;
                }
            }
        }.execute();
    }

    private void fillForecastView() {
        ViewGroup forecastView = (ViewGroup)findViewById(R.id.hourly_forecast);
        for(int i=0; i < Math.min(MAX_FORECAST_ITEMS, forecastTable.getHourlyForecastList().size()); i++) {
            Forecast current = forecastTable.getHourlyForecastList().get(i);

            View view = LayoutInflater.from(this).inflate(R.layout.hourly_forecast_item, null);
            ImageView icon = (ImageView) view.findViewById(R.id.forecast_icon);
            TextView temp = (TextView) view.findViewById(R.id.forecast_temp);
            TextView hour = (TextView) view.findViewById(R.id.forecast_hour);

            Picasso.with(this).load(getWeatherIcon(current.getWeatherWrapper().getWeatherType())).into(icon);
            temp.setText(current.getWeatherWrapper().getReadableTemperature(this));
            hour.setText(UiUtil.getReadableHour(current.getInterval().getStart()));

            forecastView.addView(view);
        }
    }

    private void fillClothesListView() {
        adapter = new CustomListAdapter(this, clothesList);
        mListView.setAdapter(adapter);
        mListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.header, null));

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

        adapter.notifyDataSetChanged();
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

    public static class CustomListAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private List<Clothes> clothesItems;

        public CustomListAdapter(Context context, List<Clothes> clothesItems) {
            this.context = context;
            this.clothesItems = clothesItems;
        }

        @Override
        public int getCount() {
            return clothesItems.size();
        }

        @Override
        public Object getItem(int location) {
            return clothesItems.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Avoid unnecessary calls to findViewById() on each row, which is expensive!
            ViewHolder holder;

            if (inflater == null)
                inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.clothes_list_item, null);

                // Create a ViewHolder and store references to the children view
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.photo);

                // The tag can be any Object, this just happens to be the ViewHolder
                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }
            holder.icon.setImageDrawable(context.getResources().getDrawable(clothesItems.get(position).getPhoto()));

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView icon;
    }
}
