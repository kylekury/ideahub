package com.ideahub.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.ideahub.model.Idea;
import com.ideahub.model.IdeaCollaborator;
import com.ideahub.model.IdeaPart;
import com.ideahub.model.IdeaPartSuggestion;
import com.ideahub.model.IdeaPartType;
import com.ideahub.model.IdeaPartTypeMetadata;
import com.ideahub.model.User;

public class IdeaDAOTest {
    private UserDAO userDAO;
    private IdeaDAO ideaDAO;
    private HibernateDAOTestUtil testUtil;

    @Before
    public void setup() {
        this.testUtil = new HibernateDAOTestUtil(
                Arrays.asList(User.class, Idea.class, IdeaPart.class, IdeaPartType.class,
                        IdeaPartSuggestion.class, IdeaCollaborator.class));

        this.userDAO = new UserDAO(this.testUtil.getSessionFactory());
        this.ideaDAO = new IdeaDAO(this.testUtil.getSessionFactory(), this.userDAO);
    }

    @Test
    public void testFindByUserId() {
        final User user = User.builder()
                .email("abc@def.com")
                .username("abcdef")
                .oauthToken("token")
                .build();
        this.testUtil.getSession().save(user);
        final Optional<User> userFound = this.userDAO.findByEmail("abc@def.com");
        assertThat(userFound.isPresent()).isTrue();

        final IdeaPartType ideaType = IdeaPartType.builder()
                .name("Type A")
                .metadata(new IdeaPartTypeMetadata("A text", "tooltip", "just text"))
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
        final Map<Integer, IdeaPart> ideaPartMap = new HashMap<>();
        ideaPartMap.put(1, ideaPart);

        final Idea idea = Idea.builder()
                .ideaParts(ideaPartMap)
                .userId(userFound.get().getId())
                .build();
        this.testUtil.getSession().save(idea);

        final Optional<Idea> ideaFound = this.ideaDAO.findByUserId(userFound.get().getId());
        assertThat(ideaFound.isPresent()).isTrue();
    }

    @Test
    public void testFindRecent() {
        final User user = User.builder()
                .email("abc@def.com")
                .username("abcdef")
                .oauthToken("token")
                .build();
        this.testUtil.getSession().save(user);

        final IdeaPartType ideaType = IdeaPartType.builder()
                .name("Type A")
                .metadata(new IdeaPartTypeMetadata("A text", "tooltip", "just text"))
                .build();
        this.testUtil.getSession().save(ideaType);

        final IdeaPart ideaPart = IdeaPart.builder()
                .content("Content text")
                .justification("justif")
                .downvotes(0)
                .upvotes(0)
                .ideaPartTypeId(ideaType.getId())
                .userId(user.getId())
                .ideaId(1)
                .build();
        final Map<Integer, IdeaPart> ideaPartMap = new HashMap<>();
        ideaPartMap.put(1, ideaPart);

        final Idea idea = Idea.builder()
                .ideaParts(ideaPartMap)
                .userId(user.getId())
                .build();
        this.testUtil.getSession().save(idea);

        final Set<Idea> ideas = this.ideaDAO.findRecent(10);

        assertThat(ideas).hasSize(1).containsOnly(idea);
    }

    @Test
    public void testFindRecentMultipleIdeas() {
        final User user = User.builder()
                .email("abc@def.com")
                .username("abcdef")
                .oauthToken("token")
                .build();
        this.testUtil.getSession().save(user);

        final IdeaPartType ideaType = IdeaPartType.builder()
                .name("Type A")
                .metadata(new IdeaPartTypeMetadata("A text", "tooltip", "just text"))
                .build();
        this.testUtil.getSession().save(ideaType);

        final IdeaPart ideaPart = IdeaPart.builder()
                .content("Content text")
                .justification("justif")
                .downvotes(0)
                .upvotes(0)
                .ideaPartTypeId(ideaType.getId())
                .userId(user.getId())
                .ideaId(1)
                .build();
        final Map<Integer, IdeaPart> ideaPartMap = new HashMap<>();
        ideaPartMap.put(1, ideaPart);

        final Idea idea = Idea.builder()
                .ideaParts(ideaPartMap)
                .userId(user.getId())
                .build();
        this.testUtil.getSession().save(idea);

        final Idea idea2 = Idea.builder()
                .ideaParts(ideaPartMap)
                .userId(user.getId())
                .build();
        this.testUtil.getSession().save(idea2);

        final Set<Idea> ideas = this.ideaDAO.findRecent(10);

        assertThat(ideas).hasSize(2).containsOnly(idea, idea2);
    }

    @Test
    public void testFindRecentMultipleIdeasLimitToOne() {
        final User user = User.builder()
                .email("abc@def.com")
                .username("abcdef")
                .oauthToken("token")
                .build();
        this.testUtil.getSession().save(user);

        final IdeaPartType ideaType = IdeaPartType.builder()
                .name("Type A")
                .metadata(new IdeaPartTypeMetadata("A text", "tooltip", "just text"))
                .build();
        this.testUtil.getSession().save(ideaType);

        final IdeaPart ideaPart = IdeaPart.builder()
                .content("Content text")
                .justification("justif")
                .downvotes(0)
                .upvotes(0)
                .ideaPartTypeId(ideaType.getId())
                .userId(user.getId())
                .ideaId(1)
                .build();
        final Map<Integer, IdeaPart> ideaPartMap = new HashMap<>();
        ideaPartMap.put(1, ideaPart);

        final Idea idea = Idea.builder()
                .ideaParts(ideaPartMap)
                .userId(user.getId())
                .createdAt(DateTime.parse("2015-01-15").toDate())
                .build();
        this.testUtil.getSession().save(idea);

        final Idea idea2 = Idea.builder()
                .ideaParts(ideaPartMap)
                .userId(user.getId())
                .createdAt(new Date())
                .build();
        this.testUtil.getSession().save(idea2);

        this.testUtil.getSession().close();
        this.testUtil.openSession();

        final Set<Idea> ideas = this.ideaDAO.findRecent(1);

        assertThat(ideas).hasSize(1).containsExactly(idea2);
    }
}