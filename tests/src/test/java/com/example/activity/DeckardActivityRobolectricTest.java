package com.example.activity;

import android.app.Activity;
import com.example.robolectric.DeckardActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

/**
 * Oritinally from https://github.com/robolectric/deckard-gradle. Let's keep this around just in case.
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class DeckardActivityRobolectricTest {

    @Test
    public void testSomething() throws Exception {
        Activity activity = Robolectric.buildActivity(DeckardActivity.class).create().get();
        assertTrue(activity != null);
    }

    @Test
    public void testSomethingElse() throws Exception {
        Activity activity = Robolectric.buildActivity(DeckardActivity.class).create().get();
        assertTrue(activity != null);
    }
}
