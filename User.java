import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

public class User implements TwitterEntity {
    private static int totalUsers = 0;

    private String id;
    private List<User> followers;
    private List<User> followings;
    private List<Tweet> tweets;
    private List<Observer> observers;
    private Group followingGroup;
    private HashMap<String, TwitterEntity> entities;

    public User(String id, HashMap<String, TwitterEntity> entities) {
        this.id = id;
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.tweets = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.entities = entities;
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

    public List<User> getFollowings() {
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

    // Update the following group of the user
    if (followingGroup == null) {
        followingGroup = user.getFollowingGroup(); // Get the following group of the followed user
    }
    if (followingGroup == null) {
        String groupName = "Following";
        followingGroup = (Group) entities.get(groupName);
        if (followingGroup == null) {
            followingGroup = new Group(groupName);
            entities.put(groupName, followingGroup);
        }
    }
    followingGroup.addEntity(user);
}



    public Group getFollowingGroup() {
        return followingGroup;
    }

    public void addTweet(Tweet tweet) {
        this.tweets.add(tweet);
        notifyObservers(tweet);
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
        for (Observer observer : observers) {
            observer.update(tweet);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return getName(); // Return the user's name
    }

    public String getName() {
        return id; // Return the user's name (in this case, the ID)
    }
}
