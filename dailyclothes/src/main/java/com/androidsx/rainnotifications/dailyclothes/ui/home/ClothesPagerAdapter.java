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

    public void updateClothesList(List<Clothes> clothesList) {
        this.clothesList = clothesList;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return ClothesFragment.newInstance(clothesList.get(position));
    }

    @Override
    public int getCount() {
        return clothesList != null ? clothesList.size() : 0;
    }

    public static class ClothesFragment extends Fragment {

        private static final String ARG_CLOTHES = "ImageFragment:clothes";
        private Clothes clothes;

        public static ClothesFragment newInstance(Clothes clothes) {
            ClothesFragment fragment = new ClothesFragment();
            Bundle args = new Bundle();
            args.putParcelable(ARG_CLOTHES, clothes);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            clothes = getArguments().getParcelable(ARG_CLOTHES);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_clothes, container, false);
            clothes.loadOnImageView(getActivity(), (ImageView) rootView.findViewById(R.id.image_view));
            return rootView;
        }
    }
}