import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class User implements TwitterEntity {
    private static int totalUsers = 0;

    private String id;
    private List<User> followers;
    private List<User> followings;
    private List<Tweet> tweets;
    private List<Observer> observers;
    private List<Tweet> newsFeed;
    private Tweet latestTweet; // Keep track of the latest posted tweet
    private UserView userView; // Reference to the associated UserView instance
    private List<Group> groups; // List of groups the user belongs to
    private long creationTime; // Creation time of the user

    public User(String id) {
        this.id = id;
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.tweets = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.newsFeed = new ArrayList<>();
        this.latestTweet = null;
        this.userView = null;
        this.groups = new ArrayList<>();
        this.creationTime = System.currentTimeMillis();
        totalUsers++;
    }

    public static int getTotalUsers() {
        return totalUsers;
    }

    public String getId() {
        return id;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public List<User> getFollowingUsers() {
        return followings;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void addFollower(User user) {
        this.followers.add(user);
    }

    public void addFollowing(User user) {
        this.followings.add(user);
        user.addFollower(this); // Add this user as a follower of the followed user
    }

    public void addTweet(Tweet tweet) {
        if (tweets.contains(tweet)) {
            return; // Tweet already exists, no further processing needed
        }

        this.tweets.add(tweet);
        notifyObservers(tweet);

        // Update the news feeds of all followers
        for (User follower : followers) {
            follower.newsFeed.add(tweet);
        }

        // Update the news feeds of all group members
        for (Group group : groups) {
            group.notifyObservers(tweet);
        }

        // Update the news feed of the current user
        if (!newsFeed.contains(tweet)) {
            this.newsFeed.add(tweet);
        }

        // Sort the news feed in chronological order
        newsFeed.sort(Comparator.comparing(Tweet::getTimestamp));

        // Update the latest posted tweet
        setLatestTweet(tweet);
    }

    public List<Tweet> getNewsFeed() {
        return newsFeed;
    }

    private void setLatestTweet(Tweet tweet) {
        if (latestTweet == null || tweet.getTimestamp().isAfter(latestTweet.getTimestamp())) {
            latestTweet = tweet;
        }
    }

    public Tweet getLatestTweet() {
        return latestTweet;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public void removeUserView() {
        this.userView = null;
    }

    public UserView getUserView() {
        return userView;
    }

    public void joinGroup(Group group) {
        this.groups.add(group);
        group.addEntity(this);
    }

    public void leaveGroup(Group group) {
        this.groups.remove(group);
        group.removeEntity(this);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(Tweet tweet) {
        List<Observer> copyObservers = new ArrayList<>(observers);
        for (Observer observer : copyObservers) {
            observer.update(tweet);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return id;
    }
}
