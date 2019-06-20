package com.filip.versu.model.dto;

import com.filip.versu.model.dto.abs.AbsBaseEntityWithOwnerDTO;

public class PostFeedbackVoteDTO extends AbsBaseEntityWithOwnerDTO<Long> {


    public PostFeedbackPossibilityDTO feedbackPossibilityDTO;



    public PostFeedbackVoteDTO() {
        super();
    }


    /**
     *
     * @param other
     * @param createdByPost true iff this constructor is invoke in PostDTO constructor
     */
    public PostFeedbackVoteDTO(PostFeedbackVoteDTO other, boolean createdByPost) {
        super(other);
        this.feedbackPossibilityDTO = new PostFeedbackPossibilityDTO(other.feedbackPossibilityDTO, createdByPost);
    }

}
