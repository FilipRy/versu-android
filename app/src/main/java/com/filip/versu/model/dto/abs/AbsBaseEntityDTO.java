package com.filip.versu.model.dto.abs;


public abstract class AbsBaseEntityDTO<K> implements BaseDTO<K> {

    private K id;

    public AbsBaseEntityDTO() {
    }


    public AbsBaseEntityDTO(BaseDTO<K> other) {
        if (other == null) {
            other = null;
        }
        this.setId(other.getId());
    }

    @Override
    public K getId() {
        return id;
    }

    @Override
    public void setId(K id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseDTO<?> that = (BaseDTO<?>) o;

        return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);

    }

}
