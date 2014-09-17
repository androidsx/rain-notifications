package com.example.robolectric;

import android.app.Activity;
import android.os.Bundle;

import com.androidsx.rain_notifications.R;

public class DeckardActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.deckard);
  }
}
