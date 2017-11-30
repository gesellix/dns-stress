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
import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {

  static String slackWebhookUrl = System.getenv("SLACK_WEBHOOK_URL");

  public static void main(String[] args) {
    long intervalSeconds = 1;
    String hostname = "elasticsearch-http.vorgangsmanagement.europace.localll";

    if (args.length == 0) {
      System.out.println("Usage: ./bin/dns-stress <interval seconds> <hostname String>");
      System.out.printf("Using defaults (%s, %s)\n", intervalSeconds, hostname);
    }
    else {
      intervalSeconds = Long.parseLong(args[0]);
      hostname = args[1];
    }
    long interval = SECONDS.toMillis(intervalSeconds);

    System.out.printf("Implementation DNS TTL for JVM is %d seconds\n", sun.net.InetAddressCachePolicy.get());

    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new LookupTask(hostname, slackWebhookUrl), 0, interval);
  }

  static class LookupTask extends TimerTask {

    private String hostname;
    private String slackWebhookUrl;

    public LookupTask(String hostname, String slackWebhookUrl) {
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
                              "\"text\": \"Lookup failed for " + hostname + ".\"," +
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

    final MediaType TEXT = MediaType.parse("application/x-www-form-urlencoded");

    final OkHttpClient client = new OkHttpClient();

    String post(String url, String payload) throws IOException {
      RequestBody body = RequestBody.create(TEXT, payload);
      Request request = new Request.Builder()
          .url(url)
          .post(body)
          .build();
      Response response = client.newCall(request).execute();
      return response.body().string();
    }
  }
}
