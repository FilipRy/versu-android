package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.filip.versu.R;


public class TutorialFragment extends Fragment {


    public static final String TAG = TutorialFragment.class.getSimpleName();

    public static final String TEXT_KEY = "TEXT_KEY";
    public static final String CHILD_KEY = "child_key";

    private boolean isChildOfFragment;

    /**
     *
     * @param text
     * @param isChildOfFragment says if this fragment is "under" fragment manager of another fragment (true), or it's "under" activity.
     * @return
     */
    public static TutorialFragment newInstance(String text, boolean isChildOfFragment) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_KEY, text);
        bundle.putBoolean(CHILD_KEY, isChildOfFragment);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_tutorial, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String text = getArguments().getString(TEXT_KEY);
        isChildOfFragment = getArguments().getBoolean(CHILD_KEY);

        TextView hintTextView = (TextView) view.findViewById(R.id.hint_text_view);

        hintTextView.setText(text);

        Button gotitButton = (Button) view.findViewById(R.id.got_it_button);
        gotitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChildOfFragment) {
                    getFragmentManager().popBackStack();
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }
}
