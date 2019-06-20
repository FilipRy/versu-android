package com.filip.versu.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.dto.PostPhotoDTO;
import com.filip.versu.service.helper.Helper;
import com.filip.versu.service.helper.PhotoSize;
import com.filip.versu.view.viewmodel.callback.IPostsFeedViewModel;
import com.filip.versu.view.viewmodel.callback.IPostsTimelineFeedViewModel;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * This is an adapter for showing the photos of voting-shopping item in a recycler (grid) view.
 */
class DoublePostRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<PostPhotoDTO> {

    public static final int TOP = 0;
    public static final int BOTTOM = 1;

    private boolean isInFeed;
    private Context context;
    private IPostsFeedViewModel shoppingFeedViewModel;

    public DoublePostRecyclerViewAdapter(List<PostPhotoDTO> recyclerViewItems, Context context, boolean isInFeed, IPostsFeedViewModel iPostsFeedViewModel) {
        super(recyclerViewItems, context);
        this.isInFeed = isInFeed;
        this.context = context;
        this.shoppingFeedViewModel = iPostsFeedViewModel;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TOP;
        }
        return BOTTOM;
    }

    @Override
    public AbsBindViewHolder<PostPhotoDTO> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TOP) {
            view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_voting_post_photo_top, parent, false);
        } else if (viewType == BOTTOM) {
            view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_voting_post_photo_bottom, parent, false);
        }

        if (isInFeed) {
            return new VotingPostFeedRecyclerViewHolder(view);
        } else {
            return new VotingPostMyProfileRecyclerViewHolder(view);
        }
    }

    /**
     * This is a general view holder for a photo of a voting shopping item in feed or my profile.
     * Functionality: read likes
     */
    abstract class AbsVotingPostRecyclerViewHolder extends AbsBindViewHolder<PostPhotoDTO> {

        protected PostPhotoDTO photoDTO;
        protected ImageView photoView;

        protected TextView votesCount;
        protected TextView possibilityName;

        protected View possibilitiesOverlay;

        protected LikeButton chooseButton;

        public AbsVotingPostRecyclerViewHolder(View itemView) {
            super(itemView);
            photoView = (ImageView) itemView.findViewById(R.id.shoppingItemPhoto);

            possibilityName = (TextView) itemView.findViewById(R.id.possibility_name);
            votesCount = (TextView) itemView.findViewById(R.id.thumbs_up_count);
            chooseButton = (LikeButton) itemView.findViewById(R.id.choose_button);

            possibilitiesOverlay = itemView.findViewById(R.id.possibilityOverlay);
        }

        @Override
        public void bindView(final PostPhotoDTO entity, int pos) {
            this.photoDTO = entity;

            int photoWidth = PhotoSize.getSingleItemPhotoWidth();
            int photoHeight = PhotoSize.getSingleItemPhotoHeight();

            final PostFeedbackPossibilityDTO possibilityDTO = entity.post.postFeedbackPossibilities.get(pos);

            possibilityName.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            possibilityName.setText(possibilityDTO.name);

            Picasso.with(context).load(entity.path).resize(photoWidth, photoHeight).centerInside().into(photoView);

            if (entity.post.showsPossibilities) {
                possibilitiesOverlay.setAlpha(Helper.POSSIBILITIES_OVERLAY_DISPLAYED_ALPHA);
            } else {
                possibilitiesOverlay.setAlpha(Helper.POSSIBLITIES_OVERLAY_HIDDEN_ALPHA);
            }
            votesCount.setText(Integer.toString(possibilityDTO.count));

            votesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shoppingFeedViewModel.requestPostFeedbackOfPossibility(possibilityDTO);
                }
            });

            possibilitiesOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    entity.post.showsPossibilities = !entity.post.showsPossibilities;
                    DoublePostRecyclerViewAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * This is a view holder for a photo of a voting shopping item in feed.
     */
    class VotingPostFeedRecyclerViewHolder extends AbsVotingPostRecyclerViewHolder {

        private IPostsTimelineFeedViewModel shoppingNewsfeedViewModel;
        private long lastTimePhotoClick;

        public VotingPostFeedRecyclerViewHolder(View itemView) {
            super(itemView);
            shoppingNewsfeedViewModel = (IPostsTimelineFeedViewModel) shoppingFeedViewModel;
        }

        @Override
        public void bindView(final PostPhotoDTO entity, int pos) {
            super.bindView(entity, pos);

            if (entity.post.myPostFeedback != null) {
                votesCount.setVisibility(View.VISIBLE);
            } else {
                votesCount.setVisibility(View.INVISIBLE);
            }

            final PostFeedbackPossibilityDTO possibilityDTO = entity.post.postFeedbackPossibilities.get(pos);

            if (entity.post.myPostFeedback != null && entity.post.myPostFeedback.feedbackPossibilityDTO.equals(possibilityDTO)) {
                possibilityName.setTextColor(ContextCompat.getColor(context, R.color.clickable_content));
            } else {
                possibilityName.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            }

            chooseButton.setEnabled(false);
            chooseButton.setVisibility(View.GONE);

            if (!entity.post.isChosen()) {//only non-chosen items can be liked
                photoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //detecting double click
                        if (System.currentTimeMillis() - lastTimePhotoClick < AbsPostRecyclerViewAdapter.SimplePostFeedRecyclerViewHolder.DOUBLE_CLICK_THRESHOLD_MS) {
                            shoppingNewsfeedViewModel.votingForFeedback(possibilityDTO);
                        }
                        lastTimePhotoClick = System.currentTimeMillis();
                    }
                });

                possibilityName.setEnabled(entity.post.showsPossibilities);

                possibilityName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shoppingNewsfeedViewModel.votingForFeedback(possibilityDTO);
                    }
                });

            } else {
                chooseButton.setVisibility(View.VISIBLE);

                if (entity.post.isChosen()) {
                    chooseButton.setLiked(possibilityDTO.equals(entity.post.chosenFeedbackPossibility));
                } else {
                    chooseButton.setLiked(false);
                }
            }
        }
    }

    /**
     * This is a view holder for a photo of shopping item in my profile.
     */
    class VotingPostMyProfileRecyclerViewHolder extends AbsVotingPostRecyclerViewHolder {


        public VotingPostMyProfileRecyclerViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(final PostPhotoDTO entity, final int pos) {
            super.bindView(entity, pos);

            chooseButton.setEnabled(entity.post.showsPossibilities);
            chooseButton.setVisibility(View.VISIBLE);

            final PostFeedbackPossibilityDTO possibilityDTO = entity.post.postFeedbackPossibilities.get(pos);

            boolean isChosen = possibilityDTO.equals(entity.post.chosenFeedbackPossibility);

            if (entity.post.isChosen() && isChosen) {
                chooseButton.setLiked(isChosen);
            } else {
                chooseButton.setLiked(false);
            }

            chooseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shoppingFeedViewModel.displayUpdateChosenStateDialog(possibilityDTO);
                }
            });
        }
    }

}
