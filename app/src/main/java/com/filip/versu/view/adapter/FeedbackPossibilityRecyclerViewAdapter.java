package com.filip.versu.view.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.viewmodel.callback.IPostsFeedViewModel;
import com.filip.versu.view.viewmodel.callback.IPostsTimelineFeedViewModel;
import com.like.LikeButton;

import java.util.List;

public class FeedbackPossibilityRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<PostFeedbackPossibilityDTO> {

    private IPostsFeedViewModel viewModel;
    private PostDTO post;

    private IUserSession userSession = UserSession.instance();

    /**
     * Says if the adapter is displayed at create post or at feed.
     */
    private boolean isInCreator;

    private View.OnClickListener parentRecyclerViewHolder;

    public FeedbackPossibilityRecyclerViewAdapter(List<PostFeedbackPossibilityDTO> recyclerViewItems, PostDTO post,
                                                  Context context, IPostsFeedViewModel viewModel,
                                                  boolean isInCreator, View.OnClickListener parentViewHolderClickListener) {
        super(recyclerViewItems, context);
        this.viewModel = viewModel;
        this.post = post;
        this.isInCreator = isInCreator;
        this.parentRecyclerViewHolder = parentViewHolderClickListener;
    }

    @Override
    public AbsBindViewHolder<PostFeedbackPossibilityDTO> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (isInCreator) {
            view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_feedback_possibility_creator, parent, false);
            return new FeedbackPossibilityCreatorViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_feedback_possibility, parent, false);
            return new FeedbackPossibilityViewHolder(view);
        }

    }

    class FeedbackPossibilityViewHolder extends AbsBindViewHolder<PostFeedbackPossibilityDTO> {

        private View possibilityItemLayout;

        private TextView votesCount;
        private TextView possibilityName;
        private LikeButton chooseButton;

        public FeedbackPossibilityViewHolder(View itemView) {
            super(itemView);
            possibilityItemLayout = itemView.findViewById(R.id.possibilityItemLayout);
            possibilityName = (TextView) itemView.findViewById(R.id.possibility_name);
            votesCount = (TextView) itemView.findViewById(R.id.thumbs_up_count);
            chooseButton = (LikeButton) itemView.findViewById(R.id.choose_button);
        }

        @Override
        public void bindView(final PostFeedbackPossibilityDTO entity, int pos) {
            possibilityName.setText(entity.name);
            possibilityName.setTextColor(ContextCompat.getColor(context, android.R.color.white));

            votesCount.setText(Integer.toString(entity.count));

            votesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewModel.requestPostFeedbackOfPossibility(entity);
                }
            });

            /**
             * delegating the on click event to view holder
             */
            if (parentRecyclerViewHolder != null) {
                possibilityItemLayout.setOnClickListener(parentRecyclerViewHolder);
            }

            boolean isMyPost = post.owner.getId().equals(userSession.getLogedInUser().getId());

            if (isMyPost) {//I am watching at my post
                votesCount.setVisibility(View.VISIBLE);

                chooseButton.setEnabled(post.showsPossibilities);
                chooseButton.setVisibility(View.VISIBLE);

                if (post.isChosen() && entity.equals(post.chosenFeedbackPossibility)) {
                    chooseButton.setLiked(true);
                }

                chooseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewModel.displayUpdateChosenStateDialog(entity);
                    }
                });

            } else {//I am watching at a post of another user

                if (post.myPostFeedback != null) {
                    votesCount.setVisibility(View.VISIBLE);
                } else {
                    votesCount.setVisibility(View.INVISIBLE);
                }

                if (post.myPostFeedback != null && post.myPostFeedback.feedbackPossibilityDTO.equals(entity)) {
                    possibilityName.setTextColor(ContextCompat.getColor(context, R.color.clickable_content));
                } else {
                    possibilityName.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                }

                chooseButton.setEnabled(false);
                chooseButton.setVisibility(View.INVISIBLE);

                if (post.isChosen() && entity.equals(post.chosenFeedbackPossibility)) {
                    chooseButton.setVisibility(View.VISIBLE);
                    chooseButton.setLiked(true);
                } else {
                    possibilityName.setEnabled(post.showsPossibilities);

                    possibilityName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            IPostsTimelineFeedViewModel shoppingFeedViewModel = (IPostsTimelineFeedViewModel) viewModel;
                            shoppingFeedViewModel.votingForFeedback(entity);
                        }
                    });
                }
            }
        }

    }


    class FeedbackPossibilityCreatorViewHolder extends AbsBindViewHolder<PostFeedbackPossibilityDTO> {

        private EditText possibilityNameEditText;


        public FeedbackPossibilityCreatorViewHolder(View itemView) {
            super(itemView);
            possibilityNameEditText = (EditText) itemView.findViewById(R.id.possibility_name_edit_text);
        }

        @Override
        public void bindView(final PostFeedbackPossibilityDTO entity, final int pos) {

            String possibility = entity.name;

            if (!possibility.equals("")) {
                possibilityNameEditText.setText(possibility);
            }

            if (pos == 0) {//requesting focus only at first edit text
                possibilityNameEditText.requestFocus();
            }

            if (pos == 1) {
                possibilityNameEditText.setHint(R.string.possibilityB);
            }

            possibilityNameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    entity.name = editable.toString();
                }
            });
        }
    }

}
