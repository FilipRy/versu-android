package com.filip.versu.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.filip.versu.model.DevicePhoto;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.abs.AbsBaseEntityWithOwnerDTO;
import com.filip.versu.view.viewmodel.AbsPostsTimelineFeedViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@JsonIgnoreProperties({"absFeedbackActionTask"})
public class PostDTO extends AbsBaseEntityWithOwnerDTO<Long> {

    public enum AccessType {
        ONLY_OWNER(1), FOLLOWERS(2), PUBLICC(3), SPECIFIC(4);

        private final int value;

        private AccessType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public String description;

    /**
     * The UTC time when the shopping item was published.
     */
    public long publishTime;

    public Location location;

    public List<PostPhotoDTO> photos = new ArrayList<>();

    public List<DevicePhoto> devicePhotos = new ArrayList<>();

    public PostDTO.AccessType accessType;

    public List<UserDTO> viewers = new ArrayList<>();

    public PostFeedbackVoteDTO myPostFeedback;

    /**
     * This is a list of the last two comments given on the shopping item.
     */
    public List<CommentDTO> comments = Collections.synchronizedList(new ArrayList<CommentDTO>());


    public PostFeedbackPossibilityDTO chosenFeedbackPossibility;


    /**
     * Represents all possible voting answers for a post.
     */
    public List<PostFeedbackPossibilityDTO> postFeedbackPossibilities = new ArrayList<>();


    public String secretUrl;

    /**
     * The vote(yes|no) task, which should be executed on this shopping item.
     */
    public AbsPostsTimelineFeedViewModel.AbsFeedbackActionTask absFeedbackActionTask;

    /**
     * Say whether the possibilities of a vote should be shown in recycler view or not.
     */
    public boolean showsPossibilities;


    public PostDTO() {

    }

    public PostDTO(PostDTO other) {
        super(other);
        this.description = other.description;
        this.publishTime = other.publishTime;
        if(other.location != null) {
            this.location = other.location;
        }

        this.photos = new ArrayList<>();
        for (PostPhotoDTO photoDTO : other.photos) {
            PostPhotoDTO photo = new PostPhotoDTO(photoDTO, true);
            photo.post = this;
            this.photos.add(photo);
        }

        for(PostFeedbackPossibilityDTO possibility: other.postFeedbackPossibilities) {
            PostFeedbackPossibilityDTO possibilityDTO = new PostFeedbackPossibilityDTO(possibility, true);
            possibilityDTO.postDTO = this;
            this.postFeedbackPossibilities.add(possibilityDTO);
        }

        if (other.myPostFeedback != null) {
            this.myPostFeedback = new PostFeedbackVoteDTO(other.myPostFeedback, true);
            //does not setting myPostFeedback.post = copy to avoid cyclic dependency
        }

        synchronized (other.comments) {
            Iterator<CommentDTO> commentIterator = other.comments.iterator();
            while(commentIterator.hasNext()) {
                CommentDTO commentDTO = commentIterator.next();
                CommentDTO comment = new CommentDTO(commentDTO, true);
                comment.postDTO = this;
                this.comments.add(comment);
            }
        }

        this.accessType = other.accessType;
        this.viewers = other.viewers;

        this.secretUrl = other.secretUrl;

        if(other.chosenFeedbackPossibility != null) {
            this.chosenFeedbackPossibility = new PostFeedbackPossibilityDTO(other.chosenFeedbackPossibility, true);
        }

    }

    public boolean isChosen() {
        return chosenFeedbackPossibility != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PostDTO that = (PostDTO) o;

        if (publishTime != that.publishTime) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (photos != null ? !photos.equals(that.photos) : that.photos != null) return false;
        return accessType == that.accessType;

    }


    @Override
    public String toString() {
        return "PostDTO{" +
                "description='" + description + '\'' +
                ", publishTime=" + publishTime +
                ", photos=" + photos +
                ", accessType=" + accessType +
                ", viewers=" + viewers +
                '}';
    }

}

