package com.filip.versu.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.view.viewmodel.callback.parent.IAppIntroContainerViewModel;
import com.filip.versu.view.viewmodel.parent.AppIntroContainerViewModel;

import eu.inloop.viewmodel.base.ViewModelBaseFragment;

public class IntroFragment extends ViewModelBaseFragment<IAppIntroContainerViewModel.IAppIntroContainerViewModelCallback, AppIntroContainerViewModel> implements IAppIntroContainerViewModel.IAppIntroContainerViewModelCallback {


    public static enum IntroFragmentType {
        ASK, VOTE, DISCOVER
    }

    public static final String TYPE_KEY = "type_key";

    public static IntroFragment newInstance(IntroFragmentType type) {
        IntroFragment introFragment = new IntroFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(TYPE_KEY, type);

        introFragment.setArguments(bundle);

        return introFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView photoImageView = (ImageView) view.findViewById(R.id.onboardingImage);
        TextView titleTextView = (TextView) view.findViewById(R.id.onboardingTitle);
        TextView descTextView = (TextView) view.findViewById(R.id.onboardingDesc);

        IntroFragmentType type = (IntroFragmentType) getArguments().getSerializable(TYPE_KEY);

        if(type == IntroFragmentType.ASK) {
            photoImageView.setImageResource(R.mipmap.onboarding_ask);
            titleTextView.setText(R.string.ask_title);
            titleTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.tab_home_underline));
            descTextView.setText(R.string.ask_text);
        } else if (type == IntroFragmentType.VOTE) {
            photoImageView.setImageResource(R.mipmap.onboarding_vote);
            titleTextView.setText(R.string.vote_title);
            titleTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.tab_camera_underline));
            descTextView.setText(R.string.vote_text);
        } else if (type == IntroFragmentType.DISCOVER) {
            photoImageView.setImageResource(R.mipmap.onboarding_discover);
            titleTextView.setText(R.string.discover_title);
            titleTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.tab_profile_underline));
            descTextView.setText(R.string.discover_text);
        }

    }

    @Nullable
    @Override
    public Class<AppIntroContainerViewModel> getViewModelClass() {
        return AppIntroContainerViewModel.class;
    }
}
