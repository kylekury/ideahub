package com.ideahub.model;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

import java.util.HashSet;
import java.util.Set;

/**
 * IdeaPartType generated by hbm2java
 */
public class IdeaPartType implements java.io.Serializable {

    private int id;
    private String name;
    private String metadata;
    private Set ideaParts = new HashSet(0);

    public IdeaPartType() {
    }

    public IdeaPartType(int id) {
        this.id = id;
    }

    public IdeaPartType(int id, String name, String metadata, Set ideaParts) {
        this.id = id;
        this.name = name;
        this.metadata = metadata;
        this.ideaParts = ideaParts;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Set getIdeaParts() {
        return this.ideaParts;
    }

    public void setIdeaParts(Set ideaParts) {
        this.ideaParts = ideaParts;
    }

}
