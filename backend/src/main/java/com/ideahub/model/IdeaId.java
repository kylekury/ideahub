package com.ideahub.model;
// Generated Feb 20, 2016 12:24:43 AM by Hibernate Tools 4.3.1

/**
 * IdeaId generated by hbm2java
 */
public class IdeaId implements java.io.Serializable {

    private int id;
    private int userId;

    public IdeaId() {
    }

    public IdeaId(int id, int userId) {
        this.id = id;
        this.userId = userId;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof IdeaId))
            return false;
        IdeaId castOther = (IdeaId) other;

        return (this.getId() == castOther.getId())
                && (this.getUserId() == castOther.getUserId());
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result + this.getId();
        result = 37 * result + this.getUserId();
        return result;
    }

}