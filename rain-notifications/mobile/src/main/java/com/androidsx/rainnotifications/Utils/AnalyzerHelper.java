package com.androidsx.rainnotifications.Utils;

import android.util.Log;

import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataBlock;
import com.forecast.io.v2.transfer.DataPoint;

import com.androidsx.rainnotifications.Utils.Constants.ForecastIO.Icon;

import java.util.List;

public class AnalyzerHelper {

    private static final String TAG = AnalyzerHelper.class.getSimpleName();

    Response response;
    DataBlock hourly;
    DataBlock minutely;
    String currentlyForecastIcon;
    long currentTime = System.currentTimeMillis() / 1000;

    public AnalyzerHelper(Response res) {
        this.response = res;
        this.hourly = res.getForecast().getHourly();
        this.minutely = res.getForecast().getMinutely();
        this.currentlyForecastIcon = res.getForecast().getCurrently().getIcon();
    }

    //TODO: make methods for determine when weather going to occur

    public DataPoint nextRainChange() {
        return getNextChange(Icon.RAIN);
    }

    public DataPoint nextCloudyChange() {
        return getNextChange(Icon.CLOUDY);
    }

    public DataPoint nextPartlyCloudyChange() {
        return getNextChange(Icon.PARTLY_CLOUDY_DAY);
    }

    public DataPoint nextClearChange() {
        return getNextChange(Icon.CLEAR_DAY);
    }

    public DataPoint nextSnowChange() {
        return getNextChange(Icon.SNOW);
    }

    public DataPoint nextSleetChange() {
        return getNextChange(Icon.SLEET);
    }

    public DataPoint nextWindChange() {
        return getNextChange(Icon.WIND);
    }

    public DataPoint nextFogChange() {
        return getNextChange(Icon.FOG);
    }

    public DataPoint nextHailChange() {
        return getNextChange(Icon.HAIL);
    }

    public DataPoint nextThunderStormChange() {
        return getNextChange(Icon.THUNDERSTORM);
    }

    public DataPoint nextTornadoChange() {
        return getNextChange(Icon.TORNADO);
    }

    private DataPoint getNextChange(String icon) {
        if(compareTo(currentlyForecastIcon, icon)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!compareTo(dpM.getIcon(), icon) && !dpM.getIcon().equals("") && dpM.getTime() > currentTime) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!compareTo(dpH.getIcon(), icon) && !dpH.getIcon().equals("") && dpH.getTime() > currentTime) {
                        return dpH;
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (compareTo(dpM.getIcon(), icon)  && !dpM.getIcon().equals("") && dpM.getTime() > currentTime) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (compareTo(dpH.getIcon(), icon)  && !dpH.getIcon().equals("") && dpH.getTime() > currentTime) {
                        return dpH;
                    }
                }
            }
        }

        return null;
    }

    public DataPoint highProbabilityRain() {
        long time1 = -1;
        long time2 = -1;
        long nexCloudyTime = -1;
        long nextPartlyCloudyTime = -1;

        DataPoint dp1 = null;
        DataPoint dp2 = null;

        if(nextCloudyChange() != null) {
            nexCloudyTime = nextCloudyChange().getTime();
        }
        if(nextPartlyCloudyChange() != null) {
            nextPartlyCloudyTime = nextPartlyCloudyChange().getTime();
        }

        if(minutely != null && nexCloudyTime > -1) {
            for(DataPoint dpM : minutely.getData()) {
                if(dpM.getTime() == nexCloudyTime && dpM.getTime() > currentTime) {
                    time1 = dpM.getTime();
                    dp1 = dpM;
                }
                if(dpM.getTime() == nextPartlyCloudyTime && dpM.getTime() > currentTime) {
                    time2 = dpM.getTime();
                    dp2 = dpM;
                }
            }
            if(time1 > -1 && time2 > -1) {
                if(time1 < time2) {
                    return dp1;
                } else {
                    return dp2;
                }
            } else if(time1 > -1 && time2 == -1) {
                return dp1;
            } else {
                return dp2;
            }
        }

        if(hourly != null && nexCloudyTime > -1) {
            for(DataPoint dpH : hourly.getData()) {
                if(dpH.getTime() == nexCloudyTime && dpH.getTime() > currentTime) {
                    time1 = dpH.getTime();
                    dp1 = dpH;
                }
                if(dpH.getTime() == nextPartlyCloudyTime && dpH.getTime() > currentTime) {
                    time2 = dpH.getTime();
                    dp2 = dpH;
                }
            }
            if(time1 > -1 && time2 > -1) {
                if(time1 < time2) {
                    return dp1;
                } else {
                    return dp2;
                }
            } else if(time1 > -1 && time2 == -1) {
                return dp1;
            } else {
                return dp2;
            }
        }

        return null;
    }

    public static boolean compareTo(String icon1, String icon2) {
        if(icon1.equals(icon2)) {
            return true;
        } else if (icon2.equals(Icon.CLEAR_NIGHT) || icon2.equals(Icon.CLEAR_DAY)) {
            if (icon1.equals(Icon.CLEAR_NIGHT) || icon1.equals(Icon.CLEAR_DAY)) {
                return true;
            }
        } else if(icon2.equals(Icon.PARTLY_CLOUDY_NIGHT) || icon2.equals(Icon.PARTLY_CLOUDY_DAY)) {
            if (icon1.equals(Icon.PARTLY_CLOUDY_NIGHT) || icon1.equals(Icon.PARTLY_CLOUDY_DAY)) {
                return true;
            }
        }

        return false;
    }
}
