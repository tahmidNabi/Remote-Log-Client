import javax.naming.directory.SearchControls;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents the object client for a
 * distributed object of class CallbackServerImpl,
 * which implements the remote interface
 * CallbackServerInterface.  It also accepts callback
 * from the server.
 *
 * @author M. L. Liu
 */

public class CallbackClient extends JFrame {

    JTextArea inputTextArea = new JTextArea();
    JTextArea outputTextArea = new JTextArea();
    JLabel statusBar;
    CallbackClientInterface callbackObj;
    CallbackServerInterface callbackServer;
    JTextField searchField;
    boolean isConnected = false;

    public CallbackServerInterface getCallbackServer() {
        return callbackServer;
    }

    public void setCallbackServer(CallbackServerInterface callbackServer) {
        this.callbackServer = callbackServer;
    }

    public JTextArea getInputTextArea() {
        return inputTextArea;
    }

    public CallbackClient() {

        initUI();
    }

    public final void initUI() {

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem registerMenuItem = fileMenu.add("Register");
        registerMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerCommand();
            }
        });

        JMenuItem unRegisterMenuItem = fileMenu.add("Unregister");
        unRegisterMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unregisterCommand();
            }
        });


        JMenuItem exitMenuItem = fileMenu.add("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unregisterCommand();
                System.exit(0);
            }
        });


        menuBar.add(fileMenu);
        setJMenuBar(menuBar);


        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        searchField = new JTextField();
        toolbar.add(searchField);


        // ImageIcon exitMenuItem = new ImageIcon("exitMenuItem.png");
        JButton searchButton = new JButton("Search");
        //   registerButton.setBorder(new EmptyBorder(0, 0, 0, 0));


        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //registerCommand();
                searchCommand();
                //outputTextArea.setText(inputTextArea.getText());
                System.out.println("Search Command Execute");
            }
        });


        toolbar.add(searchButton);


        add(toolbar, BorderLayout.NORTH);
        /*
        JToolBar vertical = new JToolBar(JToolBar.VERTICAL);
        vertical.setFloatable(false);
        vertical.setMargin(new Insets(10, 5, 5, 5));

        ImageIcon select = new ImageIcon("drive.png");
        ImageIcon freehand = new ImageIcon("computer.png");
        ImageIcon shapeed = new ImageIcon("printer.png");

        JButton selectb = new JButton(select);
        selectb.setBorder(new EmptyBorder(3, 0, 3, 0));

        JButton freehandb = new JButton(freehand);
        freehandb.setBorder(new EmptyBorder(3, 0, 3, 0));
        JButton shapeedb = new JButton(shapeed);
        shapeedb.setBorder(new EmptyBorder(3, 0, 3, 0));

        vertical.add(selectb);
        vertical.add(freehandb);
        vertical.add(shapeedb);

        add(vertical, BorderLayout.WEST);
          */

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JScrollPane inputScrollPane = new JScrollPane(getInputTextArea());

        textPanel.add(inputScrollPane);

        JLabel searchLabel = new JLabel("Search Result");
        searchLabel.setPreferredSize(new Dimension(-1, 22));
        searchLabel.setBorder(LineBorder.createGrayLineBorder());
        textPanel.add(searchLabel);

        JScrollPane outputJScrollPane = new JScrollPane(outputTextArea);

        textPanel.add(outputJScrollPane);
        Dimension dimension = new Dimension(100,20);
        inputScrollPane.setPreferredSize(dimension);
        outputJScrollPane.setPreferredSize(dimension);

        this.add(textPanel);

        statusBar = new JLabel("Status");
        statusBar.setPreferredSize(new Dimension(-1, 22));
        statusBar.setBorder(LineBorder.createGrayLineBorder());
        add(statusBar, BorderLayout.SOUTH);

        setSize(500, 500);
        setTitle("Therap Log Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        this.addWindowListener(new WindowAdapter() {
            //
            // Invoked when a window has been opened.
            //
            public void windowOpened(WindowEvent e) {
                System.out.println("Window Opened Event");
                registerCommand();
            }

            //
            // Invoked when a window is in the process of being closed.
            // The close operation can be overridden at this point.
            //
            public void windowClosing(WindowEvent e) {
                System.out.println("Window Closing Event");
                unregisterCommand();
            }

            //
            // Invoked when a window has been closed.
            //
            public void windowClosed(WindowEvent e) {
                System.out.println("Window Close Event");


            }
        });


    }

    private void unregisterCommand() {
        if (isConnected == false)
            return;
        try {
            callbackServer.unregisterForCallback(callbackObj);
            statusBar.setText("Disconnected");
            isConnected = false;
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        System.out.println("Unregistered for callback.");
    }

    private void registerCommand() {
        if (isConnected == true)
            return;

        try {
            Properties properties;
            properties = new Properties();

            properties.load(new FileInputStream("client.properties"));
            String port = properties.getProperty("port");
            String hostName = properties.getProperty("hostName");
            String serviceName = properties.getProperty("serviceName");
            String registryURL = "rmi://" + hostName + ":" + port + "/" + serviceName;
            // find the remote object and cast it to an
            //   interface object
            callbackServer = (CallbackServerInterface) Naming.lookup(registryURL);
            System.out.println("Lookup completed ");
            System.out.println("Server said " + callbackServer.sayHello());
            callbackObj = new CallbackClientImpl();
            callbackObj.setTextArea(inputTextArea);
            // register for callback
            callbackServer.registerForCallback(callbackObj);
            isConnected = true;
            statusBar.setText("Connected");
        } catch (Exception ex) {
            statusBar.setText("Disconnected");
            throw new RuntimeException(ex);

        }
    }

    public void searchCommand() {

        outputTextArea.setText("");

        String regExString = searchField.getText();
        String searchText = inputTextArea.getText();
        //String resultText = "";
        Pattern linePattern= Pattern.compile(".*\r?\n");
        Pattern pattern = Pattern.compile(regExString);

        Matcher lineMatcher = linePattern.matcher(searchText);



        while(lineMatcher.find()) {
            String line = lineMatcher.group().trim();

            Matcher patternMatcher = pattern.matcher(line);

            if (patternMatcher.find()) {
                outputTextArea.append(line);
            }



        }



    }

    public static void main(String args[]) {
        try {
            final CallbackClient callbackClient = new CallbackClient();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    callbackClient.setVisible(true);
                }
            });
        } // end try
        catch (Exception e) {
            System.out.println(
                    "Exception in CallbackClient: " + e);
        }


    } //end main
}//end class
