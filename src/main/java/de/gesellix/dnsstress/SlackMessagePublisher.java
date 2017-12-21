package de.gesellix.dnsstress;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class SlackMessagePublisher implements MessagePublisher {

  private final MediaType TEXT = MediaType.parse("application/x-www-form-urlencoded");
  private final OkHttpClient client = new OkHttpClient();

  private String slackWebhookUrl;

  public SlackMessagePublisher(String slackWebhookUrl) {
    this.slackWebhookUrl = slackWebhookUrl;
  }

  @Override
  public void publish(String message, String details) {
    if (slackWebhookUrl != null && !slackWebhookUrl.isEmpty()) {
      try {
        JSONObject payload = new JSONObject()
            .put("attachments", new JSONArray()
                .put(new JSONObject()
                         .put("title", "Lookup failed")
//                         .put("pretext", message)
                         .put("text", details)
                         .put("mrkdwn_in", Arrays.asList("pretext", "text"))
                         .put("parse", "full")))
            .put("channel", "#pku_unknownhosts")
            .put("username", "webhookbot")
            .put("icon_emoji", ":ghost:")
            .put("text", "@channel " + message)
            .put("link_names", 1);
        String res = post(slackWebhookUrl, "payload=" + payload.toString());
        System.out.println("res:" + res);
      }
      catch (IOException ioE) {
        System.err.println("Failed to send Slack notification.");
        ioE.printStackTrace();
      }
    }
  }

  private String post(String url, String payload) throws IOException {
    RequestBody body = RequestBody.create(TEXT, payload);
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    Response response = client.newCall(request).execute();
    return response.body().string();
  }
}
