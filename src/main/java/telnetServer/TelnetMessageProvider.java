package telnetServer;

/**
 *
 */
public interface TelnetMessageProvider {

    String getTelnetMessage(int waitTime) throws InterruptedException;

}
