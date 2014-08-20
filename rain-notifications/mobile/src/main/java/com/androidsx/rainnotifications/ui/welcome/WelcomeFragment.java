package com.androidsx.rainnotifications.ui.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidsx.rainnotifications.R;

public class WelcomeFragment extends Fragment {
    private static final String TAG = WelcomeFragment.class.getSimpleName();
    public static final String ARG_PAGE = "page";

    private int mPageNumber;

    public static WelcomeFragment create(int pageNumber) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return inflateWelcomeLayout(inflater, container);
        } catch (java.lang.OutOfMemoryError e) {
            // An OOM here would be extremely bad, since this is executed during our first launch
            Log.e(TAG, "Ran out of memory while inflating the layout");
            getActivity().finish();
            return null;
        }
    }

    private View inflateWelcomeLayout(LayoutInflater inflater, ViewGroup container) {
        final ViewGroup rootView;

        switch (mPageNumber) {
        case 0:
            rootView = (ViewGroup) inflater.inflate(R.layout.welcome_1, container, false);
            break;
        case 1:
            rootView = (ViewGroup) inflater.inflate(R.layout.welcome_2, container, false);
            final TextView welcomeTextStepTwo = (TextView) rootView.findViewById(R.id.welcome_text_step2);
            welcomeTextStepTwo.setText(String.format(getString(R.string.welcome_step_2), getString(R.string.app_name)));
            break;
        case 2:
            rootView = (ViewGroup) inflater.inflate(R.layout.welcome_3, container, false);
            break;
        case 3:
        default:
            rootView = (ViewGroup) inflater.inflate(R.layout.welcome_4, container, false);
            break;
        }

        return rootView;
    }
}
