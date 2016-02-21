package com.ideahub.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Random;

import org.hibernate.exception.GenericJDBCException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Optional;
import com.ideahub.model.IdeaPartType;
import com.ideahub.model.IdeaPartTypeMetadata;

public class IdeaPartTypeDAOTest {
    private static IdeaPartTypeDAO dao;
    private static HibernateDAOTestUtil testUtil;
    private static String name;

    @BeforeClass
    public static void setup() {
        testUtil = new HibernateDAOTestUtil(
                Arrays.asList(IdeaPartType.class));

        dao = new IdeaPartTypeDAO(testUtil.getSessionFactory());
        final Random seed = new Random(1);
        for (int i = 0; i < 100; i++) {
            final IdeaPartType ideaType = IdeaPartType.builder()
                    .name(String.valueOf(seed.nextInt(10000)))
                    .metadata(new IdeaPartTypeMetadata("A text", "tooltip","just text"))
                    .build();
            if (i == 0) {
                name = ideaType.getName();
            }
            try {
                testUtil.getSession().save(ideaType);
            } catch (final GenericJDBCException e) {
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
}