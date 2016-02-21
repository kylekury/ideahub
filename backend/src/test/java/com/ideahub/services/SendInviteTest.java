package com.ideahub.services;

import com.ideahub.exceptions.SendInviteException;
import com.ideahub.model.IdeaInvitation;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Created by massa on 21/02/2016.
 */
public class SendInviteTest {

    @Test
    public void sendInvite() throws SendInviteException {
        String fromName = "Massanori";
        String anIdea = "Idea name";
        String to = "massanori@gmail.com";
        String toName = "Test";
        String inviteId = IdeaInvitation.generateTransactionId();
        String ideadId = "1";
        String transaction = ElasticSendMailService.sendElasticEmail(fromName, anIdea, to, toName,
                inviteId, ideadId, false);
        assertThat(transaction != null).isTrue();
    }
}
