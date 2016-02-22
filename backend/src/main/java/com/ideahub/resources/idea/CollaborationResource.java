package com.ideahub.resources.idea;

import java.sql.Date;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.ideahub.dao.IdeaCollaboratorDAO;
import com.ideahub.dao.IdeaDAO;
import com.ideahub.dao.IdeaInvitationDAO;
import com.ideahub.dao.IdeaPartTypeDAO;
import com.ideahub.dao.UserDAO;
import com.ideahub.exceptions.SendInviteException;
import com.ideahub.model.Idea;
import com.ideahub.model.IdeaCollaborator;
import com.ideahub.model.IdeaInvitation;
import com.ideahub.model.IdeaPartType;
import com.ideahub.model.User;
import com.ideahub.services.ElasticSendMailService;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/idea")
@PetiteBean
@AllArgsConstructor
@Consumes
@Produces(MediaType.APPLICATION_JSON)
public class CollaborationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollaborationResource.class);
    private static final int MAX_ATTEMPTS = 5;

    //private final IdeaDefinitionCache ideaDefinitionCache;
    private final IdeaDAO ideaDAO;
    private final IdeaPartTypeDAO ideaPartTypeDAO;
    private final UserDAO userDAO;
    private final IdeaCollaboratorDAO ideaCollaboratorDAO;
    private final IdeaInvitationDAO ideaInvitationDAO;
    private ElasticSendMailService sendMailService;

    public CollaborationResource(final UserDAO userDAO, final IdeaDAO ideaDAO, final IdeaCollaboratorDAO collaboratorDAO,
                                 final IdeaInvitationDAO invitationDAO, final IdeaPartTypeDAO ideaPartTypeDAO) {
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
            collaborator.getInvitation().setAcceptedState(true);
            this.ideaInvitationDAO.update(collaborator.getInvitation());
            result = (Response.ok()).build();
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
        Response result = (Response.status(Response.Status.NOT_FOUND)).build();
        User targetUser = null;
        try {
            targetUser = this.userDAO.findByEmail(targetUserEmail).get();
            try {
                this.ideaCollaboratorDAO.findById(targetUser.getId(), ideaId);
                // preventing from resend invites already sent
                result = (Response.ok()).build();
            } catch (final Exception noCollaborator) {
                Idea idea = null;
                IdeaPartType type = null;
                String transactionId = null;
                try {
                    idea = this.ideaDAO.findById(ideaId).get();
                    type = this.ideaPartTypeDAO.findById(idea.getIdeaParts().get(0).getIdeaPartTypeId()).get();
                    final String fromName = authenticatedUser.getName();
                    final String anIdea = type.getName();
                    final String to = targetUser.getEmail();
                    final String toName = targetUser.getName();
                    final String targetTransactionId = IdeaInvitation.generateTransactionId();
                    int attempt = 0;
                    while (attempt < MAX_ATTEMPTS) {
                        try {
                            transactionId = this.sendMailService
                                    .sendElasticEmail(fromName, anIdea, to, toName, targetTransactionId,
                                            String.valueOf(ideaId), true);
                            break;
                        } catch (final SendInviteException inviteAttempt) {
                            attempt++;
                            if (attempt >= MAX_ATTEMPTS) {
                                result = (Response.status(Response.Status.SERVICE_UNAVAILABLE)).build();
                                break;
                            }
                            continue;
                        }
                    }
                    if ((transactionId != null) && (result.equals(Response.Status.NOT_FOUND))) {
                        final IdeaInvitation invitation = IdeaInvitation.builder()
                                .acceptedState(false)
                                .inviteState(true)
                                .createdDate(new Date(System.currentTimeMillis()))
                                .transactionId(targetTransactionId)
                                .build();
                        this.ideaInvitationDAO.create(invitation);
                        final IdeaCollaborator collaborator = IdeaCollaborator.builder()
                                .idea(idea)
                                .user(targetUser)
                                .invitation(invitation)
                                .build();
                        this.ideaCollaboratorDAO.create(collaborator);
                        result = (Response.accepted()).build();
                    }
                } catch (final Exception e){
                    result = (Response.status(Response.Status.INTERNAL_SERVER_ERROR)).build();
                }
            }
        } catch (final Exception noEmailAddressFound) {
            // email not found no need to change the response
        }
        return result;
    }
}