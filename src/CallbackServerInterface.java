import java.rmi.Remote;

/**
 * This is a remote interface for illustrating RMI
 * client callback.
 *
 * @author M. L. Liu
 */

public interface CallbackServerInterface extends Remote {


    public String sayHello()
            throws java.rmi.RemoteException;

// This remote method allows an object client to
// register for callback
// @param callbackClientObject is a reference to the
//        object of the client; to be used by the server
//        to make its callbacks.

    public void registerForCallback(
            CallbackClientInterface callbackClientObject
    ) throws java.rmi.RemoteException;

// This remote method allows an object client to
// cancel its registration for callback

    public void unregisterForCallback(
            CallbackClientInterface callbackClientObject)
            throws java.rmi.RemoteException;

    public void doCallbacks(String message) throws java.rmi.RemoteException;
}
