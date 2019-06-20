package com.filip.versu.model.dto;


import com.filip.versu.model.dto.abs.AbsBaseEntityWithOwnerDTO;

import java.util.Objects;

public class CommentDTO extends AbsBaseEntityWithOwnerDTO<Long> {

    public long timestamp;

    public String content;

    public PostDTO postDTO;

    public CommentDTO() {
        super();
    }

    public CommentDTO(CommentDTO commentDTO, boolean creatingFromShoppingItem) {
        super(commentDTO);
        this.timestamp = commentDTO.timestamp;
        if(!creatingFromShoppingItem) {
            this.postDTO = commentDTO.postDTO;
        }

        this.content = commentDTO.content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommentDTO that = (CommentDTO) o;
        return timestamp == that.timestamp &&
                Objects.equals(content, that.content) &&
                Objects.equals(postDTO, that.postDTO);
    }

}
