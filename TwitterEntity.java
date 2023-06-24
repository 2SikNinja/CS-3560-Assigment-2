public interface TwitterEntity {
    String getId();
    void accept(Visitor visitor);
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Tweet tweet);
}
