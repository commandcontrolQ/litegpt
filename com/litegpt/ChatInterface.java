package com.litegpt;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatInterface extends JFrame {
    private JTextField inputField;
    private JButton sendButton;
    private JEditorPane chatPane;
    private StringBuilder chatHistory;
    private int fontSize = 14;
	private String os = System.getProperty("os.name");
    private Runtime rt = Runtime.getRuntime();

    ChatInterface() {
        setTitle("LiteGPT build 809");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatHistory = new StringBuilder();

        chatPane = new JEditorPane();
        chatPane.setContentType("text/html");
        chatPane.setEditable(false);
        chatPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));

        // Open clicked links with XP-safe fallback
		chatPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent e) {
                if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ChatInterface.this,
                                "Unable to open link: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(chatPane);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Menu bar setup
        JMenuBar menuBar = new JMenuBar();

        JMenu menuMenu = new JMenu("Menu");
        JMenuItem saveItem = new JMenuItem("Save Chat");
        JMenuItem newItem = new JMenuItem("New Chat");
        JMenuItem exitItem = new JMenuItem("Exit");

        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveChat();
            }
        });

        newItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chatHistory.setLength(0);
                chatPane.setText("<html><body></body></html>");
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuMenu.add(saveItem);
        menuMenu.add(newItem);
        menuMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About LiteGPT");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        ChatInterface.this,
                        "This is LiteGPT build 809 on " + os + "\n" + 
                        "Lightweight open-source ChatGPT client built for Java 6\n" +
                        "This applet makes use of the ch.at project, which you can check out here:\n" +
                        "https://github.com/Deep-ai-inc/ch.at",
                        "About",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
		
		System.out.println(System.getProperty("os.name"));

        helpMenu.add(aboutItem);

        menuBar.add(menuMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
		
		playSound("/com/litegpt/alert.wav");
		JOptionPane.showMessageDialog(
			ChatInterface.this,
			"This is a testing build of LiteGPT and is therefore unstable.\n" +
			"The current functionality may not entirely represent stable builds,\n" +
			"and there is a chance that bugs can occur during standard use.\n" +
			"To report any issues see https://github.com/commandcontrolQ/litegpt",
			"Build Warning",
			JOptionPane.WARNING_MESSAGE
		);

        setVisible(true);
    }
	
	private void playSound(String resourcePath) {
    try {
        java.net.URL soundURL = getClass().getResource(resourcePath);
        if (soundURL == null) {
            System.err.println("Sound file not found: " + resourcePath);
            return;
        }

        javax.sound.sampled.AudioInputStream audioIn =
            javax.sound.sampled.AudioSystem.getAudioInputStream(soundURL);

        javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void sendMessage() {
        final String prompt = inputField.getText().trim();
        if (prompt.isEmpty()) return;
        inputField.setText("");
		
		playSound("/com/litegpt/sent.wav");
        chatHistory.append("<b>You:</b> ").append(escapeHTML(prompt)).append("<br>");
        chatPane.setText("<html><body style='font-family:sans-serif; font-size:" + fontSize + "pt;'>"
                + chatHistory.toString() + "</body></html>");

        final String fullPrompt = "Convert any LaTeX formatting in your response to plain text.\n" + prompt;

        new Thread(new Runnable() {
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader in = null;
                try {
                    String encodedPrompt = URLEncoder.encode(fullPrompt, "UTF-8");
                    URL url = new URL("http://ch.at/?q=" + encodedPrompt);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
					// Setting Accept to text/plain is crucial
                    conn.setRequestProperty("Accept", "text/plain");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);

                    in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line).append("\n");
                    }

                    String rawAnswer = extractAnswer(response.toString());
                    String formattedAnswer = markdownToHTML(rawAnswer);
					
					playSound("/com/litegpt/received.wav");
                    chatHistory.append("<b>GPT:</b> ").append(formattedAnswer).append("<br>");
                    chatPane.setText("<html><body style='font-family:sans-serif; font-size:" + fontSize + "pt;'>"
                            + chatHistory.toString() + "</body></html>");
                } catch (Exception ex) {
					playSound("/com/litegpt/alert.wav");
                    chatHistory.append("<font color='red'>LiteGPT encountered an error: ").append(escapeHTML(ex.getMessage())).append("</font><br>");
                    chatPane.setText("<html><body style='font-family:sans-serif; font-size:" + fontSize + "pt;'>"
                            + chatHistory.toString() + "</body></html>");
                } finally {
                    if (in != null) {
                        try { in.close(); } catch (IOException ex) { ex.printStackTrace(); }
                    }
                    if (conn != null) conn.disconnect();
                }
            }
        }).start();
    }

    private String extractAnswer(String text) {
        int aIndex = text.indexOf("A:");
        if (aIndex != -1) {
            return text.substring(aIndex + 2).trim();
        }
        return text.trim();
    }

    private String markdownToHTML(String text) {
        String html = escapeHTML(text);
        html = html.replaceAll("(?s)```(.*?)```", "<pre><code>$1</code></pre>");
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        html = html.replaceAll("(?<!\\*)\\*(?!\\*)(.*?)\\*(?!\\*)", "<i>$1</i>");
        html = html.replaceAll("_(.*?)_", "<i>$1</i>");
        html = html.replaceAll("__(.*?)__", "<u>$1</u>");
        html = html.replaceAll("~~(.*?)~~", "<strike>$1</strike>");
        html = html.replaceAll("`([^`]+)`", "<code>$1</code>");
        html = convertLists(html);
		
		// Convert Markdown links to <a>
        html = html.replaceAll("\\[([^\\]]+)\\]\\((https?://[^)]+)\\)", "<a href=\"$2\">$1</a>");
        
		// Directly click HTTP links (experimental)
		// The current implementation fails when the URL is encased in certain characters, this is a known bug
		// html = html.replaceAll("https?:\\/\\/[^\\s\"'<>()\\[\\]{}]+(?=[\\s\"'<>.,!?()\\[\\]{}]|$)", "<a href=\"$0\">$0</a>");
		
        html = html.replaceAll("(?<!</pre>)\\r?\\n", "<br>");
        return html;
    }

    private String convertLists(String text) {
        String[] lines = text.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        boolean inList = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.matches("^\\s*([-*])\\s+.*")) {
                if (!inList) {
                    sb.append("<ul>");
                    inList = true;
                }
                String item = line.replaceFirst("^\\s*([-*])\\s+", "");
                sb.append("<li>").append(item).append("</li>");
            } else {
                if (inList) {
                    sb.append("</ul>");
                    inList = false;
                }
                sb.append(line).append("<br>");
            }
        }
        if (inList) sb.append("</ul>");
        return sb.toString();
    }

    private String escapeHTML(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<': out.append("&lt;"); break;
                case '>': out.append("&gt;"); break;
                case '&': out.append("&amp;"); break;
                case '"': out.append("&quot;"); break;
                default: out.append(c);
            }
        }
        return out.toString();
    }

    private void saveChat() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            PrintWriter out = null;
            try {
                out = new PrintWriter(file, "UTF-8");
                out.print(chatHistory.toString());
            } catch (IOException e) {
				playSound("/com/litegpt/alert.wav");
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (out != null) out.close();
            }
        }
    }

    private void openLinkInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                    return;
                }
            }
        } catch (Exception ex) {
            // fallback
        }

        String oslower = os.toLowerCase();
        try {
            if (oslower.contains("win")) {
                rt.exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", url });
            } else if (oslower.contains("mac")) {
                rt.exec(new String[] { "open", url });
            } else {
                rt.exec(new String[] { "xdg-open", url });
            }
        } catch (IOException e) {
			playSound("/com/litegpt/alert.wav");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Unable to open link: " + url,
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
