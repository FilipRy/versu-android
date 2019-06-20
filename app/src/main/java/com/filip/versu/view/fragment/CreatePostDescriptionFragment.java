package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.view.viewmodel.CreatePostDescriptionViewModel;
import com.filip.versu.view.viewmodel.callback.ICreatePostDescriptionViewModel.ICreatePostDescriptionViewModelCallback;

import eu.inloop.viewmodel.base.ViewModelBaseFragment;


/**
 * This is a fragment for creating description of a post
 */
public class CreatePostDescriptionFragment
        extends ViewModelBaseFragment<ICreatePostDescriptionViewModelCallback, CreatePostDescriptionViewModel>
        implements ICreatePostDescriptionViewModelCallback {

    public static final String TAG = CreatePostDescriptionFragment.class.getSimpleName();

    public static final String POST_KEY = "POST_KEY";

    private PostDTO postDTO;

    public static CreatePostDescriptionFragment instance(PostDTO postDTO) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(POST_KEY, postDTO);
        CreatePostDescriptionFragment fragment = new CreatePostDescriptionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post_description, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postDTO = (PostDTO) getArguments().get(POST_KEY);

        setModelView(this);
        getViewModel().setDependencies(postDTO);

        EditText postDescriptionEditText = (EditText) view.findViewById(R.id.postDescEditText);
        postDescriptionEditText.setText(postDTO.description);

        postDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getViewModel().setPostDescription(editable.toString());
            }
        });
    }

    @Nullable
    @Override
    public Class<CreatePostDescriptionViewModel> getViewModelClass() {
        return CreatePostDescriptionViewModel.class;
    }
}
