package com.ideahub.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import com.ideahub.exceptions.SendInviteException;

import lombok.AllArgsConstructor;

/**
 * Created by massa on 21/02/2016.
 */
public class SendInviteTest {
    @AllArgsConstructor
    private static class ElasticSendMailServiceMock extends ElasticSendMailService {
        private final String expectedData;
        private final String result;

        @Override
        protected String sendRequest(final String data) throws MalformedURLException, IOException {
            assertThat(data).isEqualTo(this.expectedData);
            return this.result;
        }
    }

    private ElasticSendMailService sendMailService;

    @Before
    public void setup() {
        this.sendMailService = new ElasticSendMailServiceMock(
                "userName=623c65ce-c629-4f1c-ae4d-cfc3cebfa241&api_key=623c65ce-c629-4f1c-ae4d-cfc3cebfa241&from=support%40ideahub.io&from_name=IdeaHub&subject=Massanori+is+inviting+you+to+collaborate+with+his%2Fher+Idea%3A+Idea+name&body_html=%3C%21DOCTYPE+html%3E%3Chtml%3E%3Cbody%3E%3Ch1%3EIdeaHub%3C%2Fh1%3E%3Cbr%2F%3E%3Cp%3EHi+Test%3C%2Fp%3E%3Cbr%2F%3E%3Cp%3EIdeaHub+has+an+Invitation+for+you%3C%2Fp%3E%0A%3Ca+href%3D%22http%3A%2F%2F169.44.56.200%2Fidea%2F1%2Finvite%2Finvite_id%22%3EClick+here+to+join%3C%2Fa%3E%0A%3C%2Fbody%3E%0A%3C%2Fhtml%3E&to=massanori%40gmail.com",
                "f74b9f96-f89a-4cfe-813f-5f86df1cb37f");
    }

    @Test
    public void sendInvite() throws SendInviteException {
        final String fromName = "Massanori";
        final String anIdea = "Idea name";
        final String to = "massanori@gmail.com";
        final String toName = "Test";
        final String inviteId = "invite_id";
        final String ideadId = "1";
        final String transaction = this.sendMailService.sendElasticEmail(fromName, anIdea, to,
                toName, inviteId, ideadId, false);
        assertThat(transaction).isNotNull();
    }
}