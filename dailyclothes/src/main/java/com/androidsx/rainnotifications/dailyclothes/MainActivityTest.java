package com.androidsx.rainnotifications.dailyclothes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidsx.rainnotifications.dailyclothes.model.Clothes;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivityTest extends FragmentActivity {

    private SlidingUpPanelLayout bottomSheet;
    private ViewPager imagesPager;
    private MainImagePagerAdapter adapter;
    private List<Clothes> clothesList = new ArrayList<Clothes>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_test);

        imagesPager = (ViewPager) findViewById(R.id.view_pager);
        bottomSheet = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        bottomSheet.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                Log.d("TMP", "onPanelSlide: " + v);
            }

            @Override
            public void onPanelCollapsed(View view) {
                Log.d("TMP", "onPanelCollapsed");
            }

            @Override
            public void onPanelExpanded(View view) {
                Log.d("TMP", "onPanelExpanded");
            }

            @Override
            public void onPanelAnchored(View view) {
                Log.d("TMP", "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View view) {
                Log.d("TMP", "onPanelHidden");
            }
        });

        fillClothesViewPager();
    }

    private void fillClothesViewPager() {
        clothesList.add(new Clothes(R.drawable.lucky_1));
        clothesList.add(new Clothes(R.drawable.lucky_2));
        clothesList.add(new Clothes(R.drawable.lucky_3));
        clothesList.add(new Clothes(R.drawable.lucky_4));
        clothesList.add(new Clothes(R.drawable.lucky_5));
        clothesList.add(new Clothes(R.drawable.ann_taylor_1));
        clothesList.add(new Clothes(R.drawable.ann_taylor_2));
        clothesList.add(new Clothes(R.drawable.ann_taylor_3));
        clothesList.add(new Clothes(R.drawable.ann_taylor_4));
        clothesList.add(new Clothes(R.drawable.ann_taylor_5));
        clothesList.add(new Clothes(R.drawable.blogger_1));
        clothesList.add(new Clothes(R.drawable.blogger_2));
        clothesList.add(new Clothes(R.drawable.blogger_3));
        clothesList.add(new Clothes(R.drawable.blogger_4));
        clothesList.add(new Clothes(R.drawable.blogger_5));
        clothesList.add(new Clothes(R.drawable.blogger_6));
        clothesList.add(new Clothes(R.drawable.blogger_7));
        clothesList.add(new Clothes(R.drawable.blogger_8));
        clothesList.add(new Clothes(R.drawable.blogger_9));
        clothesList.add(new Clothes(R.drawable.blogger_10));

        adapter = new MainImagePagerAdapter(getSupportFragmentManager(), clothesList);
        imagesPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class MainImagePagerAdapter extends FragmentStatePagerAdapter {

        private List<Clothes> clothesList;

        public MainImagePagerAdapter(FragmentManager fm, List<Clothes> clothesList) {
            super(fm);
            this.clothesList = clothesList;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.newInstance(clothesList.get(position).getPhoto());
        }

        @Override
        public int getCount() {
            return clothesList.size();
        }
    }

    public static class ImageFragment extends Fragment {

        private static final String ARG_IMAGE_RESOURCE = "ImageFragment:imageResource";
        private int imageResource;

        public static ImageFragment newInstance(int imageResource) {
            ImageFragment fragment = new ImageFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_IMAGE_RESOURCE, imageResource);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            imageResource = getArguments().getInt(ARG_IMAGE_RESOURCE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_image, container, false);

            ((ImageView) rootView.findViewById(R.id.image_view)).setImageResource(imageResource); //TODO: Hacer con Picasso

            return rootView;
        }
    }


}
