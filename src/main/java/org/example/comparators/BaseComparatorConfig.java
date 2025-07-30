package org.example.comparators;

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

    public void setEnforceImageSize(boolean enforceImageSize) {
        this.enforceImageSize = enforceImageSize;
    }

    public boolean isAssureImageSize() {
        return assureImageSize;
    }

    public void setAssureImageSize(boolean assureImageSize) {
        this.assureImageSize = assureImageSize;
    }
}
