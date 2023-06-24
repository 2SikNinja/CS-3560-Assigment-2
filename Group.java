import java.util.ArrayList;
import java.util.List;

public class Group implements TwitterEntity {
    private static int totalGroups = 0;

    private String id;
    private List<TwitterEntity> entities;
    private List<Observer> observers;

    public Group(String id) {
        this.id = id;
        this.entities = new ArrayList<>();
        this.observers = new ArrayList<>();
        totalGroups++;
    }

    public static int getTotalGroups() {
        return totalGroups;
    }

    public String getId() {
        return id;
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
    public void accept(Visitor visitor) {
        visitor.visit(this);
        for (TwitterEntity entity : entities) {
            entity.accept(visitor);
        }
    }

    @Override
    public String toString() {
        return id;
    }

    public void addEntity(TwitterEntity entity) {
        entities.add(entity);
        if (entity instanceof User) {
            User user = (User) entity;
            for (Tweet tweet : user.getTweets()) {
                notifyObservers(tweet);
            }
        }
    }

    public void notifyObservers(Tweet tweet) {
        for (Observer observer : observers) {
            observer.update(tweet);
        }
    }
}
