package com.filip.versu.model.view;

import com.filip.versu.model.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.dto.UserProfileDTO;
import com.filip.versu.model.view.abs.AbsPostViewModel;
import com.filip.versu.service.impl.PostService;


public class UserProfileFeedViewModel extends AbsPostViewModel {

    public UserCardViewModel userCard;


    public UserProfileFeedViewModel() {

    }

    public UserProfileFeedViewModel(UserProfileDTO userProfileDTO) {
        userCard = new UserCardViewModel(userProfileDTO.userCard);
        feedItems = userProfileDTO.userShoppingItems.getContent();
    }

    public UserProfileFeedViewModel(UserProfileFeedViewModel other) {
        super(other);
        this.userCard = other.userCard;
    }


    @Override
    public UserProfileFeedViewModel removeReferencesBeforeSerialization() {
        UserProfileFeedViewModel copy = new UserProfileFeedViewModel(this);
        copy.feedItems = PostService.instance().createCopiesForSerialization(feedItems);
        return copy;
    }

    //TODO does not need extend AbsBaseEntity, it only because AbsBaseBindViewHolder, find another solution.
    public static class UserCardViewModel extends AbsBaseEntityDTO<Long> {
        /**
         * The user, who this card is representing
         */
        public UserDTO userDTO;

        /**
         * following information about @userDTO
         */
        public int followersCount;
        public int followingsCount;

        /**
         * The following between "me" and @userDTO, can be null (if there is no such following).
         */
        public FollowingDTO followingDTO;

        public UserCardViewModel() {
        }

        public UserCardViewModel(UserProfileDTO.UserCardDTO userCard) {
            this.userDTO = userCard.userDTO;
            this.followingsCount = userCard.followingsCount;
            this.followersCount = userCard.followersCount;
            if(userCard.followingDTO != null) {
                this.followingDTO = userCard.followingDTO;
            }
        }

    }

}
