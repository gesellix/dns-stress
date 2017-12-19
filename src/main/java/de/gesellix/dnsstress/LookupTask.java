package de.gesellix.dnsstress;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;

class LookupTask extends TimerTask {

  private String ownHost;
  private String hostname;
  private MessagePublisher publisher;

  LookupTask(String ownHost, String hostname, String slackWebhookUrl) {
    this.ownHost = ownHost == null || ownHost.isEmpty() ? "UNKNOWN" : ownHost;
    this.hostname = hostname;
    this.publisher = new SlackMessagePublisher(slackWebhookUrl);
  }

  public void setPublisher(MessagePublisher publisher) {
    this.publisher = publisher;
  }

  @Override
  public void run() {
    Stopwatch watch = new Stopwatch();
    String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
    try {
      System.out.printf("Trying to lookup '%s'...\n", hostname);
      InetAddress address = InetAddress.getByName(hostname);
      System.out.printf("Resolved to '%s'\n", address.toString());
    }
    catch (UnknownHostException e) {
      e.printStackTrace();
      System.err.printf("%s Lookup failed\n", timestamp);

      publisher.publish("Lookup failed for " + hostname + " from " + ownHost + ".",
                        "- elapsed seconds: " + watch.elapsedDuration().getSeconds() + "\n" +
                        "- contents of /etc/hosts:\n" +
                        "```\n" +
                        "" + getFileContents("/etc/hosts") + "\n" +
                        "```\n" +
                        "- contents of /etc/resolv.conf:\n" +
                        "```\n" +
                        "" + getFileContents("/etc/resolv.conf") + "\n" +
                        "```\n");
    }
  }

  private String getFileContents(String filename) {
    try {
      StringBuilder contents = new StringBuilder();
      Files.lines(Paths.get(filename), StandardCharsets.UTF_8)
          .map((line) -> line + "\n")
          .forEach(contents::append);
      return contents.toString();
    }
    catch (IOException ioExc) {
      ioExc.printStackTrace();
      return "Couldn't read from " + filename + " (" + ioExc.getMessage() + ")";
    }
  }

  static class Stopwatch {
    private long start = System.currentTimeMillis();

    Duration elapsedDuration() {
      return Duration.ofMillis(System.currentTimeMillis() - start);
    }
  }
}
