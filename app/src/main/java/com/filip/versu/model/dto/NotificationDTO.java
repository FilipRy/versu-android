package com.filip.versu.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.filip.versu.model.dto.abs.AbsBaseEntityDTO;

import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO extends AbsBaseEntityDTO<Long> {

    public enum NotificationType {
        following, post, comment, post_feedback
    }

    public NotificationType type;

    /**
     * References ID of the entity, which creation caused to create this notification. == ID of AbsNotification.getContentEntity()
     */
    public Long contentEntityID;


    /**
     * This are the url of photos, which will be shows as thumbnails within the notification
     */
    public List<String> photoUrls = new ArrayList<>();

    /**
     * This is the count of users, whose actions(comments, vote_yes yes ...) created this notification
     */
    public int count;

    /**
     * Last of users, who created actions, which created this notification.
     */
    public UserDTO userDTO;


    public long creationTime;

    /**
     * Says if this notification was already clicked (& viewed) by the user, or it's still new.
     */
    public boolean seen;

}
