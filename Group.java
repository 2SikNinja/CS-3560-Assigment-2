import java.util.ArrayList;
import java.util.List;

public class Group implements TwitterEntity, Observer {
    private String id;
    private List<TwitterEntity> entities;
    private List<Tweet> tweets; // Store the received tweets

    public Group(String id) {
        this.id = id;
        this.entities = new ArrayList<>();
        this.tweets = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void addEntity(TwitterEntity entity) {
        entities.add(entity);
    }

    public List<TwitterEntity> getEntities() {
        return entities;
    }

    public List<Tweet> collectTweets() {
        List<Tweet> collectedTweets = new ArrayList<>();
        for (TwitterEntity entity : entities) {
            if (entity instanceof User) {
                User user = (User) entity;
                collectedTweets.addAll(user.getTweets());
            } else if (entity instanceof Group) {
                Group group = (Group) entity;
                collectedTweets.addAll(group.collectTweets());
            }
        }
        return collectedTweets;
    }

    public static String generateGroupName(User user, User followUser) {
        return user.getId() + "'s Following with " + followUser.getId();
    }

    @Override
    public void addObserver(Observer observer) {
        for (TwitterEntity entity : entities) {
            entity.addObserver(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        for (TwitterEntity entity : entities) {
            entity.removeObserver(observer);
        }
    }

    @Override
    public void notifyObservers(Tweet tweet) {
        for (TwitterEntity entity : entities) {
            entity.notifyObservers(tweet);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public void update(Tweet tweet) {
        // Store the received tweet
        tweets.add(tweet);

        // Notify the observers (users and other groups)
        notifyObservers(tweet);
    }

    public void removeEntity(TwitterEntity entity) {
        entities.remove(entity);
    }
}
