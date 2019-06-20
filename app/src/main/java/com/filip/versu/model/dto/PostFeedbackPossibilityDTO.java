package com.filip.versu.model.dto;

import com.filip.versu.model.dto.abs.AbsBaseEntityDTO;


public class PostFeedbackPossibilityDTO extends AbsBaseEntityDTO<Long> {


    public String name;

    public int count;


    public PostDTO postDTO;


    public PostFeedbackPossibilityDTO() {
    }

    /**
     *
     * @param other
     * @param createdByPost true iff this constructor is invoke in PostDTO constructor
     */
    public PostFeedbackPossibilityDTO(PostFeedbackPossibilityDTO other, boolean createdByPost) {

        super(other);
        this.name = other.name;
        this.count = other.count;

        if(!createdByPost) {//to avoid stackoverflow exception
            this.postDTO  = new PostDTO(other.postDTO);
        }

    }


    /**
     * equals - post feedback possibilities are compared only at id, not need to override equals method
     */


}
