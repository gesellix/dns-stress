package de.gesellix.dnsstress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: java -jar app.jar <interval seconds> <hostname String>");
      System.exit(1);
    }
    long interval = SECONDS.toMillis(Long.parseLong(args[0]));
    String hostname = args[1];
//    String hostname = "elasticsearch-http.vorgangsmanagement.europace.local";

    System.out.printf("Implementation DNS TTL for JVM is %d seconds\n", sun.net.InetAddressCachePolicy.get());

    TimerTask lookupTask = new TimerTask() {
      @Override
      public void run() {
        try {
          System.out.printf("Trying to lookup '%s'...\n", hostname);
          InetAddress address = InetAddress.getByName(hostname);
          System.out.printf("Resolved to '%s'\n", address.toString());
        }
        catch (UnknownHostException e) {
          System.err.println("Lookup failed");
          e.printStackTrace();
        }
      }
    };

    Timer timer = new Timer();
    timer.scheduleAtFixedRate(lookupTask, 0, interval);
  }
}
