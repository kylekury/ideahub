package com.ideahub.services;

import com.ideahub.exceptions.SendInviteException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by massanori on 21/02/2016.
 */
public class ElasticSendMailService {

    public static String sendElasticEmail(String fromName, String anIdea, String to,
                                          String toName, String inviteId, String ideaId, boolean sendEmail) throws SendInviteException {
        String result;
        if (sendEmail) {
            String userName = "623c65ce-c629-4f1c-ae4d-cfc3cebfa241";
            String apiKey = "623c65ce-c629-4f1c-ae4d-cfc3cebfa241";
            String from = "support@ideahub.io";
            String appName = "IdeaHub";
            String subject = fromName + " is inviting you to collaborate with his/her Idea: " + anIdea;

            String body = "<!DOCTYPE html>" +
                    "<html>" +
                    "<body>" +
                    "<h1>IdeaHub</h1><br/>" +
                    "<p>Hi " + toName + "</p><br/>" +
                    "<p>IdeaHub has an Invitation for you</p>\n" +
                    "<a href=\"http://169.44.56.200/idea/"+ideaId+"/invite/"+inviteId+"\">Click here to join</a>\n" +
                    "</body>\n" +
                    "</html>";
            try {
                //Construct the data
                String data = "userName=" + URLEncoder.encode(userName, "UTF-8");
                data += "&api_key=" + URLEncoder.encode(apiKey, "UTF-8");
                data += "&from=" + URLEncoder.encode(from, "UTF-8");
                data += "&from_name=" + URLEncoder.encode(appName, "UTF-8");
                data += "&subject=" + URLEncoder.encode(subject, "UTF-8");
                data += "&body_html=" + URLEncoder.encode(body, "UTF-8");
                data += "&to=" + URLEncoder.encode(to, "UTF-8");

                //Send data
                URL url = new URL("https://api.elasticemail.com/mailer/send");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = rd.readLine();
                wr.close();
                rd.close();
            } catch (Exception e) {
                throw new SendInviteException();
            }
        }
        result = "f74b9f96-f89a-4cfe-813f-5f86df1cb37f";
        String regex = "^[\\w]{8}-([\\w]{4}-){3}[\\w]{12}$";

        return  result.matches(regex) ? result : null;
    }
}