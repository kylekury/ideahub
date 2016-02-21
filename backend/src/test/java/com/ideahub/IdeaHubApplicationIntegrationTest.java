package com.ideahub;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.ideahub.model.User;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.util.Duration;

public class IdeaHubApplicationIntegrationTest {
    public static class IdeaHubApplicationMock extends IdeaHubApplication {
        @Override
        public void run(final IdeaHubConfiguration configuration, final Environment environment)
                throws Exception {
            super.run(configuration, environment);

            final Session session = this.hibernate.getSessionFactory().openSession();
            final User user = User.builder()
                    .email("abc@def")
                    .username("testuser")
                    .oauthToken("token")
                    .avatarUrl("avatar")
                    .build();
            session.save(user);
            session.flush();
            session.close();
        }
    }

    @ClassRule
    public static final DropwizardAppRule<IdeaHubConfiguration> RULE = new DropwizardAppRule<IdeaHubConfiguration>(
            IdeaHubApplication.class, ResourceHelpers.resourceFilePath("config.yml"));

    private static Client client;

    @BeforeClass
    public static void createClient() {
        final JerseyClientConfiguration configuration = new JerseyClientConfiguration();
        configuration.setTimeout(Duration.minutes(1L));
        configuration.setConnectionTimeout(Duration.minutes(1L));
        configuration.setConnectionRequestTimeout(Duration.minutes(1L));
        client = new JerseyClientBuilder(RULE.getEnvironment()).using(configuration)
                .build("test client");
    }

    @Test
    public void testGetUser() {
        final Response response = client
                .target(String.format("http://localhost:%d/idea/definition", RULE.getLocalPort()))
                .request().get();

        assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
    }
}