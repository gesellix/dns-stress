package de.gesellix.dnsstress;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;

class LookupTask extends TimerTask {

  private final MediaType TEXT = MediaType.parse("application/x-www-form-urlencoded");

  private final OkHttpClient client = new OkHttpClient();

  private String hostname;
  private String slackWebhookUrl;

  LookupTask(String hostname, String slackWebhookUrl) {
    this.hostname = hostname;
    this.slackWebhookUrl = slackWebhookUrl;
  }

  @Override
  public void run() {
    String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
    try {
      System.out.printf("Trying to lookup '%s'...\n", hostname);
      InetAddress address = InetAddress.getByName(hostname);
      System.out.printf("Resolved to '%s'\n", address.toString());
    }
    catch (UnknownHostException e) {
      System.err.printf("%s Lookup failed\n", timestamp);
      e.printStackTrace();

      if (slackWebhookUrl != null && !slackWebhookUrl.isEmpty()) {
        try {
          String res = post(slackWebhookUrl,
                            "payload={" +
                            "\"channel\": \"#pku_unknownhosts\"," +
                            "\"username\": \"webhookbot\"," +
                            "\"text\": \"@channel Lookup failed for " + hostname + ".\"," +
                            "\"parse\": \"full\"," +
                            "\"icon_emoji\": \":ghost:\"}");
          System.out.println("res:" + res);
        }
        catch (IOException ioE) {
          System.err.println("Failed to send Slack notification.");
          ioE.printStackTrace();
        }
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
