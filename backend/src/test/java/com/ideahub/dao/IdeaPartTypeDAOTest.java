package com.ideahub.dao;

import com.google.common.base.Optional;
import com.ideahub.model.IdeaPartType;
import com.ideahub.model.IdeaPartTypeMetadata;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class IdeaPartTypeDAOTest {
    private IdeaPartTypeDAO dao;
    private HibernateDAOTestUtil testUtil;

    @Before
    public void setup() {
        this.testUtil = new HibernateDAOTestUtil(
                Arrays.asList(IdeaPartType.class));

        this.dao = new IdeaPartTypeDAO(this.testUtil.getSessionFactory());
    }

    @Test
    public void testFindByName() {
        final IdeaPartType ideaType = IdeaPartType.builder()
                .name("Type AB")
                .metadata(new IdeaPartTypeMetadata("A text", "just text"))
                .build();
        this.testUtil.getSession().save(ideaType);

        final Optional<IdeaPartType> notFoundIdeaType = this.dao.findByName("abc@def.com");
        assertThat(notFoundIdeaType.isPresent()).isFalse();
        final Optional<IdeaPartType> foundIdeaType = this.dao.findByName("Type AB");
        assertThat(foundIdeaType.isPresent()).isTrue();
    }
}