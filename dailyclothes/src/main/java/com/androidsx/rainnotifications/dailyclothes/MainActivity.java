package com.androidsx.rainnotifications.dailyclothes;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidsx.rainnotifications.dailyclothes.model.Clothes;
import com.androidsx.rainnotifications.dailyclothes.quickreturn.QuickReturnListView;
import com.androidsx.rainnotifications.dailyclothes.widget.CustomFontTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private Random random = new Random();
    private List<Clothes> clothesList = new ArrayList<Clothes>();
    private CustomListAdapter adapter;

    private QuickReturnListView mListView;
    private TextView mQuickReturnView;
    private View mPlaceHolder;

    private int mCachedVerticalScrollRange;
    private int mQuickReturnHeight;

    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;
    private static final int STATE_EXPANDED = 3;
    private int mState = STATE_ONSCREEN;
    private int mScrollY;
    private int mMinRawY = 0;
    private int rawY;
    private boolean noAnimation = false;

    private TranslateAnimation anim;

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

        fillForecastView(8, 24);
        fillClothesListView();
    }

    private void fillForecastView(int startHour, int endHour) {
        ViewGroup forecastView = (ViewGroup)findViewById(R.id.hourly_forecast);
        int maxTemp = 0;
        for(int i=startHour; i < endHour; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.hourly_forecast_item, null);
            ImageView icon = (ImageView) view.findViewById(R.id.forecast_icon);
            TextView temp = (TextView) view.findViewById(R.id.forecast_temp);
            TextView hour = (TextView) view.findViewById(R.id.forecast_hour);
            icon.setImageDrawable(getResources().getDrawable(getRandomWeatherIcon()));
            int auxTemp = getRandomBetweenNumbers(60, 67);
            temp.setText(auxTemp + "º");
            hour.setText(i+"am");
            forecastView.addView(view);
            if(auxTemp > maxTemp) {
                maxTemp = auxTemp;
            }
        }
        TextView forecastMessage = (TextView) findViewById(R.id.forecast_message);
        forecastMessage.setText(Html.fromHtml(String.format(getString(R.string.forecast_message), maxTemp)));
    }

    private void fillClothesListView() {
        adapter = new CustomListAdapter(this, clothesList);
        mListView.setAdapter(adapter);
        mListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.header, null));

        mListView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mQuickReturnHeight = mQuickReturnView.getHeight();
                        mListView.computeScrollY();
                        mCachedVerticalScrollRange = mListView.getListHeight();
                    }
                });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                mScrollY = 0;
                int translationY = 0;

                if (mListView.scrollYIsComputed()) {
                    mScrollY = mListView.getComputedScrollY();
                }

                rawY = mPlaceHolder.getTop()
                        - Math.min(
                        mCachedVerticalScrollRange
                                - mListView.getHeight(), mScrollY);

                switch (mState) {
                    case STATE_OFFSCREEN:
                        if (rawY <= mMinRawY) {
                            mMinRawY = rawY;
                        } else {
                            mState = STATE_RETURNING;
                        }
                        translationY = rawY;
                        break;

                    case STATE_ONSCREEN:
                        if (rawY < -mQuickReturnHeight) {
                            System.out.println("test3");
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                        translationY = rawY;
                        break;

                    case STATE_RETURNING:

                        if (translationY > 0) {
                            translationY = 0;
                            mMinRawY = rawY - mQuickReturnHeight;
                        }

                        else if (rawY > 0) {
                            mState = STATE_ONSCREEN;
                            translationY = rawY;
                        }

                        else if (translationY < -mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;

                        } else if (mQuickReturnView.getTranslationY() != 0
                                && !noAnimation) {
                            noAnimation = true;
                            anim = new TranslateAnimation(0, 0,
                                    -mQuickReturnHeight, 0);
                            anim.setFillAfter(true);
                            anim.setDuration(250);
                            mQuickReturnView.startAnimation(anim);
                            anim.setAnimationListener(new Animation.AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    noAnimation = false;
                                    mMinRawY = rawY;
                                    mState = STATE_EXPANDED;
                                }
                            });
                        }
                        break;

                    case STATE_EXPANDED:
                        if (rawY < mMinRawY - 2 && !noAnimation) {
                            noAnimation = true;
                            anim = new TranslateAnimation(0, 0, 0,
                                    -mQuickReturnHeight);
                            anim.setFillAfter(true);
                            anim.setDuration(250);
                            anim.setAnimationListener(new Animation.AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    noAnimation = false;
                                    mState = STATE_OFFSCREEN;
                                }
                            });
                            mQuickReturnView.startAnimation(anim);
                        } else if (translationY > 0) {
                            translationY = 0;
                            mMinRawY = rawY - mQuickReturnHeight;
                        }

                        else if (rawY > 0) {
                            mState = STATE_ONSCREEN;
                            translationY = rawY;
                        }

                        else if (translationY < -mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        } else {
                            mMinRawY = rawY;
                        }
                }
                /** this can be used if the build is below honeycomb **/
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                    anim = new TranslateAnimation(0, 0, translationY,
                            translationY);
                    anim.setFillAfter(true);
                    anim.setDuration(0);
                    mQuickReturnView.startAnimation(anim);
                } else {
                    mQuickReturnView.setTranslationY(translationY);
                }

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        clothesList.add(new Clothes(
                R.drawable.model));
        clothesList.add(new Clothes(
                R.drawable.a));
        clothesList.add(new Clothes(
                R.drawable.b));
        clothesList.add(new Clothes(
                R.drawable.c));
        adapter.notifyDataSetChanged();
    }

    private int getRandomBetweenNumbers(int minValue, int maxValue) {
        return random.nextInt((maxValue + 1) - minValue) + minValue;
    }

    private int getRandomWeatherIcon() {
        final TypedArray mascotTypedArray = getResources().obtainTypedArray(R.array.weatherIcons);
        final int mascotIndex = random.nextInt(mascotTypedArray.length());
        return mascotTypedArray.getResourceId(mascotIndex, -1);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 15;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
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

            if (inflater == null)
                inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.clothes_list_item, null);

            ImageView photo = (ImageView)convertView.findViewById(R.id.photo);

            Clothes c = clothesItems.get(position);
            photo.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), c.getPhoto())));

            return convertView;
        }
    }
}
