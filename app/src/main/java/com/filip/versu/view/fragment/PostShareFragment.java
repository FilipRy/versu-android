package com.filip.versu.view.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.view.viewmodel.PostShareViewModel;
import com.filip.versu.view.viewmodel.callback.IPostShareViewModel;

import eu.inloop.viewmodel.base.ViewModelBaseFragment;

public class PostShareFragment extends ViewModelBaseFragment<IPostShareViewModel.IPostShareViewModelCallback, PostShareViewModel> implements IPostShareViewModel.IPostShareViewModelCallback {

    public static final String TAG = PostShareFragment.class.getSimpleName();

    public static final String POST_KEY = "post_key";

    private PostDTO postDTO;

    private TextView shareableLinkTextView;
    private Button generateLinkBtn;
    private Button removeLinkBtn;
    private ProgressBar progressBar;

    public static PostShareFragment newInstance(PostDTO postDTO) {
        PostShareFragment postShareFragment = new PostShareFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(POST_KEY, postDTO);
        postShareFragment.setArguments(bundle);
        return postShareFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postDTO = (PostDTO) getArguments().getSerializable(POST_KEY);

        setModelView(this);


        shareableLinkTextView = (TextView) view.findViewById(R.id.shareableLink);
        generateLinkBtn = (Button) view.findViewById(R.id.generateNewBtn);
        removeLinkBtn = (Button) view.findViewById(R.id.removeLinkBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        if (postDTO.secretUrl == null) {
            shareableLinkTextView.setText(R.string.no_shareable_link);
            removeLinkBtn.setVisibility(View.GONE);
        } else {
            shareableLinkTextView.setText(postDTO.secretUrl);
        }

        generateLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateNewLink();
            }
        });

        removeLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLink();
            }
        });

    }


    private void generateNewLink() {

        if(postDTO.secretUrl == null) {
            getViewModel().generateNewLink(postDTO);
            return;
        }

        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.generate_secret_link_dialog_title);
        builder.setMessage(R.string.generate_secret_link_dialog_text);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getViewModel().generateNewLink(postDTO);
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void removeLink() {
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.remove_secret_link_dialog_title);
        builder.setMessage(R.string.remove_secret_link_dialog_text);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getViewModel().removeLink(postDTO);
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void newLinkGenerated() {
        shareableLinkTextView.setText(postDTO.secretUrl);
        removeLinkBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void linkRemoved() {
        shareableLinkTextView.setText(R.string.no_shareable_link);
        removeLinkBtn.setVisibility(View.GONE);
    }

    @Override
    public void displayProgress(boolean display) {
        if (display) {
            progressBar.setVisibility(View.VISIBLE);
            shareableLinkTextView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            shareableLinkTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayMessage(int messageResId) {
        displayMessage(getString(messageResId));
    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_share, container, false);
    }

    @Nullable
    @Override
    public Class<PostShareViewModel> getViewModelClass() {
        return PostShareViewModel.class;
    }

}
