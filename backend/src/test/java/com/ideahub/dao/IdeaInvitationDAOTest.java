package com.ideahub.dao;

import com.google.common.base.Optional;
import com.ideahub.model.IdeaInvitation;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Date;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class IdeaInvitationDAOTest {
    private static IdeaInvitationDAO dao;
    private static HibernateDAOTestUtil testUtil;
    private static String name;

    @BeforeClass
    public static void setup() {
        testUtil = new HibernateDAOTestUtil(
                Arrays.asList(IdeaInvitation.class));

        dao = new IdeaInvitationDAO(testUtil.getSessionFactory());
    }

    @Test
    public void testFindById() {
        final IdeaInvitation invitation = IdeaInvitation.builder()
                .acceptedState(false)
                .inviteState(true)
                .createdDate(new Date(System.currentTimeMillis()))
                .transactionId("test-transaction-id")
                .build();
        testUtil.getSession().save(invitation);

        final Optional<IdeaInvitation> foundIdeaType = dao.findById(invitation.getId());
        assertThat(foundIdeaType.isPresent()).isTrue();
    }
}