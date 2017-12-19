package de.gesellix.dnsstress;

public class ConsoleMessagePublisher implements MessagePublisher {
  @Override
  public void publish(String message, String details) {
    System.err.println("message: " + message + ", details: " + details);
  }
}
