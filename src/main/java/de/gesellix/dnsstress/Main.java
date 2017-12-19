package de.gesellix.dnsstress;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Security;
import java.util.Properties;
import java.util.Timer;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {

  private static String slackWebhookUrl = System.getenv("SLACK_WEBHOOK_URL");
  private static String ownHost = System.getenv("OWN_HOST");

  public static void main(String[] args) throws IOException {
    long intervalSeconds = 1;
    String hostname = "foo.example.local";

    if (args.length == 0) {
      System.out.println("Usage: ./bin/dns-stress <interval seconds> <hostname string>");
      System.out.printf("Using defaults (%s, %s)\n", intervalSeconds, hostname);
    }
    else {
      intervalSeconds = Long.parseLong(args[0]);
      hostname = args[1];
    }
    long interval = SECONDS.toMillis(intervalSeconds);

    Security.setProperty("networkaddress.cache.negative.ttl", "0");
    Security.setProperty("networkaddress.cache.ttl", "10");

    System.out.printf("Implementation negative DNS TTL for JVM is %d seconds\n", sun.net.InetAddressCachePolicy.getNegative());
    System.out.printf("Implementation DNS TTL for JVM is %d seconds\n", sun.net.InetAddressCachePolicy.get());

    if ((slackWebhookUrl == null || slackWebhookUrl.isEmpty()) && new File("secrets-env.properties").isFile()) {
      Properties properties = new Properties();
      properties.load(new FileReader("secrets-env.properties"));
      slackWebhookUrl = properties.getProperty("SLACK_WEBHOOK_URL");
    }

    Timer timer = new Timer();
    LookupTask lookupTask = new LookupTask(ownHost, hostname, slackWebhookUrl);
//    lookupTask.setPublisher(new ConsoleMessagePublisher());
    timer.scheduleAtFixedRate(lookupTask, 0, interval);
  }
}
