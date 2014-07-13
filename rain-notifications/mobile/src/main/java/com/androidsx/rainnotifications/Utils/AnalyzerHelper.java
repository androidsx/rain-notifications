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

    public DataPoint nextRain() {
        return getNext(Icon.RAIN);
    }

    public DataPoint nextCloudy() {
        return getNext(Icon.CLOUDY);
    }

    public DataPoint nextPartlyCloudy() {
        return getNext(Icon.PARTLY_CLOUDY_DAY, Icon.PARTLY_CLOUDY_NIGHT);
    }

    public DataPoint nextClear() {
        return getNext(Icon.CLEAR_DAY, Icon.CLEAR_NIGHT);
    }

    public DataPoint nextSnow() {
        return getNext(Icon.SNOW);
    }

    public DataPoint nextSleet() {
        return getNext(Icon.SLEET);
    }

    public DataPoint nextWind() {
        return getNext(Icon.WIND);
    }

    public DataPoint nextFog() {
        return getNext(Icon.FOG);
    }

    public DataPoint nextHail() {
        return getNext(Icon.HAIL);
    }

    public DataPoint nextThunderStorm() {
        return getNext(Icon.THUNDERSTORM);
    }

    public DataPoint nextTornado() {
        return getNext(Icon.TORNADO);
    }

    private DataPoint getNext(String icon) {
        if(currentlyForecastIcon.equals(icon)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(icon) && !dpM.getIcon().equals("") && dpM.getTime() > currentTime) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(icon) && !dpH.getIcon().equals("") && dpH.getTime() > currentTime) {
                        return dpH;
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(icon)  && !dpM.getIcon().equals("") && dpM.getTime() > currentTime) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(icon)  && !dpH.getIcon().equals("") && dpH.getTime() > currentTime) {
                        return dpH;
                    }
                }
            }
        }

        return null;
    }
    private DataPoint getNext(String icon1, String icon2) {
        if(currentlyForecastIcon.equals(icon1) || currentlyForecastIcon.equals(icon2)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(icon1) && !dpM.getIcon().equals(icon2)
                            && !dpM.getIcon().equals("") && dpM.getTime() > currentTime) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(icon1) && !dpH.getIcon().equals(icon2)
                            && !dpH.getIcon().equals("") && dpH.getTime() > currentTime) {
                        return dpH;
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(icon1) || dpM.getIcon().equals(icon2)
                            && !dpM.getIcon().equals("") && dpM.getTime() > currentTime) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(icon1) || dpH.getIcon().equals(icon2)
                            && !dpH.getIcon().equals("") && dpH.getTime() > currentTime) {
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

        if(nextCloudy() != null) {
            nexCloudyTime = nextCloudy().getTime();
        }
        if(nextPartlyCloudy() != null) {
            nextPartlyCloudyTime = nextPartlyCloudy().getTime();
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
}
