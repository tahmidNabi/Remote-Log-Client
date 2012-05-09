import javax.swing.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is a remote interface for illustrating RMI
 * client callback.
 * @author M. L. Liu
 */

public interface CallbackClientInterface
  extends Remote{
  // This remote method is invoked by a callback
  // server to make a callback to an client which
  // implements this interface.
  // @param message - a string containing information for the
  //                  client to process upon being called back.

    public void setTextArea(JTextArea textArea) throws RemoteException;
    public String notifyMe(String message)
      throws RemoteException;

} // end interface
