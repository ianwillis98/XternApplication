import java.util.ArrayList;
import java.util.List;

public class XternCoinExample {

    public static void main(String[] args) {

	// Create the server
	XternCoinServer server = new XternCoinServer();

	// Create the list for storing userIds
	List<String> userIds = new ArrayList<>();

	// Add 100 users to the server
	for (int i = 0; i < 100; i++) {
	    userIds.add(server.createNewXternCoinUser());
	}

	// Start guessing simulation
	server.startGuessing();

	// Wait 1 second (1000 milliseconds)
	try {
	    Thread.sleep(1000);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	// Add another 100 users
	for (int i = 0; i < 100; i++) {
	    userIds.add(server.createNewXternCoinUser());
	}

	// Wait another second (1000 milliseconds)
	try {
	    Thread.sleep(1000);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	// Stop the guessing simulation
	server.stopGuessing();

	// Output the results
	for (String userId : userIds) {
	    System.out.println("User: " + userId + " has " + server.getCoins(userId) + " coins after two seconds!");
	}

	// Create a new user
	String myUserId = server.createNewXternCoinUser();

	// Trade all coins to the new user
	for (String otherUserId : userIds) {
	    server.tradeCoins(otherUserId, myUserId, 100);
	}

	// Output the trade results
	System.out.println("I now have all " + server.getCoins(myUserId) + " coins!");
    }
}
