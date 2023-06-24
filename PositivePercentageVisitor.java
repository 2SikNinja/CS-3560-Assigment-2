public class PositivePercentageVisitor implements Visitor {
     private int totalTweets = 0;
     private int positiveTweets = 0;

     @Override
     public void visit(User user) {
          for (Tweet tweet : user.getTweets()) {
               totalTweets++;
               if (tweet.isPositive()) {
                    positiveTweets++;
               }
          }
     }

     @Override
     public void visit(Group group) {
          // Do nothing for groups
     }

     public float getPositivePercentage() {
          return totalTweets == 0 ? 0 : ((float) positiveTweets / totalTweets) * 100;
     }
}
