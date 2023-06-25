import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class IconTreeCellRenderer extends JPanel implements TreeCellRenderer {
    private JLabel label;
    private ImageIcon groupIcon;
    private ImageIcon userIcon;

    public IconTreeCellRenderer() {
        setLayout(new BorderLayout());
        label = new JLabel();
        add(label, BorderLayout.CENTER);
        groupIcon = new ImageIcon("group.png");
        userIcon = new ImageIcon("user.png");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        if (userObject instanceof Group) {
            label.setIcon(groupIcon);
            label.setText(userObject.toString());
        } else if (userObject instanceof User) {
            label.setIcon(userIcon);
            label.setText(userObject.toString());
        }
        return this;
    }
}
