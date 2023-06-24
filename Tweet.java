import java.util.Arrays;
import java.util.List;

public class Tweet {
     private String message;
     private User author;
     private static List<String> positiveWords = Arrays.asList("good", "happy", "excellent", "fantastic", "positive", "joy", "enjoy");

     public Tweet(String message, User author) {
          this.message = message;
          this.author = author;
     }

     public String getMessage() {
          return message;
     }

     public User getAuthor() {
          return author;
     }

     public boolean isPositive() {
          String[] words = message.toLowerCase().split(" ");
          for (String word : words) {
               if (positiveWords.contains(word)) {
                    return true;
               }
          }
          return false;
     }
}
