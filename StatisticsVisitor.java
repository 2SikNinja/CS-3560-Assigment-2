public class StatisticsVisitor implements Visitor {
     private int totalUsers = 0;
     private int totalGroups = 0;
     private int totalTweets = 0;

     @Override
     public void visit(User user) {
          totalUsers++;
          totalTweets += user.getTweets().size(); // Count tweets for each user
     }

     @Override
     public void visit(Group group) {
          totalGroups++;
     }

     public int getTotalUsers() {
          return totalUsers;
     }

     public int getTotalGroups() {
          return totalGroups;
     }

     public int getTotalTweets() {
          return totalTweets;
     }
}
