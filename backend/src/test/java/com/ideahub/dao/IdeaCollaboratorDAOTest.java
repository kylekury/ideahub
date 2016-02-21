package com.ideahub.dao;

import com.google.common.base.Optional;
import com.ideahub.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class IdeaCollaboratorDAOTest {
    private UserDAO userDAO;
    private IdeaDAO ideaDAO;
    private IdeaCollaboratorDAO collaboratorDAO;
    private HibernateDAOTestUtil testUtil;

    @Before
    public void setup() {
        this.testUtil = new HibernateDAOTestUtil(
                Arrays.asList(User.class, Idea.class, IdeaPart.class, IdeaPartType.class
                        ,IdeaPartSuggestion.class, IdeaCollaborator.class));

        this.userDAO = new UserDAO(this.testUtil.getSessionFactory());
        this.ideaDAO = new IdeaDAO(this.testUtil.getSessionFactory());
        this.collaboratorDAO = new IdeaCollaboratorDAO(this.testUtil.getSessionFactory());
    }

    @Test
    public void testFindById() {
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
                .ideaPartType(ideaType)
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

        final IdeaCollaborator collab = IdeaCollaborator.builder()
                .id(collabId)
                .user(userB)
                .idea(ideaFound.get())
                .inviteStatus((byte) 0)
                .build();
        this.testUtil.getSession().save(collab);

        final Optional<IdeaCollaborator> collaboratorFound = this.collaboratorDAO
                .findById(userB.getId(), ideaFound.get().getId());

        assertThat(collaboratorFound.isPresent()).isTrue();
        assertThat(collaboratorFound.get().getUser().getEmail().equals(userB.getEmail())).isTrue();
        assertThat(collaboratorFound.get().getIdea().getId() == ideaFound.get().getId()).isTrue();
    }
}