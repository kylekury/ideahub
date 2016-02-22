package com.ideahub.resources.idea;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.ideahub.cache.IdeaDefinitionCache;
import com.ideahub.model.IdeaPartType;
import com.ideahub.model.User;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/idea/definition")
@PetiteBean
@AllArgsConstructor
public class IdeaDefinitionResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdeaDefinitionResource.class);

    private final IdeaDefinitionCache ideaDefinitionCache;

    @GET
    @Timed
    @ExceptionMetered
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    public List<IdeaPartType> getIdeaDefinition(@Auth final User authenticatedUser)
            throws Exception {
        return this.ideaDefinitionCache.getIdeaDefinition();
    }
}