package com.androidsx.rainnotifications.alert.util;

import android.content.res.Resources;
import android.content.res.TypedArray;

import com.androidsx.rainnotifications.alert.R;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ResourcesHelper {

    public static String resourceToToRandomAlertMessage(Resources resources, int arrayResource, Random random) {
        return pickRandom(Arrays.asList(resources.getStringArray(arrayResource)), random);
    }

    public static String resourceToToRandomAlertMessage(Resources resources, int arrayResource, Random random, Period periodFromNow) {
        final Locale locale = Locale.getDefault(); // TODO: use the real one
        return String.format(resourceToToRandomAlertMessage(resources, arrayResource, random), periodToString(
                periodFromNow,
                resources.getString(R.string.unit_hours),
                resources.getString(R.string.unit_minutes),
                locale));
    }

    public static int generateMascot(Weather weather, Resources resources, Random random) {
        final Map<WeatherType, Integer> owlieVariations = new HashMap<WeatherType, Integer>() {
            {
                //TODO: uncomment when merge
                put(WeatherType.RAIN, R.array.rainy);
                //put(WeatherType.RAIN_NIGHT, R.array.rainy_night);
                put(WeatherType.SUNNY, R.array.sunny);
                //put(WeatherType.CLEAR_NIGHT, R.array.clear_night);
                //put(WeatherType.CLOUDY, R.array.cloudy);
                //put(WeatherType.CLOUDY_NIGHT, R.array.cloudy_night);
                //put(WeatherType.PARTLY_CLOUDY, R.array.partlycloudy);
                //put(WeatherType.PARTLY_CLOUDY_NIGHT, R.array.partlycloudy_night);
                put(WeatherType.UNKNOWN, R.array.default_weather);
            }
        };

        final int mascotArray = owlieVariations.get(weather.getType());
        final TypedArray mascotTypedArray = resources.obtainTypedArray(mascotArray);
        final int mascotIndex = random.nextInt(mascotTypedArray.length());
        return mascotTypedArray.getResourceId(mascotIndex, -1);
    }

    /** Visibility raised from private for testing purposes. */
    static String periodToString(Period period, String hours, String minutes, Locale locale) {
        final PeriodFormatter durationFormatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSeparatorIfFieldsBefore(" " + hours + " and ")
                .appendMinutes()
                .appendSeparatorIfFieldsBefore(" " + minutes)
                .toFormatter()
                .withLocale(locale);

        return durationFormatter.print(period);
    }

    private static <T> T pickRandom(List<T> list, Random random) {
        return new ArrayList<T>(list).get(random.nextInt(list.size()));
    }
}
