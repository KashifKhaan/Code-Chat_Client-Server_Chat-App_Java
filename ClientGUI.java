import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
public class ClientGUI extends JFrame {

    private JTextPane chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    public ClientGUI() {
        setTitle("Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14)); // Adjust the font size as needed

        // Create a custom document to apply different colors to messages
        StyledDocument styledDocument = chatArea.getStyledDocument();
        Style regularStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style incomingMessageStyle = styledDocument.addStyle("incoming", regularStyle);
        StyleConstants.setForeground(incomingMessageStyle, Color.BLUE);
        Style outgoingMessageStyle = styledDocument.addStyle("outgoing", regularStyle);
        StyleConstants.setForeground(outgoingMessageStyle, Color.RED);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14)); // Adjust the font size and style as needed
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        setVisible(true);

        try {
            socket = new Socket("127.0.0.1", 7778);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            startReading();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReading() {
        // thread
        Runnable r1 = () -> {
            appendToChatArea("READER STARTED....", "regular");

            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        appendToChatArea("SERVER TERMINATED", "regular");
                        socket.close();
                        break;
                    }
                    appendToChatArea("Server: " + msg, "incoming");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(r1).start();
    }

    private void sendMessage() {
        String content = inputField.getText();
        out.println(content);
        out.flush();
        inputField.setText("");

        if (content.equals("exit")) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            appendToChatArea("Client: " + content, "outgoing");
        }
    }

    private void appendToChatArea(String message, String style) {
        StyledDocument styledDocument = chatArea.getStyledDocument();
        try {
            styledDocument.insertString(styledDocument.getLength(), message + "\n", styledDocument.getStyle(style));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });
    }
}

