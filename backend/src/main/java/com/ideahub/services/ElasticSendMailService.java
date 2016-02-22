package com.ideahub.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.ideahub.exceptions.SendInviteException;

import jodd.petite.meta.PetiteBean;

/**
 * Created by massanori on 21/02/2016.
 */
@PetiteBean
public class ElasticSendMailService {
    public String sendElasticEmail(final String fromName, final String anIdea,
            final String to, final String toName, final String inviteId, final String ideaId,
            final boolean sendEmail)
                    throws SendInviteException {
        String result = null;
        final String userName = "623c65ce-c629-4f1c-ae4d-cfc3cebfa241";
        final String apiKey = "623c65ce-c629-4f1c-ae4d-cfc3cebfa241";
        final String from = "support@ideahub.io";
        final String appName = "IdeaHub";
        final String subject = fromName + " is inviting you to collaborate with his/her Idea: "
                + anIdea;

        final String body = "<!DOCTYPE html>" +
                "<html>" +
                "<body>" +
                "<h1>IdeaHub</h1><br/>" +
                "<p>Hi " + toName + "</p><br/>" +
                "<p>IdeaHub has an Invitation for you</p>\n" +
                "<a href=\"http://169.44.56.200/idea/" + ideaId + "/invite/" + inviteId
                + "\">Click here to join</a>\n" +
                "</body>\n" +
                "</html>";
        try {
            // Construct the data
            String data = "userName=" + URLEncoder.encode(userName, "UTF-8");
            data += "&api_key=" + URLEncoder.encode(apiKey, "UTF-8");
            data += "&from=" + URLEncoder.encode(from, "UTF-8");
            data += "&from_name=" + URLEncoder.encode(appName, "UTF-8");
            data += "&subject=" + URLEncoder.encode(subject, "UTF-8");
            data += "&body_html=" + URLEncoder.encode(body, "UTF-8");
            data += "&to=" + URLEncoder.encode(to, "UTF-8");

            result = this.sendRequest(data);
        } catch (final Exception e) {
            throw new SendInviteException();
        }
        final String regex = "^[\\w]{8}-([\\w]{4}-){3}[\\w]{12}$";

        return ((result != null) && (result.matches(regex))) ? result : null;
    }

    protected String sendRequest(final String data) throws MalformedURLException, IOException {
        String result;
        // Send data
        final URL url = new URL("https://api.elasticemail.com/mailer/send");
        final URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        final BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        result = rd.readLine();
        wr.close();
        rd.close();
        return result;
    }
}