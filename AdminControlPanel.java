import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.util.HashMap;

public class AdminControlPanel {
    private static AdminControlPanel instance = null;
    private JFrame frame;
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;
    private JTree tree;
    private HashMap<String, TwitterEntity> entities;

    private AdminControlPanel() {
        frame = new JFrame("Admin Control Panel");
        frame.setSize(600, 500);
        frame.setLayout(new GridLayout(1, 2));

        entities = new HashMap<>();

        root = new DefaultMutableTreeNode(new Group("Root"));
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tree);
        frame.add(scrollPane);

        JPanel rightSidePanel = new JPanel();
        rightSidePanel.setLayout(new BoxLayout(rightSidePanel, BoxLayout.Y_AXIS));

        JTextField userIdField = new JTextField("Enter User ID here");
        userIdField.setForeground(Color.GRAY);
        userIdField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (userIdField.getText().equals("Enter User ID here")) {
                    userIdField.setText("");
                    userIdField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (userIdField.getText().isEmpty()) {
                    userIdField.setForeground(Color.GRAY);
                    userIdField.setText("Enter User ID here");
                }
            }
        });
        rightSidePanel.add(userIdField);

        JButton addUserBtn = new JButton("Add User");
        addUserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    String userId = userIdField.getText();
                    if (userId.isEmpty() || userId.equals("Enter User ID here")) {
                        JOptionPane.showMessageDialog(frame, "Please enter a user ID first.");
                    } else {
                        User newUser = new User(userId);
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(newUser);
                        selectedNode.add(node);
                        treeModel.reload();
                        entities.put(userId, newUser);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a folder first.");
                }
            }
        });
        rightSidePanel.add(addUserBtn);

        JTextField groupIdField = new JTextField("Enter Group ID here");
        groupIdField.setForeground(Color.GRAY);
        groupIdField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (groupIdField.getText().equals("Enter Group ID here")) {
                    groupIdField.setText("");
                    groupIdField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (groupIdField.getText().isEmpty()) {
                    groupIdField.setForeground(Color.GRAY);
                    groupIdField.setText("Enter Group ID here");
                }
            }
        });
        rightSidePanel.add(groupIdField);

        JButton addGroupBtn = new JButton("Add Group");
        addGroupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    String groupId = groupIdField.getText();
                    if (groupId.isEmpty() || groupId.equals("Enter Group ID here")) {
                        JOptionPane.showMessageDialog(frame, "Please enter a group ID first.");
                    } else {
                        Group newGroup = new Group(groupId);
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(newGroup);
                        selectedNode.add(node);
                        treeModel.reload();
                        entities.put(groupId, newGroup);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a folder first.");
                }
            }
        });
        rightSidePanel.add(addGroupBtn);

        JButton showUserViewBtn = new JButton("Open User View");
        showUserViewBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    Object selectedObject = selectedNode.getUserObject();
                    if (selectedObject instanceof User) {
                        User user = (User) selectedObject;
                        UserView userView = new UserView(user);
                        user.addObserver(userView);
                        userView.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please select a valid user.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a user first.");
                }
            }
        });
        rightSidePanel.add(showUserViewBtn);

        JButton showUserTotalBtn = new JButton("Show User Total");
        showUserTotalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalUsers = 0;
                for (TwitterEntity entity : entities.values()) {
                    if (entity instanceof User) {
                        totalUsers++;
                    }
                }
                JOptionPane.showMessageDialog(frame, "Total Users: " + totalUsers);
            }
        });
        rightSidePanel.add(showUserTotalBtn);

        JButton showGroupTotalBtn = new JButton("Show Group Total");
        showGroupTotalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalGroups = 0;
                for (TwitterEntity entity : entities.values()) {
                    if (entity instanceof Group) {
                        totalGroups++;
                    }
                }
                JOptionPane.showMessageDialog(frame, "Total Groups: " + totalGroups);
            }
        });
        rightSidePanel.add(showGroupTotalBtn);

        JButton showMessageTotalBtn = new JButton("Show Messages Total");
        showMessageTotalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalMessages = 0;
                for (TwitterEntity entity : entities.values()) {
                    if (entity instanceof User) {
                        User user = (User) entity;
                        totalMessages += user.getTweets().size();
                    }
                }
                JOptionPane.showMessageDialog(frame, "Total Messages: " + totalMessages);
            }
        });
        rightSidePanel.add(showMessageTotalBtn);

        JButton showPositivePercentageBtn = new JButton("Show Positive Percentages");
        showPositivePercentageBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PositivePercentageVisitor visitor = new PositivePercentageVisitor();
                for (TwitterEntity entity : entities.values()) {
                    entity.accept(visitor);
                }
                float percentage = visitor.getPositivePercentage();
                JOptionPane.showMessageDialog(frame, "Positive Percentage: " + percentage + "%");
            }
        });
        rightSidePanel.add(showPositivePercentageBtn);

        frame.add(rightSidePanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public TwitterEntity findEntity(String entityId) {
        return entities.get(entityId);
    }

    public static AdminControlPanel getInstance() {
        if (instance == null) {
            instance = new AdminControlPanel();
        }
        return instance;
    }

    public User findUser(String userId) {
        TwitterEntity entity = entities.get(userId);
        if (entity instanceof User) {
            return (User) entity;
        }
        return null;
    }
}