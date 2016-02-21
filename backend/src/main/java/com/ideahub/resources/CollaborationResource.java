package com.ideahub.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.ideahub.cache.IdeaDefinitionCache;
import com.ideahub.dao.*;
import com.ideahub.exceptions.SendInviteException;
import com.ideahub.model.*;
import com.ideahub.services.ElasticSendMailService;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;

@Path("/idea")
@PetiteBean
@AllArgsConstructor
@Consumes
@Produces(MediaType.APPLICATION_JSON)
public class CollaborationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollaborationResource.class);

    //private final IdeaDefinitionCache ideaDefinitionCache;
    private final IdeaDAO ideaDAO;
    private final IdeaPartTypeDAO ideaPartTypeDAO;
    private final UserDAO userDAO;
    private final IdeaCollaboratorDAO ideaCollaboratorDAO;
    private final IdeaInvitationDAO ideaInvitationDAO;
    private final int maxAttempts = 5;

    public CollaborationResource(UserDAO userDAO, IdeaDAO ideaDAO, IdeaCollaboratorDAO collaboratorDAO,
                                 IdeaInvitationDAO invitationDAO, IdeaPartTypeDAO ideaPartTypeDAO) {
        this.ideaDAO = ideaDAO;
        this.userDAO = userDAO;
        this.ideaPartTypeDAO = ideaPartTypeDAO;
        this.ideaCollaboratorDAO = collaboratorDAO;
        this.ideaInvitationDAO = invitationDAO;
    }

    @GET
    @Path("/{ideaId}/invite/{transactionId}")
    @Timed
    @ExceptionMetered
    @UnitOfWork
    @PermitAll
    public Response getIdeaInvitation(@Auth final User authenticatedUser,
                                      @PathParam("ideaId") final long ideaId,
                                      @PathParam("transactionId ") final String transactionId)
            throws Exception {
        Response result = (Response.status(404)).build();
        final IdeaCollaborator collaborator = this.ideaCollaboratorDAO.findById(authenticatedUser.getId(), ideaId).get();

        if ((authenticatedUser.getId() == collaborator.getId().getUserId()) &&
                (collaborator.getInvitation().getCreatedDate().before(new Date(System.currentTimeMillis()))) &&
                (collaborator.getInvitation().getTransactionId().equals(transactionId))) {
            result = (Response.accepted()).build();
        }
        return result;
    }

    @POST
    @Path("{ideaId}/invite/{userEmail}")
    @Timed
    @ExceptionMetered
    @Consumes
    @UnitOfWork
    @PermitAll
    public Response inviteCollaborator(@Auth final User authenticatedUser,
                           @PathParam("ideaId") final long ideaId,
                           @PathParam("userEmail") final String targetUserEmail) throws Exception {
        Response result = (Response.status(404)).build();
        try {
            final Optional<IdeaCollaborator> collaborator = this.ideaCollaboratorDAO.findById(authenticatedUser.getId(), ideaId);
            // No need to send invitation
        } catch (Exception e) {
            User targetUser = null;
            Idea idea = null;
            IdeaPartType type = null;
            String transactionId = null;
            try {
                targetUser = userDAO.findByEmail(targetUserEmail).get();
                idea = ideaDAO.findById(ideaId).get();
                type = ideaPartTypeDAO.findById(idea.getIdeaParts().iterator().next().getIdeaPartTypeId()).get();
                String fromName = authenticatedUser.getName();
                String anIdea = type.getName();
                String to = targetUser.getEmail();
                String toName = targetUser.getName();
                String targetTransactionId = IdeaInvitation.generateTransactionId();
                int attempt = 0;
                while (attempt < maxAttempts) {
                    try {
                        transactionId = ElasticSendMailService
                                .sendElasticEmail(fromName, anIdea, to, toName, targetTransactionId,
                                        String.valueOf(ideaId), true);
                        break;
                    } catch (SendInviteException inviteAttempt) {
                        attempt++;
                        if (attempt >= maxAttempts) {
                            /// throw new Exception to retry later
                        }
                        continue;
                    }
                }
                if (transactionId != null) {
                    final IdeaInvitation invitation = IdeaInvitation.builder()
                            .acceptedState(false)
                            .inviteState(true)
                            .createdDate(new Date(System.currentTimeMillis()))
                            .transactionId(targetTransactionId)
                            .build();
                    this.ideaInvitationDAO.create(invitation);
                    IdeaCollaborator collaborator = IdeaCollaborator.builder()
                            .idea(idea)
                            .user(targetUser)
                            .invitation(invitation)
                            .build();
                    this.ideaCollaboratorDAO.create(collaborator);
                    result = (Response.accepted()).build();
                }
            } catch (Exception noUser) {
                // can't send to non registered user
            }
        }
        return result;
    }
}