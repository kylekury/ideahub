package com.ideahub.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecentIdea {
    private long id;
    private long userId;
    private String title;
    private String description;
}