package com.ideahub;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import lombok.Getter;

@Getter
public class IdeaHubConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("database")
    private final DataSourceFactory database = new DataSourceFactory();

    @NotNull
    private String clientId;
    @NotNull
    private String clientSecret;
    @NotNull
    private String secretState;
    @NotNull
    private String authCallback;
}