import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * This class implements the remote interface
 * CallbackClientInterface.
 *
 * @author M. L. Liu
 */

public class CallbackClientImpl extends UnicastRemoteObject
        implements CallbackClientInterface, Runnable {
    //BorderLayoutExample layoutExample;
    JTextArea textArea;
    Thread thread;

    public void setTextArea(JTextArea textArea) throws RemoteException {
        this.textArea = textArea;
    }

    public CallbackClientImpl() throws RemoteException {
        super();
        //layoutExample = new BorderLayoutExample();
        thread = new Thread(this);
        thread.start();

    }

    public String notifyMe(String message) {
        String returnMessage = "Call back received: " + message;
        System.out.println(returnMessage);
        textArea.append(message);
        return returnMessage;
    }

    public void run() {
        //layoutExample.setVisible(true);

    }

}// end CallbackClientImpl class
