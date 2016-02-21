package com.ideahub.resources;

import com.google.common.base.Optional;
import com.ideahub.dao.*;
import com.ideahub.model.*;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class IdeaCollaboratorResourceTest {
    private UserDAO userDAO;
    private IdeaDAO ideaDAO;
    private IdeaCollaboratorDAO collaboratorDAO;
    private IdeaInvitationDAO invitationDAO;
    private IdeaPartTypeDAO ideaPartTypeDAO;

    private CollaborationResource resource;

    private HibernateDAOTestUtil testUtil;


    @Before
    public void setup() {
        this.testUtil = new HibernateDAOTestUtil(
                Arrays.asList(User.class, Idea.class, IdeaPart.class, IdeaPartType.class
                        ,IdeaPartSuggestion.class, IdeaCollaborator.class, IdeaInvitation.class));
        this.userDAO = new UserDAO(this.testUtil.getSessionFactory());
        this.ideaDAO = new IdeaDAO(this.testUtil.getSessionFactory());
        this.collaboratorDAO = new IdeaCollaboratorDAO(this.testUtil.getSessionFactory());
        this.invitationDAO = new IdeaInvitationDAO(this.testUtil.getSessionFactory());
        this.ideaPartTypeDAO = new IdeaPartTypeDAO(this.testUtil.getSessionFactory());

        this.resource = new CollaborationResource(this.userDAO, this.ideaDAO, this.collaboratorDAO,
                this.invitationDAO, this.ideaPartTypeDAO);
    }

    @Test
    public void testSendMatchInvitation() {
        final User userA = User.builder()
                .email("abc@def.com")
                .username("abcdef")
                .oauthToken("token")
                .build();
        this.testUtil.getSession().save(userA);
        final Optional<User> userFound = this.userDAO.findByEmail("abc@def.com");
        assertThat(userFound.isPresent()).isTrue();

        final User userB = User.builder()
                .email("ghi@def.com")
                .username("ghi")
                .oauthToken("token")
                .build();
        this.testUtil.getSession().save(userB);

        final IdeaPartType ideaType = IdeaPartType.builder()
                .name("Type A")
                .metadata(new IdeaPartTypeMetadata("A text", "tooltip","just text"))
                .build();
        this.testUtil.getSession().save(ideaType);

        final IdeaPart ideaPart = IdeaPart.builder()
                .content("Content text")
                .justification("justif")
                .downvotes(0)
                .upvotes(0)
                .ideaPartTypeId(ideaType.getId())
                .userId(userFound.get().getId())
                .ideaId(1)
                .build();
        Set<IdeaPart> ideaPartSet = new HashSet<>();
        ideaPartSet.add(ideaPart);

        final Idea idea = Idea.builder()
                .ideaParts(ideaPartSet)
                .userId(userFound.get().getId())
                .build();
        this.testUtil.getSession().save(idea);

        final Optional<Idea> ideaFound = this.ideaDAO.findByUserId(userFound.get().getId());
        assertThat(ideaFound.isPresent()).isTrue();

        IdeaCollaboratorId collabId = IdeaCollaboratorId.builder()
                .userId((int) userB.getId())
                .ideaId((int) idea.getId())
                .build();
        final IdeaInvitation invitation = IdeaInvitation.builder()
                .acceptedState(false)
                .inviteState(true)
                .transactionId(IdeaInvitation.generateTransactionId())
                .createdDate(new java.sql.Date(System.currentTimeMillis()))
                .build();
        testUtil.getSession().save(invitation);

        final IdeaCollaborator collab = IdeaCollaborator.builder()
                .id(collabId)
                .user(userB)
                .idea(ideaFound.get())
                .invitation(invitation)
                .build();
        this.testUtil.getSession().save(collab);
        final Optional<IdeaCollaborator> collaboratorFound = this.collaboratorDAO
                .findById(userB.getId(), ideaFound.get().getId());

        assertThat(collaboratorFound.isPresent()).isTrue();
        assertThat(collaboratorFound.get().getUser().getEmail().equals(userB.getEmail())).isTrue();
        assertThat(collaboratorFound.get().getIdea().getId() == ideaFound.get().getId()).isTrue();
        try {
            final Response response = resource.getIdeaInvitation(userB,idea.getId(),invitation.getTransactionId());
            assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED_202);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}