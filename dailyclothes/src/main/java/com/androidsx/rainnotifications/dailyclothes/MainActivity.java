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
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidsx.rainnotifications.dailyclothes.model.Clothes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity {
    private Random random = new Random();
    private List<Clothes> clothesList = new ArrayList<Clothes>();
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateUI();
    }

    private void updateUI() {
        fillForecastView((ViewGroup)findViewById(R.id.hourly_forecast), 8, 24);
        fillClothesListView((ListView)findViewById(R.id.clothes_list_view));
    }

    private void fillForecastView(ViewGroup forecastView, int startHour, int endHour) {
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
        TextView alertMessage = (TextView) findViewById(R.id.alert_message);
        forecastMessage.setText(Html.fromHtml(String.format(getString(R.string.forecast_message), maxTemp)));
        alertMessage.setText(Html.fromHtml(String.format(getString(R.string.alert_message))));
    }

    private void fillClothesListView(ListView listView) {
        adapter = new CustomListAdapter(this, clothesList);
        listView.setAdapter(adapter);

        clothesList.add(new Clothes(
                "Vogue",
                "Magazine",
                "Moda y belleza; todas las pasarelas internacionales; tendencias, diseñadores, modelos y fotógrafos de moda; joyas, moda en la calle.",
                R.drawable.vogue_logo,
                R.drawable.model));
        clothesList.add(new Clothes(
                "",
                "",
                "",
                R.drawable.vogue_logo,
                R.drawable.a));
        clothesList.add(new Clothes(
                "",
                "",
                "",
                R.drawable.vogue_logo,
                R.drawable.b));
        clothesList.add(new Clothes(
                "",
                "",
                "",
                R.drawable.vogue_logo,
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

            TextView magazine = (TextView)convertView.findViewById(R.id.magazine);
            TextView mSubtitle = (TextView)convertView.findViewById(R.id.magazine_subtitle);
            TextView description = (TextView)convertView.findViewById(R.id.text_description);
            ImageView logo = (ImageView)convertView.findViewById(R.id.magazine_logo);
            ImageView photo = (ImageView)convertView.findViewById(R.id.photo);

            Clothes c = clothesItems.get(position);
            magazine.setText(c.getMagazine());
            mSubtitle.setText(c.getSubtitle());
            description.setText(c.getDescription());
            logo.setImageDrawable(context.getResources().getDrawable(c.getLogo()));
            photo.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), c.getPhoto())));

            return convertView;
        }
    }
}
