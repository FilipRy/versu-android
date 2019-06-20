package com.filip.versu.model.dto;


import com.filip.versu.model.dto.abs.AbsBaseEntityDTO;

public class FollowingDTO extends AbsBaseEntityDTO<Long> {

    public UserDTO creator;
    public UserDTO target;

    public long createTime;

    public FollowingDTO() {

    }

    public FollowingDTO(UserDTO creator, UserDTO target) {
        this.creator = creator;
        this.target = target;
    }

    public FollowingDTO(UserDTO target) {
        this.target = target;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FollowingDTO that = (FollowingDTO) o;

        if (createTime != that.createTime) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        return !(target != null ? !target.equals(that.target) : that.target != null);

    }

}
