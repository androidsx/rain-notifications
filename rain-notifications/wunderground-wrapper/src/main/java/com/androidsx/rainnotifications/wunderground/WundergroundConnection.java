package com.androidsx.rainnotifications.wunderground;

import android.content.Context;
import android.util.Log;

import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.ForecastTableBuilder;
import com.fortysevendeg.android.wunderground.api.service.WundergroundApiProvider;
import com.fortysevendeg.android.wunderground.api.service.request.Feature;
import com.fortysevendeg.android.wunderground.api.service.request.Query;
import com.fortysevendeg.android.wunderground.api.service.response.WundergroundResponse;

import it.restrung.rest.cache.RequestCache;
import it.restrung.rest.client.ContextAwareAPIDelegate;

public abstract class WundergroundConnection {

    private static final String TAG = WundergroundConnection.class.getSimpleName();

    Context context;

    public WundergroundConnection(Context context) {
        this.context = context;
    }

    public void getForecast(double latitude, double longitude) {
        WundergroundApiProvider.getClient().query(new ContextAwareAPIDelegate<WundergroundResponse>(context, WundergroundResponse.class, RequestCache.LoadPolicy.NEVER) {
            @Override
            public void onResults(WundergroundResponse wundergroundResponse) {
                Log.v(TAG, "Raw response from Forecast.io:\n" + wundergroundResponse);

                final ForecastTable forecastTable = ForecastTableBuilder.buildFromWunderground(wundergroundResponse);
                Log.d(TAG, "Transition table: " + forecastTable);

                onSuccess(forecastTable);
            }

            @Override
            public void onError(Throwable e) {
                onFailure(e);
            }
        }, Constants.API_KEY, Query.latLng(latitude, longitude), Feature.conditions, Feature.hourly);
    }

    public abstract void onSuccess(ForecastTable forecastTable);
    public abstract void onFailure(Throwable e);
}
