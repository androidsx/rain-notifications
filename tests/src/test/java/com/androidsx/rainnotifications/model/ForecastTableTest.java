package com.androidsx.rainnotifications.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;

@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class ForecastTableTest {

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyListIsNotAllowed() {
        new ForecastTable(Collections.<Forecast>emptyList());
    }
}
