package com.filip.versu.model.dto;


import com.filip.versu.model.dto.abs.AbsBaseEntityDTO;

public class PostPhotoDTO extends AbsBaseEntityDTO<Long> {


    public String path;
    /**
     * This is the time, when the Photo was created.
     * UTC time.
     */
    public long takenTime;
    public String description;

    public PostDTO post;


    public PostPhotoDTO() {

    }

    /**
     * @param other
     * @param createdFromShoppingItem
     */
    public PostPhotoDTO(PostPhotoDTO other, boolean createdFromShoppingItem) {
        super(other);
        this.path = other.path;
        this.takenTime = other.takenTime;
        this.description = other.description;
        if(!createdFromShoppingItem) {
            this.post = new PostDTO(other.post);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PostPhotoDTO photoDTO = (PostPhotoDTO) o;

        if (takenTime != photoDTO.takenTime) return false;
        if (path != null ? !path.equals(photoDTO.path) : photoDTO.path != null) return false;
        return !(description != null ? !description.equals(photoDTO.description) : photoDTO.description != null);

    }
}
