package com.androidsx.rainnotifications.dailyclothes.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.rainnotifications.dailyclothes.R;
import com.androidsx.rainnotifications.dailyclothes.model.MockDailyForecast;

import java.util.List;

public class DailyForecastAdapter extends BaseAdapter {
    private List<MockDailyForecast> dailyForecasts;
    private LayoutInflater inflater;

    public DailyForecastAdapter(LayoutInflater inflater, List<MockDailyForecast> dailyForecasts) {
        this.inflater = inflater;
        this.dailyForecasts = dailyForecasts;
    }

    @Override
    public int getCount() {
        return dailyForecasts.size();
    }

    @Override
    public Object getItem(int position) {
        return dailyForecasts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_daily_forecast, null);
            convertView.setTag(new DailyForecastHolder(convertView));
        }

        ((DailyForecastHolder) convertView.getTag()).update(dailyForecasts.get(position));

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

        public void update(MockDailyForecast mockDailyForecast) {
            icon.setImageResource(mockDailyForecast.iconRes);
            day.setText(mockDailyForecast.day);
            minTemperature.setText("" + mockDailyForecast.minTemperature);
            maxTemperature.setText("" + mockDailyForecast.maxTemperature);
        }
    }
}