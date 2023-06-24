import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserView implements Observer {
    private JFrame frame;
    private User user;
    private DefaultListModel<String> followingModel;
    private DefaultListModel<String> newsFeedModel;
    private JTextField followingField;
    private JTextField tweetField;

    public UserView(User user) {
        this.user = user;
        createGui();
    }

    private void createGui() {
        frame = new JFrame(user.getId());
        frame.setSize(400, 500);
        frame.setLayout(new GridLayout(3, 1));

        // Setup following list
        followingModel = new DefaultListModel<>();
        JList<String> followingList = new JList<>(followingModel);
        frame.add(new JScrollPane(followingList));
        updateFollowingList();

        // Setup news feed list
        newsFeedModel = new DefaultListModel<>();
        JList<String> newsFeedList = new JList<>(newsFeedModel);
        frame.add(new JScrollPane(newsFeedList));
        updateNewsFeed();

        // Setup lower part for following users and posting tweets
        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new GridLayout(2, 2));

        followingField = new JTextField("Enter User ID here");
        followingField.setForeground(Color.GRAY);
        followingField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (followingField.getText().equals("Enter User ID here")) {
                    followingField.setText("");
                    followingField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (followingField.getText().isEmpty()) {
                    followingField.setForeground(Color.GRAY);
                    followingField.setText("Enter User ID here");
                }
            }
        });
        lowerPanel.add(followingField);

        JButton followButton = new JButton("Follow User");
        followButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String followUserId = followingField.getText();
                User followUser = findUser(followUserId);
                if (followUser != null) {
                    user.addFollowing(followUser);
                    updateFollowingList();
                }
            }
        });
        lowerPanel.add(followButton);

        tweetField = new JTextField("Enter Tweet here");
        tweetField.setForeground(Color.GRAY);
        tweetField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (tweetField.getText().equals("Enter Tweet here")) {
                    tweetField.setText("");
                    tweetField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (tweetField.getText().isEmpty()) {
                    tweetField.setForeground(Color.GRAY);
                    tweetField.setText("Enter Tweet here");
                }
            }
        });
        lowerPanel.add(tweetField);

        JButton tweetButton = new JButton("Post Tweet");
        tweetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tweetMsg = tweetField.getText();
                Tweet tweet = new Tweet(tweetMsg, user);
                user.addTweet(tweet);
                updateNewsFeed();
            }
        });
        lowerPanel.add(tweetButton);

        frame.add(lowerPanel);

        // Configure the frame
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void updateFollowingList() {
        followingModel.clear();
        for (User following : user.getFollowings()) {
            followingModel.addElement(following.getId());
        }
    }

    private void updateNewsFeed() {
        newsFeedModel.clear();
        for (Tweet tweet : user.getTweets()) {
            newsFeedModel.addElement(tweet.getAuthor().getId() + ": " + tweet.getMessage());
        }
    }

    private User findUser(String userId) {
        AdminControlPanel adminControlPanel = AdminControlPanel.getInstance();
        return adminControlPanel.findUser(userId);
    }

    @Override
    public void update(Tweet tweet) {
        newsFeedModel.addElement(tweet.getAuthor().getId() + ": " + tweet.getMessage());
    }

    public void setVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }
}