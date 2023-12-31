import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;
import java.time.ZoneOffset;

public class UserView extends JFrame implements Observer {
    private User user;
    private DefaultListModel<String> followingModel;
    private DefaultListModel<String> newsFeedModel;
    private JTextField followingField;
    private JTextField tweetField;
    private boolean isUpdatingNewsFeed; // Flag to control news feed updates

    public UserView(User user) {
        this.user = user;
        user.addObserver(this); // Register this view as an observer of the user
        createGui();
        isUpdatingNewsFeed = true; // Set the flag to true initially
    }

    private void createGui() {
        setTitle(user.getId() + " (User created on: " + getFormattedTimestamp(user.getCreationTime()) + ")");
        setSize(400, 500);
        setLayout(new GridLayout(4, 1));

        // Setup following list
        followingModel = new DefaultListModel<>();
        JList<String> followingList = new JList<>(followingModel);
        add(new JScrollPane(followingList));
        updateFollowingList();

        // Setup news feed list
        newsFeedModel = new DefaultListModel<>();
        JList<String> newsFeedList = new JList<>(newsFeedModel);
        add(new JScrollPane(newsFeedList));
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
                    updateNewsFeed();
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
                LocalDateTime timestamp = LocalDateTime.now();
                Tweet tweet = new Tweet(tweetMsg, user, timestamp);

                // Disable news feed updates temporarily
                isUpdatingNewsFeed = false;

                user.addTweet(tweet);
                tweetField.setText(""); // Clear the tweet text field

                // Re-enable news feed updates
                isUpdatingNewsFeed = true;
            }
        });
        lowerPanel.add(tweetButton);

        add(lowerPanel);

        // Configure the frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void updateFollowingList() {
        followingModel.clear();
        for (User following : user.getFollowingUsers()) {
            followingModel.addElement(following.getId());
        }
    }

    public void updateNewsFeed() {
        newsFeedModel.clear();
        List<Tweet> sortedTweets = getSortedTweets();

        Set<LocalDateTime> addedTimestamps = new HashSet<>(); // Keep track of added timestamps

        // Add the tweets from following users and group members to the news feed
        for (Tweet tweet : sortedTweets) {
            LocalDateTime timestamp = tweet.getTimestamp();
            if (!addedTimestamps.contains(timestamp)) {
                addedTimestamps.add(timestamp);

                String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                String message = "[" + formattedTimestamp + "] " + tweet.getAuthor().getId() + ": " + tweet.getMessage();
                newsFeedModel.addElement(message);
            }
        }
    }

    private List<Tweet> getSortedTweets() {
        List<Tweet> allTweets = new ArrayList<>();
        Set<User> followingUsers = new HashSet<>(user.getFollowingUsers());

        // Collect all tweets from following users
        for (User followingUser : followingUsers) {
            allTweets.addAll(followingUser.getTweets());
        }

        // Collect all tweets from group members
        for (Group group : user.getGroups()) {
            List<TwitterEntity> entities = group.getEntities();
            for (TwitterEntity entity : entities) {
                if (entity instanceof User) {
                    User groupUser = (User) entity;
                    allTweets.addAll(groupUser.getTweets());
                }
            }
        }

        // Add the user's own tweets to the list
        allTweets.addAll(user.getTweets());

        // Sort the tweets based on timestamp and remove duplicates with the same timestamp
        allTweets = allTweets.stream()
                .distinct()
                .sorted(Comparator.comparing(Tweet::getTimestamp))
                .collect(Collectors.toList());

        return allTweets;
    }

    private String getFormattedTimestamp(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    private User findUser(String userId) {
        AdminControlPanel adminControlPanel = AdminControlPanel.getInstance();
        return adminControlPanel.findUser(userId);
    }

    @Override
    public void update(Tweet tweet) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (isUpdatingNewsFeed) {
                    updateNewsFeed();
                }
            }
        });
    }
}
