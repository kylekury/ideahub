package com.ideahub.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import com.ideahub.model.*;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

public class UserDAOTest {
    private UserDAO dao;
    private HibernateDAOTestUtil testUtil;

    @Before
    public void setup() {
        this.testUtil = new HibernateDAOTestUtil(
                Arrays.asList(User.class, Idea.class, IdeaPart.class, IdeaPartType.class,
                        IdeaPartSuggestion.class, IdeaCollaborator.class));

        this.dao = new UserDAO(this.testUtil.getSessionFactory());
    }

    @Test
    public void testFindByEmail() {
        final User user = User.builder()
                .email("abc@def.com")
                .username("abcdef")
                .oauthToken("token")
                .build();
        this.testUtil.getSession().save(user);

        final Optional<User> userFound = this.dao.findByEmail("abc@def.com");
        assertThat(userFound.isPresent()).isTrue();
    }
}