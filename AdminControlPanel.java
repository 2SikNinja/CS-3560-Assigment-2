import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AdminControlPanel {
    private static AdminControlPanel instance = null;
    private JFrame frame;
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;
    private JTree tree;
    private HashMap<String, TwitterEntity> entities;
    private ImageIcon groupIcon;
    private ImageIcon userIcon;
    private ImageIcon folderIcon;

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
                        TwitterEntity selectedEntity = (TwitterEntity) selectedNode.getUserObject();
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

        JButton showMessageTotalBtn = new JButton("Show Message Total");
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

        JButton showPositivePercentageBtn = new JButton("Show Positive Percentage");
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

	   
        JButton userGroupVerification = new JButton("Verify Users and Groups");
	   userGroupVerification.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean allIDsValid = validateIDs(root);
			String verificationMessage = allIDsValid ? "Users and Groups Verified" : "Some IDs are invalid.";
			JOptionPane.showMessageDialog(frame, verificationMessage);
				}
			});
		rightSidePanel.add(userGroupVerification);



        frame.add(rightSidePanel);

        // Set custom icons for Group and User nodes
        CustomTreeCellRenderer renderer = new CustomTreeCellRenderer();
        groupIcon = new ImageIcon("group.png");
        userIcon = new ImageIcon("user.png");
        folderIcon = new ImageIcon("folder.png");
        renderer.setClosedIcon(folderIcon);
        renderer.setOpenIcon(folderIcon);
        renderer.setLeafIcon(folderIcon);
        tree.setCellRenderer(renderer);

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

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, Group group) {
        if (root.getUserObject().equals(group)) {
            return root;
        } else {
            DefaultMutableTreeNode node = null;
            int childCount = root.getChildCount();
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) root.getChildAt(i);
                node = findNode(childNode, group);
                if (node != null) {
                    break;
                }
            }
            return node;
        }
    }

    // Custom tree cell renderer to display different icons for User and Group nodes
    private class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
                if (userObject instanceof User) {
                    setIcon(userIcon);
                } else if (userObject instanceof Group) {
                    if (node.isRoot()) {
                        setIcon(folderIcon);
                    } else {
                        setIcon(groupIcon);
                    }
                }
            }
            return component;
        }
    }

    private boolean validateIDs(DefaultMutableTreeNode node) {
    Set<String> idSet = new HashSet<>();
    Enumeration<?> enumeration = node.depthFirstEnumeration();
    while (enumeration.hasMoreElements()) {
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumeration.nextElement();
        Object userObject = currentNode.getUserObject();
        if (userObject instanceof TwitterEntity) {
            TwitterEntity entity = (TwitterEntity) userObject;
            String id = entity.getId();
            if (idSet.contains(id) || id.contains(" ")) {
                return false;
            }
            idSet.add(id);
        }
    }

    for (int i = 0; i < node.getChildCount(); i++) {
        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
        if (!validateIDs(childNode)) {
            return false;
        }
    }

    return true;
}


}
