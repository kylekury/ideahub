package com.ideahub.dao;

import com.google.common.base.Optional;
import com.ideahub.model.IdeaPartType;
import com.ideahub.model.IdeaPartTypeMetadata;
import org.hibernate.exception.GenericJDBCException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class IdeaPartTypeDAOTest {
    private static IdeaPartTypeDAO dao;
    private static HibernateDAOTestUtil testUtil;
    private static String name;

    @BeforeClass
    public static void setup() {
        testUtil = new HibernateDAOTestUtil(
                Arrays.asList(IdeaPartType.class));

        dao = new IdeaPartTypeDAO(testUtil.getSessionFactory());
        Random seed = new Random(1);
        for (int i = 0; i < 100; i++) {
            final IdeaPartType ideaType = IdeaPartType.builder()
                    .name(String.valueOf(seed.nextInt(10000)))
                    .metadata(new IdeaPartTypeMetadata("A text", "just text"))
                    .build();
            if (i == 0) {
                name = ideaType.getName();
            }
            try {
                testUtil.getSession().save(ideaType);
            } catch (GenericJDBCException e) {
                System.out.println(i);
            }
        }
    }

    @Test
    public void testFindByName() {
        final Optional<IdeaPartType> notFoundIdeaType = dao.findByName("abc@def.com");
        assertThat(notFoundIdeaType.isPresent()).isFalse();
        final Optional<IdeaPartType> foundIdeaType = dao.findByName(name);
        assertThat(foundIdeaType.isPresent()).isTrue();
    }

    @Test
    public void testFindCachedName() {
        final IdeaPartType ideaTypeA = IdeaPartType.builder()
                .name("Type ABC")
                .metadata(new IdeaPartTypeMetadata("A text", "just text"))
                .build();
        testUtil.getSession().save(ideaTypeA);

        final IdeaPartType ideaTypeB = IdeaPartType.builder()
                .name("Type ABCD")
                .metadata(new IdeaPartTypeMetadata("A text", "just text"))
                .build();
        testUtil.getSession().save(ideaTypeB);

        final Optional<IdeaPartType> notFoundIdeaType = dao.findCachedName("abc@def.com");
        assertThat(notFoundIdeaType.isPresent()).isFalse();
        final Optional<IdeaPartType> foundIdeaType = dao.findCachedName("Type ABC");
        assertThat(foundIdeaType.isPresent()).isTrue();
    }
}