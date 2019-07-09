package uk.co.tarbits.network.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.tarbits.network.pipeline.Session;
import uk.co.tarbits.network.protocol.IMessage;

/**
 * Thrown when no message consumer is found.
 * 
 * @author Joseph Tarbit
 *
 */
@RequiredArgsConstructor
@Getter
public class UnhandledMessageException extends Exception {

  private static final long serialVersionUID = -7661305251257254588L;

  private final IMessage unhandledMessage;
  private final Session session;
}
