package com.filip.versu.model.view.abs;

import java.io.Serializable;
import java.util.List;


public abstract class AbsBaseViewModel implements Serializable {

    public AbsBaseViewModel() {
    }

    public AbsBaseViewModel(AbsBaseViewModel other) {
    }

    public abstract int getSize();

    public abstract List getPageableContent();

    public abstract AbsBaseViewModel removeReferencesBeforeSerialization();

    public abstract void fixReferences();



}
