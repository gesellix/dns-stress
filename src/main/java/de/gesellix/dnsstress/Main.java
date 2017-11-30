package de.gesellix.dnsstress;

import java.security.Security;
import java.util.Timer;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {

  private static String slackWebhookUrl = System.getenv("SLACK_WEBHOOK_URL");

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

    Security.setProperty("networkaddress.cache.negative.ttl", "0");
    System.out.printf("Implementation negative DNS TTL for JVM is %d seconds\n", sun.net.InetAddressCachePolicy.getNegative());

//    Security.setProperty("networkaddress.cache.ttl", "60");
    System.out.printf("Implementation DNS TTL for JVM is %d seconds\n", sun.net.InetAddressCachePolicy.get());

    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new LookupTask(hostname, slackWebhookUrl), 0, interval);
  }
}
