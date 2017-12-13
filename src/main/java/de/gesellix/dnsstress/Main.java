package de.gesellix.dnsstress;

import java.security.Security;
import java.util.Timer;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {

  private static String slackWebhookUrl = System.getenv("SLACK_WEBHOOK_URL");
  private static String ownHost = System.getenv("OWN_HOST");

  public static void main(String[] args) {
    long intervalSeconds = 1;
    String hostname = "foo.pku.europace.local";

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

    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new LookupTask(ownHost, hostname, slackWebhookUrl), 0, interval);
  }
}
