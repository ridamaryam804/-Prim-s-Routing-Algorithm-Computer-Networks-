import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;
import java.util.List;
import java.util.Queue;

public class CoreGraphHandler extends JPanel {
    //  2: Allow users to input or generate a graph with nodes and weighted edges
    private final ArrayList<Point> nodes = new ArrayList<>();
    private final ArrayList<Edge> edges = new ArrayList<>();
    private final ArrayList<Edge> mstEdges = new ArrayList<>();
    private final Map<String, Point> labelToPoint = new LinkedHashMap<>();
    private JTextArea logArea;
    private int nodeCounter = 0;
    private Point draggedNode = null;
    private int dragOffsetX, dragOffsetY;
    private boolean deleteMode = false;
    private boolean edgeAddMode = false;
    private boolean edgeDeleteMode = false;
    private boolean sourceSelectionMode = false;
    private Point selectedSource = null;
    private Point selectedEdgeStart = null;
    private Point currentHighlightedNode = null;

    public void setLogArea(JTextArea logArea) {
        this.logArea = logArea;
    }
    private void resetModes() {
    deleteMode = false;
    edgeAddMode = false;
    edgeDeleteMode = false;
    sourceSelectionMode = false;
}
    // 1 & 7. provide way to user to add nodes for graph ..... Allow changes in topology (add nodes)

    public void addNode() {
         resetModes();
        int x = 50 + (int)(Math.random() * (getWidth() - 100));
        int y = 50 + (int)(Math.random() * (getHeight() - 100));
        Point newNode = new Point(x, y);
        String label = "N" + nodeCounter++;
        nodes.add(newNode);
        labelToPoint.put(label, newNode);
         log(" Node " + label + " added."); //  8: Logging messages
        repaint();
    }
   /* This method only activates the edge adding mode by setting the `edgeaddmode` flag to true.
    The actual node deletion happens in `mousePressed()`*/

    public void enableEdgeAddingMode() {
        resetModes();
        edgeAddMode = true;
        selectedEdgeStart = null;
        deleteMode = false;
        log(" Select two nodes to add edge.");
    }

    /* Enables "Delete Node" mode.
     *  This method only activates the node deletion mode by setting the `deleteMode` flag to true.
     *  The actual node deletion happens in `mousePressed()` */

    public void enableDeleteMode() {
        resetModes();
        deleteMode = true;
        edgeAddMode = false;
        log(" Click a node to delete it..."); //  8: Logging
    }


    /* Enables "Delete Node" mode.
     *  This method only activates the edge deletion mode by setting the `edgedeleteMode` flag to true.
     *  The actual edge deletion happens in `mousePressed()` */

    public void enableEdgeDeleteMode() {
        resetModes();
        edgeDeleteMode = true;
        edgeAddMode = false;
        deleteMode = false;
        sourceSelectionMode = false;
        log(" Click on the edge to remove it...");//8. Log messages
    }

    /* Enables "Delete Node" mode.
     *  This method only activates the source selection mode by setting the `sourceselectionmode` flag to true.
     *  The actual selection happens in `mousePressed()` */
    public void enableSourceSelectionMode() {
        sourceSelectionMode = true;
        edgeAddMode = false;
        log(" Click on a node to set it as source.");//8. Log messages
    }

    public int getSelectedSourceIndex() {
        if (selectedSource == null) return -1;
        return new ArrayList<>(labelToPoint.values()).indexOf(selectedSource);
    }

    //  4: Implement and simulate the chosen routing algorithm
    //  6: Visualize the entire routing process (graph traversal, path selection)
    public void runPrimsAnimated(int sourceIndex) {


        mstEdges.clear();
        if (nodes.isEmpty() || sourceIndex < 0 || sourceIndex >= nodes.size()) return;

        Set<Point> visited = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        List<Point> nodeList = new ArrayList<>(labelToPoint.values());
        Point start = nodeList.get(sourceIndex);
        visited.add(start);
        currentHighlightedNode = start;

        for (Edge e : edges) {
            if (e.src.equals(start) || e.dest.equals(start)) pq.add(e);
        }

        repaint();

        Timer timer = new Timer(700, null);
        timer.addActionListener(e -> {
            if (visited.size() >= nodes.size() || pq.isEmpty()) {
                timer.stop();
                repaint();
                int totalWeight = mstEdges.stream().mapToInt(e -> e.weight).sum();

                // 5: Display the shortest/best path calculated by the algorithm
                log(" Total Weight: " + totalWeight);
                log(" Shortest Path (MST Edges):");
                for (Edge e : mstEdges) {
                    String from = getLabelByPoint(e.src);
                    String to = getLabelByPoint(e.dest);
                    log(" - " + from + " ↔ " + to + " (Weight: " + e.weight + ")"); // 8: Log routing decisions
                }
                return;
            }

            Edge edge = pq.poll();
            Point next = null;
            if (visited.contains(edge.src) && !visited.contains(edge.dest)) next = edge.dest;
            else if (visited.contains(edge.dest) && !visited.contains(edge.src)) next = edge.src;

            if (next != null) {
                visited.add(next);
                mstEdges.add(edge);
                currentHighlightedNode = next;
                for (Edge e : edges) {
                    if ((e.src.equals(next) && !visited.contains(e.dest)) ||
                            (e.dest.equals(next) && !visited.contains(e.src))) pq.add(e);
                }

                String from = getLabelByPoint(edge.src);
                String to = getLabelByPoint(edge.dest);
                log("🔹 " + from + " ↔ " + to + " selected"); // 8: Log routing decisions
                repaint();
            }
        });
        timer.start();
    }

    // 9. Handle edge cases  disconnected nodes
    public boolean isGraphConnected() {
        if (nodes.isEmpty()) return false;
        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();
        Point start = nodes.getFirst();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            for (Edge edge : edges) {
                if (edge.src.equals(current) && !visited.contains(edge.dest)) {
                    visited.add(edge.dest);
                    queue.add(edge.dest);
                } else if (edge.dest.equals(current) && !visited.contains(edge.src)) {
                    visited.add(edge.src);
                    queue.add(edge.src);
                }
            }
        }

        return visited.size() == nodes.size();
    }


 //clears window
    public void resetGraph() {
        nodes.clear();
        edges.clear();
        mstEdges.clear();
        labelToPoint.clear();
        nodeCounter = 0;
        selectedSource = null;
        repaint();
    }



    // Feature 8: Log area
    private void log(String msg) {
        if (logArea != null) logArea.append(msg + "\n");
    }

    private String getLabelByPoint(Point p) {
        for (Map.Entry<String, Point> entry : labelToPoint.entrySet()) {
            if (entry.getValue().equals(p)) return entry.getKey();
        }
        return "";
    }

    // 6: Visualize traversal and MST building process
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Edge e : edges) {
            g2d.setColor(new Color(120, 120, 120));
            g2d.drawLine(e.src.x, e.src.y, e.dest.x, e.dest.y);
            Point mid = new Point((e.src.x + e.dest.x) / 2, (e.src.y + e.dest.y) / 2);
            g2d.setColor(new Color(200, 230, 255));
            g2d.drawString(String.valueOf(e.weight), mid.x, mid.y);
        }

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(0, 180, 255));
        for (Edge e : mstEdges) {
            g2d.drawLine(e.src.x, e.src.y, e.dest.x, e.dest.y);
        }

        for (Map.Entry<String, Point> entry : labelToPoint.entrySet()) {
            Point p = entry.getValue();

            if (p.equals(currentHighlightedNode)) {
                g2d.setColor(new Color(255, 255, 0));
                g2d.setStroke(new BasicStroke(4f));
                g2d.drawOval(p.x - 20, p.y - 20, 40, 40);
            }

            GradientPaint gp = new GradientPaint(p.x - 15, p.y - 15, new Color(0, 160, 255), p.x + 15, p.y + 15, new Color(0, 100, 220));
            g2d.setPaint(gp);
            g2d.fillOval(p.x - 15, p.y - 15, 30, 30);
            g2d.setColor(new Color(200, 240, 255));
            g2d.drawOval(p.x - 15, p.y - 15, 30, 30);
            g2d.setColor(Color.WHITE);
            g2d.drawString(entry.getKey(), p.x - 10, p.y + 5);
        }
    }

    public CoreGraphHandler() {


        addMouseListener(new MouseAdapter() {

// this methods implements  and handles all the functions and control through mouse clicks
            @Override
            public void mousePressed(MouseEvent e) {
                Point clicked = e.getPoint();

                //   7: Allow changes in topology (Delete Node)
                if (deleteMode) {
                    String label = null;
                    for (Map.Entry<String, Point> entry : labelToPoint.entrySet()) {
                        if (entry.getValue().distance(clicked) < 15) {
                            label = entry.getKey();
                            break;
                        }
                    }

                    if (label != null) {
                        Point toDelete = labelToPoint.get(label);
                        nodes.remove(toDelete);
                        edges.removeIf(e1 -> e1.src.equals(toDelete) || e1.dest.equals(toDelete));
                        mstEdges.removeIf(e1 -> e1.src.equals(toDelete) || e1.dest.equals(toDelete));
                        labelToPoint.remove(label);
                        log(" Node " + label + " removed.");
                    } else {
                        deleteMode = false;
                        setCursor(Cursor.getDefaultCursor());
                        log(" Delete Mode Inactive. No node clicked.");
                    }
                    repaint();
                    return;
                }

                //  7: Allow changes in topology (Add edge)
                if (edgeAddMode) {
                    boolean clickedOnNode = false;
                    for (Point node : nodes) {
                        if (node.distance(clicked) < 15) {
                            clickedOnNode = true;
                            break;
                        }
                    }

                    if (!clickedOnNode) {
                        edgeAddMode = false;
                        selectedEdgeStart = null;
                        setCursor(Cursor.getDefaultCursor());
                        log(" Add Edge Mode Inactive.");
                        repaint();
                        return;
                    }
                }

                //  7: Allow changes in topology (Delete Edge)
                if (edgeDeleteMode) {
                    boolean clickedOnEdge = false;
                    for (Edge edge : edges) {
                        double dist = ptLineDist(edge.src, edge.dest, clicked);
                        if (dist < 7) {
                            clickedOnEdge = true;
                            break;
                        }
                    }

                    if (!clickedOnEdge) {
                        edgeDeleteMode = false;
                        setCursor(Cursor.getDefaultCursor());
                        log(" Remove Edge Mode Inactive.");
                        repaint();
                        return;
                    }
                }

                //  Edge Weight Update by clicking on the weight digits
                for (Edge edge : edges) {
                    Point mid = new Point((edge.src.x + edge.dest.x) / 2, (edge.src.y + edge.dest.y) / 2);
                    if (clicked.distance(mid) < 10) {
                        String newWeightStr = JOptionPane.showInputDialog("Update edge weight:", edge.weight);
                        try {
                            int newWeight = Integer.parseInt(newWeightStr);
                            if (newWeight == 0) {
                                //9.edge case handling invalid weight when user tries to update existing assigned weight
                                JOptionPane.showMessageDialog(null, "❌ Invalid weight! Weight cannot be zero.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                                log(" Edge weight cannot be zero.");
                            } else {
                                edge.weight = newWeight;
                                log(" Edge weight updated.");
                            }
                        } catch (Exception ex) {
                            log(" Invalid weight input.");
                        }
                        repaint();
                        return;
                    }
                }

                //   3: Provide a way to select the source node
                if (sourceSelectionMode) {
                    for (Point node : nodes) {
                        if (node.distance(clicked) < 15) {
                            selectedSource = node;
                            log(" Source node selected: " + getLabelByPoint(node)); //8. Log messages
                            sourceSelectionMode = false;
                            repaint();
                            return;
                        }
                    }
                    log(" No node clicked. Try again."); //8. Log messages
                    return;
                }

                // Feature 2 & 7: allow user adding weighted edges for graph and also allows changes in topology(edge add)
                if (edgeAddMode) {
                    for (Point node : nodes) {
                        if (node.distance(clicked) < 15) {
                            if (selectedEdgeStart == null) {
                                selectedEdgeStart = node;
                                log(" First node selected. Now select second node."); //8. Log messages
                            } else if (node.equals(selectedEdgeStart)) {
                                //9.handle edge case self loop
                                JOptionPane.showMessageDialog(null, " Cannot connect a node to itself!", "Invalid Edge", JOptionPane.WARNING_MESSAGE);
                            } else {
                                boolean exists = false;
                                for (Edge edge : edges) {
                                    if ((edge.src.equals(selectedEdgeStart) && edge.dest.equals(node)) ||
                                            (edge.dest.equals(selectedEdgeStart) && edge.src.equals(node))) {
                                        exists = true;
                                        break;
                                    }
                                }
                                if (exists) {
                                    log("⚠ Edge already exists between selected nodes."); //8. Log messages
                                } else {
                                    String weightStr = JOptionPane.showInputDialog("Enter edge weight:");
                                    try {
                                        int weight = Integer.parseInt(weightStr);
                                        //9.handling edge cases invalid weight inputb
                                        if (weight == 0) {
                                            JOptionPane.showMessageDialog(null, "❌ Invalid weight! Weight cannot be zero.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                                            log(" Edge weight cannot be zero.");
                                        } else {
                                            edges.add(new Edge(selectedEdgeStart, node, weight));
                                            log(" Edge added."); //8. Log messages
                                        }
                                    } catch (Exception ex) {
                                        log(" Invalid weight input."); //8. Log messages
                                    }
                                }
                                selectedEdgeStart = null;
                            }
                            repaint();
                            return;
                        }
                    }
                    return;
                }

                // Feature 7: allow change in topology(delete edge)
                if (edgeDeleteMode) {
                    Edge toRemove = null;
                    for (Edge edge : edges) {
                        double dist = ptLineDist(edge.src, edge.dest, clicked);
                        if (dist < 7) {
                            toRemove = edge;
                            break;
                        }
                    }
                    if (toRemove != null) {
                        edges.remove(toRemove);
                        Edge finalToRemove = toRemove;
                        mstEdges.removeIf(edge ->
                                (edge.src.equals(finalToRemove.src) && edge.dest.equals(finalToRemove.dest)) ||
                                        (edge.dest.equals(finalToRemove.src) && edge.src.equals(finalToRemove.dest))
                        );
                        log("Edge removed."); //8. Log messages
                    } else {
                        log(" No edge found at click location."); // 8. Log messages
                    }
                    repaint();
                    return;
                }

                //  Dragging Node Logic
                for (Point node : nodes) {
                    if (node.distance(clicked) < 15) {
                        draggedNode = node;
                        dragOffsetX = clicked.x - node.x;
                        dragOffsetY = clicked.y - node.y;
                        break;
                    }
                }
            }


            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    draggedNode.setLocation(e.getX() - dragOffsetX, e.getY() - dragOffsetY);
                    repaint();
                }
            }
        });
    }

    private double ptLineDist(Point A, Point B, Point P) {
        double norm = A.distance(B);
        return Math.abs((B.y - A.y) * P.x - (B.x - A.x) * P.y + B.x * A.y - B.y * A.x) / norm;
    }
}