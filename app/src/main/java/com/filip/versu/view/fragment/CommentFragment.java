package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.filip.versu.R;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.CommentsFeedViewModel;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.adapter.CommentRecyclerViewAdapter;
import com.filip.versu.view.viewmodel.CommentViewModel;
import com.filip.versu.view.viewmodel.callback.ICommentViewModel.ICommentViewCallback;

import java.util.ArrayList;


public class CommentFragment extends AbsRefreshablePageableFragment<ICommentViewCallback, CommentsFeedViewModel, CommentViewModel> implements ICommentViewCallback {

    public static final String TAG = "CommentFragment";
    public static final String POST_KEY = "POST_KEY";

    private AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChange;

    private Button loadMoreComments;
    private boolean hideLoadMoreBtn;

    public static CommentFragment newInstance(PostDTO postDTO) {
        CommentFragment commentFragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(POST_KEY, postDTO);
        commentFragment.setArguments(bundle);
        return commentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView commentView = (ImageView) view.findViewById(R.id.imageViewLayout);
        final EditText commentText = (EditText) view.findViewById(R.id.editTextLayout);

        commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = commentText.getText().toString();
                getViewModel().createCommentTask(content);
                commentText.setText("");
            }
        });

        PostDTO postDTO = (PostDTO) getArguments().getSerializable(POST_KEY);

        getViewModel().setDependencies(postDTO);
        getViewModel().setRecyclerViewAdapterCallback(notifyFeedbackActionChange);
        getViewModel().requestItemsFromInternalStorage(getActivity().getApplicationContext());

    }


    @Override
    public void initializeEndlessScrolling(View parentView) {

        loadMoreComments = (Button) parentView.findViewById(R.id.load_more_btn);

        loadMoreComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getViewModel().requestNextPageFromBackend();
            }
        });
    }

    @Override
    public void hideLoadMoreBtn() {
        hideLoadMoreBtn = true;//when showProgressBarAtBtn(false) is called afterwards, bnt wont be shown anymore
        loadMoreComments.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBarAtBottom(boolean show) {
        super.showProgressBarAtBottom(show);
        if(show) {
            loadMoreComments.setVisibility(View.GONE);
        } else {
            if (!hideLoadMoreBtn) {
                loadMoreComments.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public AbsBaseEntityRecyclerViewAdapter<CommentDTO> createRecyclerViewAdapter() {
        CommentRecyclerViewAdapter commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(new ArrayList<CommentDTO>(), getActivity().getApplicationContext(), getViewModel());
        return commentRecyclerViewAdapter;
    }

    @Override
    public void setModelView() {
        setModelView(this);
    }

    @Override
    public void setRecyclerViewAdapterCallback(AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChangeCallback) {
        this.notifyFeedbackActionChange = notifyFeedbackActionChangeCallback;
    }

    @Override
    public void scrollToLastItem() {
        int size = super.recyclerViewAdapter.getItemCount();
        super.recyclerView.scrollToPosition(size - 1);

        //TODO find better sol, this is a hack
        hideLoadMoreBtn = false;

    }

    @Nullable
    @Override
    public Class<CommentViewModel> getViewModelClass() {
        return CommentViewModel.class;
    }

    @Override
    public void displayProfileOfUserFragment(UserDTO userDTO) {
        profileDisplayer.displayUserProfile(userDTO, getActivity());
    }

    @Override
    public void addItemToViewAdapter(CommentDTO item) {
        recyclerViewAdapter.addItem(item);
    }

    @Override
    public void addItemToViewAdapter(CommentDTO item, int position) {
        recyclerViewAdapter.addItemToPosition(item, position);
    }

    @Override
    public void removeItemFromViewAdapter(CommentDTO item) {
        recyclerViewAdapter.removeItem(item);
    }
}
