package com.filip.versu.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.helper.Helper;
import com.filip.versu.service.helper.PhotoSize;
import com.filip.versu.service.impl.PostService;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.custom.PostVSDivider;
import com.filip.versu.view.viewmodel.callback.IPostsFeedViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public abstract class AbsPostRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<PostDTO> {


    public enum POST_ITEM_TYPE {

        SIMPLE_MINE(0), SIMPLE_OTHER(1), DOUBLE_MINE(2), DOUBLE_OTHER(3);

        private final int value;

        private POST_ITEM_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    protected Context context;

    protected IPostsFeedViewModel iPostsFeedViewModel;

    private IUserSession userSession = UserSession.instance();

    public AbsPostRecyclerViewAdapter(List<PostDTO> recyclerViewItems, Context context) {
        super(recyclerViewItems, context);
        this.context = context;
    }

    public AbsPostRecyclerViewAdapter(List<PostDTO> recyclerViewItems, Context context, IPostsFeedViewModel iPostsFeedViewModel) {
        this(recyclerViewItems, context);
        this.iPostsFeedViewModel = iPostsFeedViewModel;
    }

    @Override
    public int getItemViewType(int position) {
        PostDTO postDTO = getItem(position);

        if (postDTO.owner.getId().equals(userSession.getLogedInUser().getId())) {
            if (postDTO.photos.size() > 1) {
                return POST_ITEM_TYPE.DOUBLE_MINE.getValue();
            }
            return POST_ITEM_TYPE.SIMPLE_MINE.getValue();
        } else {
            if (postDTO.photos.size() > 1) {
                return POST_ITEM_TYPE.DOUBLE_OTHER.getValue();
            }
            return POST_ITEM_TYPE.SIMPLE_OTHER.getValue();
        }
    }


    /**
     * This is a general view holder for ALL shopping items.
     * It shows only the description of the shopping item
     */
    abstract class AbsGeneralPostRecyclerViewHolder extends AbsBindViewHolder<PostDTO> implements PopupMenu.OnMenuItemClickListener {

        private TextView shopperNameView;
        private TextView itemDescView;

        private TextView timeInfoView;
        private TextView locationInfoView;
        private CircleImageView shopperProfilePhotoView;

        private TextView possibilitiesTextView;

        private ImageView imageViewComment;
        private ImageView accessTypeView;
        protected ImageView contextMenuBtn;

        private RecyclerView shoppingItemCommentsView;
        private SmallCommentRecyclerViewAdapter commentRecyclerViewAdapter;

        protected PostDTO postDTO;
        protected int pos;

        AbsGeneralPostRecyclerViewHolder(View itemView) {
            super(itemView);
            shopperNameView = (TextView) itemView.findViewById(R.id.shopperNameText);
            itemDescView = (TextView) itemView.findViewById(R.id.commentContentView);
            accessTypeView = (ImageView) itemView.findViewById(R.id.accessTypeView);
            contextMenuBtn = (ImageView) itemView.findViewById(R.id.contextMenuBtn);


            shopperProfilePhotoView = (CircleImageView) itemView.findViewById(R.id.shopperImageView);
            timeInfoView = (TextView) itemView.findViewById(R.id.shoppingItemAge);
            locationInfoView = (TextView) itemView.findViewById(R.id.shoppingItemLocation);

            imageViewComment = (ImageView) itemView.findViewById(R.id.comment);

            shoppingItemCommentsView = (RecyclerView) itemView.findViewById(R.id.recyclerViewComments);

            possibilitiesTextView = (TextView) itemView.findViewById(R.id.possibilitiesTextView);

        }

        @Override
        public void bindView(final PostDTO entity, int pos) {
            this.postDTO = entity;

            this.pos = pos;

            shopperNameView.setText(postDTO.owner.username);

            if (entity.accessType == PostDTO.AccessType.PUBLICC) {
                accessTypeView.setImageResource(R.drawable.ic_public);
            } else {
                accessTypeView.setImageResource(R.drawable.ic_people);
            }

            if (entity.description != null) {
                itemDescView.setText(entity.description);
            } else {
                itemDescView.setText("");
            }

            if (entity.postFeedbackPossibilities != null) {
                String possibilityA = entity.postFeedbackPossibilities.get(0).name;

                String result = "#" + possibilityA;

                String possibilityB = entity.postFeedbackPossibilities.get(1).name;

                result = result + PostService.POSSIBILITIES_SEPARATOR + possibilityB;

                Spannable formattedWord = new SpannableString(result);

                int formatEnd = 1;
                formattedWord.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.hashtag_vs)), 0, formatEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                formattedWord.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.clickable_content)), formatEnd, formatEnd + possibilityA.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                formatEnd = formatEnd + possibilityA.length();

                formattedWord.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.hashtag_vs)), formatEnd, formatEnd + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                formatEnd = formatEnd + 2;

                formattedWord.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.clickable_content)), formatEnd, formattedWord.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


                possibilitiesTextView.setText(formattedWord);
            }


            imageViewComment.setImageResource(R.drawable.comment);//TODO this should be set in xml via app:srcCompat, but does not work

            commentRecyclerViewAdapter = new SmallCommentRecyclerViewAdapter(postDTO.comments, context, iPostsFeedViewModel);
            shoppingItemCommentsView.setAdapter(commentRecyclerViewAdapter);
            shoppingItemCommentsView.setHasFixedSize(true);
            shoppingItemCommentsView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            String postAge = PostService.getFormattedAge(context, postDTO.publishTime);
            timeInfoView.setText(postAge);

            if (postDTO.location != null) {
                locationInfoView.setText(postDTO.location.name);
                locationInfoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iPostsFeedViewModel.requestDisplayLocationProfile(postDTO.location);
                    }
                });
            } else {
                locationInfoView.setText("");
            }

            Picasso.with(context).load(postDTO.owner.profilePhotoURL).placeholder(R.mipmap.ic_account_circle_white).into(shopperProfilePhotoView);

            shopperNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPostsFeedViewModel.requestDisplayProfileOfUser(postDTO.owner);
                }
            });

            imageViewComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPostsFeedViewModel.requestCommentsOfPost(postDTO);
                }
            });

            shoppingItemCommentsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPostsFeedViewModel.requestCommentsOfPost(postDTO);
                }
            });

            possibilitiesTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] possibilities = new String[entity.postFeedbackPossibilities.size()];

                    int i = 0;
                    for (PostFeedbackPossibilityDTO possibilityDTO: entity.postFeedbackPossibilities) {
                        possibilities[i++] = possibilityDTO.name;
                    }
                    iPostsFeedViewModel.requestPostsByFeedbackPossibilities(possibilities);
                }
            });

        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.delete) {
                iPostsFeedViewModel.executeDeleteItemTask(postDTO, pos, getContent());
            } else if (item.getItemId() == R.id.report) {
                iPostsFeedViewModel.reportPost(postDTO);
            } else if(item.getItemId() == R.id.share) {
                iPostsFeedViewModel.sharePostWithLink(postDTO);
            }
            return true;
        }

    }

    /**
     * This is a general view holder for all (simple) shopping items.
     * The user can read likes, dislikes and create + read comments here.
     */
    abstract class AbsSimplePostRecyclerViewHolder extends AbsGeneralPostRecyclerViewHolder implements View.OnClickListener {

        protected ImageView photoView;
        protected RecyclerView feedbackPossibilityView;
        protected View feedbackPossibilityLayout;
        protected FeedbackPossibilityRecyclerViewAdapter feedbackPossibilityRecyclerViewAdapter;


        AbsSimplePostRecyclerViewHolder(View itemView) {
            super(itemView);
            photoView = (ImageView) itemView.findViewById(R.id.shoppingItemPhoto);
            feedbackPossibilityView = (RecyclerView) itemView.findViewById(R.id.voting_poss_view);
            feedbackPossibilityLayout = itemView.findViewById(R.id.possibilityLayout);
        }

        @Override
        public void bindView(final PostDTO entity, int pos) {
            super.bindView(entity, pos);

            this.postDTO = entity;

            //creating adapter
            feedbackPossibilityRecyclerViewAdapter = new FeedbackPossibilityRecyclerViewAdapter(postDTO.postFeedbackPossibilities, entity, context, iPostsFeedViewModel, false, this);
            feedbackPossibilityView.setAdapter(feedbackPossibilityRecyclerViewAdapter);
            feedbackPossibilityView.setHasFixedSize(true);
            feedbackPossibilityView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            feedbackPossibilityView.addItemDecoration(new PostVSDivider(context));

            if (entity.showsPossibilities) {
                feedbackPossibilityLayout.setAlpha(Helper.POSSIBILITIES_OVERLAY_DISPLAYED_ALPHA);
            } else {
                feedbackPossibilityLayout.setAlpha(Helper.POSSIBLITIES_OVERLAY_HIDDEN_ALPHA);
            }

            int photoWidth = PhotoSize.getSingleItemPhotoWidth();
            int photoHeight = PhotoSize.getSingleItemPhotoHeight();

            Picasso.with(context).load(entity.photos.get(0).path).resize(photoWidth, photoHeight).centerInside().into(photoView);

            feedbackPossibilityLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            invertPossVisibility(postDTO);
        }

        private void invertPossVisibility(PostDTO entity) {
            entity.showsPossibilities = !entity.showsPossibilities;
            AbsPostRecyclerViewAdapter.this.notifyDataSetChanged();
        }

    }

    /**
     * This is a concrete view holder for shopping items shown in my newsfeed
     */
    class SimplePostFeedRecyclerViewHolder extends AbsSimplePostRecyclerViewHolder {

        public static final long DOUBLE_CLICK_THRESHOLD_MS = 500;

        SimplePostFeedRecyclerViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(final PostDTO entity, int pos) {
            super.bindView(entity, pos);

            contextMenuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    popupMenu.inflate(R.menu.post_context_menu);
                    popupMenu.setOnMenuItemClickListener(SimplePostFeedRecyclerViewHolder.this);
                    popupMenu.show();
                }
            });

        }
    }

    /**
     * This is a concrete view holder for shopping items shown in my profile.
     */
    class SimplePostMyProfileRecyclerViewHolder extends AbsSimplePostRecyclerViewHolder {

        SimplePostMyProfileRecyclerViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(final PostDTO entity, final int pos) {
            super.bindView(entity, pos);

            contextMenuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.inflate(R.menu.my_post_context_menu);
                    popupMenu.setOnMenuItemClickListener(SimplePostMyProfileRecyclerViewHolder.this);
                    popupMenu.show();
                }
            });

        }
    }

    /**
     * This is a ViewHolder for VotingShoppingItem.
     */
    class DoublePostRecyclerViewHolder extends AbsGeneralPostRecyclerViewHolder implements View.OnClickListener {

        private RecyclerView photosGridView;
        private DoublePostRecyclerViewAdapter doublePostRecyclerViewAdapter;

        /**
         * if this view holder is in feed and not in my profile.
         */
        private boolean isInFeed;

        private int position;

        DoublePostRecyclerViewHolder(View itemView, boolean isInFeed) {
            super(itemView);
            photosGridView = (RecyclerView) itemView.findViewById(R.id.recyclerViewPhotos);
            this.isInFeed = isInFeed;
        }

        @Override
        public void bindView(final PostDTO entity, final int pos) {
            super.bindView(entity, pos);
            this.position = pos;

            contextMenuBtn.setOnClickListener(this);

            doublePostRecyclerViewAdapter = new DoublePostRecyclerViewAdapter(entity.photos, context, isInFeed, iPostsFeedViewModel);
            photosGridView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            photosGridView.setHasFixedSize(true);
            photosGridView.addItemDecoration(new PostVSDivider(context));
            photosGridView.setAdapter(doublePostRecyclerViewAdapter);
        }

        @Override
        public void onClick(View view) {
            if(view == contextMenuBtn) {
                int menuResId = R.menu.my_post_context_menu;
                if(isInFeed) {
                    menuResId = R.menu.post_context_menu;
                }

                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(menuResId);
                popupMenu.setOnMenuItemClickListener(DoublePostRecyclerViewHolder.this);
                popupMenu.show();
            }
        }
    }

}
