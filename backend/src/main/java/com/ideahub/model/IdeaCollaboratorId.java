package com.ideahub.model;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

/**
 * IdeaCollaboratorId generated by hbm2java
 */
public class IdeaCollaboratorId implements java.io.Serializable {

    private int userId;
    private int ideaId;

    public IdeaCollaboratorId() {
    }

    public IdeaCollaboratorId(int userId, int ideaId) {
        this.userId = userId;
        this.ideaId = ideaId;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIdeaId() {
        return this.ideaId;
    }

    public void setIdeaId(int ideaId) {
        this.ideaId = ideaId;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof IdeaCollaboratorId))
            return false;
        IdeaCollaboratorId castOther = (IdeaCollaboratorId) other;

        return (this.getUserId() == castOther.getUserId())
                && (this.getIdeaId() == castOther.getIdeaId());
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result + this.getUserId();
        result = 37 * result + this.getIdeaId();
        return result;
    }

}