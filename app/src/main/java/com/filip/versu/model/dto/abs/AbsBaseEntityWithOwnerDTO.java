package com.filip.versu.model.dto.abs;


import com.filip.versu.model.dto.UserDTO;

public abstract class AbsBaseEntityWithOwnerDTO<K> extends AbsBaseEntityDTO<K> {

    public UserDTO owner;

    public AbsBaseEntityWithOwnerDTO() {
    }

    public AbsBaseEntityWithOwnerDTO(AbsBaseEntityWithOwnerDTO other) {
        super(other);
        this.owner = new UserDTO(other.owner);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AbsBaseEntityWithOwnerDTO<?> that = (AbsBaseEntityWithOwnerDTO<?>) o;

        return !(owner != null ? !owner.equals(that.owner) : that.owner != null);

    }

}
