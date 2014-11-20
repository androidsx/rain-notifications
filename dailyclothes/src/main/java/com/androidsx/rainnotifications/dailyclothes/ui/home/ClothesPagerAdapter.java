package com.androidsx.rainnotifications.dailyclothes.ui.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidsx.rainnotifications.dailyclothes.R;
import com.androidsx.rainnotifications.dailyclothes.model.Clothes;

import java.util.List;

public class ClothesPagerAdapter extends FragmentStatePagerAdapter {

    private List<Clothes> clothesList;

    public ClothesPagerAdapter(FragmentManager fm, List<Clothes> clothesList) {
        super(fm);
        this.clothesList = clothesList;
    }

    @Override
    public Fragment getItem(int position) {
        return ClothesFragment.newInstance(clothesList.get(position).getPhoto());
    }

    @Override
    public int getCount() {
        return clothesList.size();
    }

    public static class ClothesFragment extends Fragment {

        private static final String ARG_IMAGE_RESOURCE = "ImageFragment:imageResource";
        private int imageResource;

        public static ClothesFragment newInstance(int imageResource) {
            ClothesFragment fragment = new ClothesFragment();
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
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_clothes, container, false);

            ((ImageView) rootView.findViewById(R.id.image_view)).setImageResource(imageResource); //TODO: Hacer con Picasso

            return rootView;
        }
    }
}