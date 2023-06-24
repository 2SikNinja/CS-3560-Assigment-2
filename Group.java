import java.util.ArrayList;
import java.util.List;

public class Group implements TwitterEntity {
    private String id;
    private List<TwitterEntity> entities;

    public Group(String id) {
        this.id = id;
        this.entities = new ArrayList<>();
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

    public static String generateGroupName(User user) {
        return user.getId() + "'s Following";
    }

    @Override
    public void addObserver(Observer observer) {
        // Do nothing
    }

    @Override
    public void removeObserver(Observer observer) {
        // Do nothing
    }

    @Override
    public void notifyObservers(Tweet tweet) {
        // Do nothing
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return id;
    }
}
