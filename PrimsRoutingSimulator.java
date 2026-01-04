import  javax.swing.*;
import java.awt.*;

public class PrimsRoutingSimulator extends JFrame {
    //these are GUI components
     final CoreGraphHandler graph;
     final JButton addNodeButton, removeNodeButton, addEdgeButton, removeEdgeButton;
     final JButton runPrimsButton;
     final JTextArea logArea;

    public PrimsRoutingSimulator() {
        //  1: Routing Algorithm Simulator Features
        // GUI setup for visualizing and controlling routing simulations

        setTitle(" Prim's Routing Simulator ");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Center panel - Graph drawing area
        graph = new CoreGraphHandler();
        graph.setBackground(new Color(20, 25, 35));
        add(graph, BorderLayout.CENTER);

        // (Sidebar) control panel where buttons are kept
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 35, 45));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        controlPanel.setPreferredSize(new Dimension(300, 0));

        // Button style settings
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
        Dimension smallButtonSize = new Dimension(120, 40);
        Dimension largeButtonSize = new Dimension(240, 45);

        // Nodes functions buttons
        JPanel nodePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        nodePanel.setOpaque(false);
        addNodeButton = createStyledButton("Add node", buttonFont);
        removeNodeButton = createStyledButton("Remove node", buttonFont);
        addNodeButton.setPreferredSize(smallButtonSize);
        removeNodeButton.setPreferredSize(smallButtonSize);
        nodePanel.add(addNodeButton);
        nodePanel.add(removeNodeButton);
        controlPanel.add(nodePanel);
        controlPanel.add(Box.createVerticalStrut(15));

        // Edge control buttons
        JPanel edgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        edgePanel.setOpaque(false);
        addEdgeButton = createStyledButton("Add edge", buttonFont);
        removeEdgeButton = createStyledButton("Remove edge", buttonFont);
        addEdgeButton.setPreferredSize(smallButtonSize);
        removeEdgeButton.setPreferredSize(smallButtonSize);
        edgePanel.add(addEdgeButton);
        edgePanel.add(removeEdgeButton);
        controlPanel.add(edgePanel);
        controlPanel.add(Box.createVerticalStrut(20));

        // Main action buttons

        //  3: Provide a way to select the source(via button)
        JButton selectSourceButton = createStyledButton("Select source", buttonFont);

        //  4: Implement and simulate the chosen routing algorithm (via button)
        runPrimsButton = createStyledButton("Run prims", buttonFont);
        JButton clearAllButton = createStyledButton("Clear all", buttonFont);

        selectSourceButton.setPreferredSize(largeButtonSize);
        runPrimsButton.setPreferredSize(largeButtonSize);
        clearAllButton.setPreferredSize(largeButtonSize);

        controlPanel.add(centered(selectSourceButton));
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(centered(runPrimsButton));
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(centered(clearAllButton));
        controlPanel.add(Box.createVerticalGlue());

        add(controlPanel, BorderLayout.WEST);

        //  8: Log routing decisions and messages exchanged(console area setup)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(15, 20, 30));
        logArea.setForeground(new Color(100, 200, 255));
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 60, 80)));
        scrollPane.setPreferredSize(new Dimension(0, 220));
        add(scrollPane, BorderLayout.SOUTH);

        graph.setLogArea(logArea);
       //action buttons
        addNodeButton.addActionListener(e -> graph.addNode());
removeNodeButton.addActionListener(e -> graph.enableDeleteMode());
addEdgeButton.addActionListener(e -> graph.enableEdgeAddingMode());
removeEdgeButton.addActionListener(e -> graph.enableEdgeDeleteMode());
selectSourceButton.addActionListener(e -> graph.enableSourceSelectionMode());

runPrimsButton.addActionListener(e -> {
    // Add your Prim's algorithm logic here

            if (graph.getSelectedSourceIndex() == -1) {
                JOptionPane.showMessageDialog(this, "⚠️ Please select a source node first.", "Source Not Selected", JOptionPane.WARNING_MESSAGE);
                graph.enableSourceSelectionMode();
            } else if (!graph.isGraphConnected()) {
                JOptionPane.showMessageDialog(this, "⚠️ The graph is disconnected. Please connect all nodes first.", "Disconnected Graph", JOptionPane.WARNING_MESSAGE);
            } else {
                graph.runPrimsAnimated(graph.getSelectedSourceIndex());
            }
        });

        clearAllButton.addActionListener(e -> {
            graph.resetGraph();
            logArea.setText("");
        });

        // Welcome Message Dialog (JDialog with glowing button)
        showWelcomeDialog();
    }
    // Welcome popup with instructions
private void showWelcomeDialog() {
    JDialog welcomeDialog = new JDialog(this, "🤝𝐖𝐄𝐋𝐂𝐎𝐌𝐄", true);
    welcomeDialog.setUndecorated(true);
    welcomeDialog.setSize(500, 420);
    welcomeDialog.setLocationRelativeTo(this);
    welcomeDialog.setLayout(new BorderLayout());
    welcomeDialog.getContentPane().setBackground(new Color(70, 130, 169)); //rgb(6, 90, 146)
    welcomeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    JTextPane welcomeText = new JTextPane();
    welcomeText.setContentType("text/html");
welcomeText.setText(
    "<html><body style='font-family:Segoe UI; font-size:14px; color:white;'>"
  + "<div style='text-align:center;'>"
  + "<div style='display:inline-block; padding:12px 30px; border-radius:60px; "
  + "border: 3px solid #00FF FF; box-shadow: 0 0 12px #00FF FF; margin-bottom:15px;'>"
  + "<h2 style='color:white; margin:0;'>🤝 <b>𝐖el𝐂ome to Prim's Routing Simulator</b></h2>"
  + "</div>"
  + "</div>"

  + "<ul style='margin-top:10px; color:white; list-style-type: none; padding-left: 5px;'>"
+ "<li><b style='color:white;'>🟢 Add Node:</b> Keep clicking the <i>Add Node</i> button to insert nodes</li>"
  + "<li><b style='color:white;'>➕ Add Edge:</b> Click two nodes</li>"
  + "<li><b style='color:white;'>✂️ Delete Node:</b> Click a node after selecting <i>Remove Node</i></li>"
  + "<li><b style='color:white;'>✂️ Delete Edge:</b> Click on an edge after selecting <i>Remove Edge</i></li>"
  + "<li><b style='color:white;'>📝 Update Weight:</b> Double-click the weight number</li>"
  + "<li><b style='color:white;'>🛑 Deactivate Buttons:</b> Click anywhere on the screen</li>"
  + "</ul>"

  + "<p style='margin-top:15px; font-weight:bold; text-align:center; color:white;'>"
  + " :) Start building your graph now!</p>"

  + "</body></html>"
);

    welcomeText.setEditable(false);
    welcomeText.setOpaque(false);
    welcomeText.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
    welcomeDialog.add(welcomeText, BorderLayout.CENTER);

    JButton okButton = createStyledButton(" OK, Let's Go!", new Font("Segoe UI", Font.BOLD, 14));
    okButton.addActionListener(e -> welcomeDialog.dispose());

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(new Color(70, 130, 169));
    buttonPanel.add(okButton);
    welcomeDialog.add(buttonPanel, BorderLayout.SOUTH);

    welcomeDialog.setVisible(true);
}
  //glowing buttons
    private JButton createStyledButton(String text, Font font) {
        JButton button = new JButton(text) {
            private float pulseAlpha = 0.4f;
            private boolean increasing = true;
            final Timer timer;

            {
                // Glowing animation effect
                timer = new Timer(50, e -> {
                    if (increasing) {
                        pulseAlpha += 0.02f;
                        if (pulseAlpha >= 1.0f) {
                            pulseAlpha = 1.0f;
                            increasing = false;
                        }
                    } else {
                        pulseAlpha -= 0.02f;
                        if (pulseAlpha <= 0.4f) {
                            pulseAlpha = 0.4f;
                            increasing = true;
                        }
                    }
                    repaint();
                });
                timer.start();
            }

            //for neon  buttons, bg and borders
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();

                Color color1 = getModel().isPressed() ? new Color(70, 150, 255)
                        : getModel().isRollover() ? new Color(0, 200, 255)
                        : new Color(110, 130, 160);
                Color color2 = getModel().isPressed() ? new Color(20, 90, 180)
                        : getModel().isRollover() ? new Color(0, 150, 230)
                        : new Color(70, 90, 130);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, width, height, 40, 40);

                g2.setColor(new Color(0, 255, 255, (int) (pulseAlpha * 100)));
                g2.setStroke(new BasicStroke(5f));
                g2.drawRoundRect(1, 1, width - 3, height - 3, 40, 40);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(0, 255, 255, 180));
                    g2.setStroke(new BasicStroke(6f));
                    g2.drawRoundRect(1, 1, width - 3, height - 3, 40, 40);

                    GradientPaint shine = new GradientPaint(0, 0, new Color(255, 255, 255, 100),
                            0, (float) height / 2, new Color(255, 255, 255, 0));
                    g2.setPaint(shine);
                    g2.fillRoundRect(5, 5, width - 10, height / 2, 40, 40);
                }

                g2.setColor(new Color(0, 255, 255));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, width - 1, height - 1, 40, 40);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(font);
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel centered(Component c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBackground(new Color(30, 35, 45));
        p.add(c);
        return p;
    }
//main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PrimsRoutingSimulator frame = new PrimsRoutingSimulator();
            frame.setVisible(true);
        });
    }
}
