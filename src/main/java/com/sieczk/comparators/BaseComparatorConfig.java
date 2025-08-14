package com.sieczk.comparators;

public abstract class BaseComparatorConfig {

    /**
     * Should an exception be thrown if images vary in size
     */
    private boolean enforceImageSize = true;

    /**
     * Should images be resized to the same size before comparison
     */
    private boolean assureImageSize = true;


    public boolean isEnforceImageSize() {
        return enforceImageSize;
    }

    public BaseComparatorConfig enforceImageSize(boolean enforceImageSize) {
        this.enforceImageSize = enforceImageSize;
        return this;
    }

    public boolean isAssureImageSize() {
        return assureImageSize;
    }

    public BaseComparatorConfig assureImageSize(boolean assureImageSize) {
        this.assureImageSize = assureImageSize;
        return this;
    }
}
