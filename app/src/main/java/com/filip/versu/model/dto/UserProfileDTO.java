package com.filip.versu.model.dto;

import com.filip.versu.service.helper.SpringPage;

import java.io.Serializable;

/**
 * This DTO represents the profile of a user with the content of the profile.
 */
public class UserProfileDTO implements Serializable {

    public SpringPage.PostSpringPage userShoppingItems;

    public UserCardDTO userCard;

    public static class UserCardDTO {
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
    }


}
