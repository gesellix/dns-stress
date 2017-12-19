package de.gesellix.dnsstress;

public interface MessagePublisher {

  void publish(String message, String details);
}
