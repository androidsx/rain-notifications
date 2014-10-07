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

import com.androidsx.rainnotifications.backgroundservice.util.NotificationHelper;
import com.androidsx.rainnotifications.backgroundservice.util.UserLocationFetcher;
import com.androidsx.rainnotifications.dailyclothes.model.Clothes;
import com.androidsx.rainnotifications.dailyclothes.quickreturn.QuickReturnHelper;
import com.androidsx.rainnotifications.dailyclothes.quickreturn.QuickReturnListView;
import com.androidsx.rainnotifications.dailyclothes.widget.CustomFontTextView;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.weatherclientfactory.WeatherClientFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private Random random = new Random();
    private List<Clothes> clothesList = new ArrayList<Clothes>();
    private List<ForecastListItem> forecastListItems = new ArrayList<ForecastListItem>();
    private CustomListAdapter adapter;

    private QuickReturnListView mListView;
    private CustomFontTextView mQuickReturnView;
    private View mPlaceHolder;

    private int maxTemp = 0;
    private int todayNumClicks = 0;
    private static final int CLICKS_FOR_FIRST_MESSAGE = 3;
    private static final int CLICKS_FOR_SECOND_MESSAGE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateUI();
    }

    private void updateUI() {
        mQuickReturnView = (CustomFontTextView)findViewById(R.id.forecast_message);
        mPlaceHolder = findViewById(R.id.layout_weather);
        mListView = (QuickReturnListView)findViewById(R.id.clothes_list_view);

        fillForecastView();
        fillClothesListView();
        configureQuickReturn();
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
        UserLocationFetcher.getUserLocation(this, new UserLocationFetcher.UserLocationResultListener() {
            @Override
            public void onLocationSuccess(Location location) {
                WeatherClientFactory.requestForecastForLocation(MainActivity.this, location.getLatitude(), location.getLongitude(), new WeatherClientResponseListener() {
                    @Override
                    public void onForecastSuccess(ForecastTable forecastTable) {
                        for (Forecast f : forecastTable.getRawForecastList()) {
                            forecastListItems.add(
                                    new ForecastListItem(
                                            getRandomWeatherIcon(),
                                            getRandomBetweenNumbers(60, 67),
                                            f.getInterval().getStart().getHourOfDay()));
                        }
                        loadForecastList();
                    }

                    @Override
                    public void onForecastFailure(WeatherClientException weatherClientException) {

                    }
                });
            }

            @Override
            public void onLocationFailure(UserLocationFetcher.UserLocationException exception) {

            }
        });
    }

    private void loadForecastList() {
        ViewGroup forecastView = (ViewGroup) findViewById(R.id.hourly_forecast);
        for (ForecastListItem f : forecastListItems) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.hourly_forecast_item, null);
            ImageView icon = (ImageView) view.findViewById(R.id.forecast_icon);
            TextView temp = (TextView) view.findViewById(R.id.forecast_temp);
            TextView hour = (TextView) view.findViewById(R.id.forecast_hour);
            Picasso.with(MainActivity.this).load(f.getIcon()).into(icon);
            temp.setText(f.getTemp() + "º");
            hour.setText(f.getHour() + "h");
            forecastView.addView(view);
            if (f.getTemp() > maxTemp) {
                maxTemp = f.getTemp();
            }
        }
        ((TextView) findViewById(R.id.forecast_message)).setText(
                Html.fromHtml(String.format(getString(R.string.forecast_first_message), maxTemp)));
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

    private void configureQuickReturn() {
        QuickReturnHelper.configureQuickReturn(mQuickReturnView, mListView, mPlaceHolder);
    }

    private int getRandomBetweenNumbers(int minValue, int maxValue) {
        return random.nextInt((maxValue + 1) - minValue) + minValue;
    }

    private int getRandomWeatherIcon() {
        final TypedArray mascotTypedArray = getResources().obtainTypedArray(R.array.weatherIcons);
        final int mascotIndex = random.nextInt(mascotTypedArray.length());
        return mascotTypedArray.getResourceId(mascotIndex, -1);
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

    private class ForecastListItem {
        int icon;
        int temp;
        int hour;

        public ForecastListItem(int icon, int temp, int hour) {
            this.icon = icon;
            this.temp = temp;
            this.hour = hour;
        }

        public int getIcon() {
            return icon;
        }

        public int getTemp() {
            return temp;
        }

        public int getHour() {
            return hour;
        }
    }
}
