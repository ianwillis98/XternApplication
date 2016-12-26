import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class XternCoinServer {

    // The range of possible random numbers that users have
    // to guess from is [0, maxRandomInt)
    private static int maxRandomInt = Integer.MAX_VALUE / 200000;

    // Map of all users connected to the server
    // Maps a user's id to the number of coins said user has
    // Note: currently, users cannot be removed once added
    private Map<String, Integer> users;

    // For generating possible user id's and
    // random numbers for guessing and checking
    private SecureRandom random;

    // The current random number users need to guess to get a coin
    private int currentRandomNumber;

    // Thread where users are simulated making guesses
    private Thread guessThread;

    // Boolean which represents whether or not the simulation is currently
    // running
    // This field is volatile because the guessThread reads its value and the
    // main thread updates it
    private volatile boolean isRunning;

    public XternCoinServer() {
	// ConcurrentHashMap is used to allow users to be added while guessing
	// is taking place
	users = new ConcurrentHashMap<>();
	random = new SecureRandom();
	currentRandomNumber = random.nextInt(maxRandomInt);
    }

    public String createNewXternCoinUser() {
	String uuid = generateUuid();
	users.put(uuid, 0);
	return uuid;
    }

    public String generateUuid() {
	// "[chooses] 130 bits from a cryptographically secure random bit
	// generator, and [encodes] them in base-32." -
	// http://stackoverflow.com/a/41156/6907163
	String randomUuid = "";
	do {
	    randomUuid = new BigInteger(130, random).toString(32);
	} while (isUuidInUse(randomUuid));
	return randomUuid;
    }

    private boolean isUuidInUse(String uuid) {
	return users.containsKey(uuid);
    }

    /*
     * A function which, when called, pretends to be a user of XternCoin and
     * uses the other two functions you've written to accumulate coins by
     * guessing random numbers in a loop (indefinite is fine).
     */
    public void startGuessing() {
	if (isRunning)
	    return;
	isRunning = true;
	guessThread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		while (isRunning) {
		    for (String userId : users.keySet()) {
			int randomGuess = random.nextInt(maxRandomInt);
			boolean isCorrectGuess = handleGuess(userId, randomGuess);
			if (isCorrectGuess) {
			    increaseCoinCount(userId);
			}
		    }
		    try {
			// Decrease load on cpu
			// Sleeps for 1 millisecond
			Thread.sleep(1);
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		}
	    }
	});
	// Begin guessing on async thread
	guessThread.start();

    }

    public void stopGuessing() {
	isRunning = false;
    }

    /*
     * Function which takes a user's id and a user's guess, and returns whether
     * or not their guess was correct.
     */
    private boolean handleGuess(String userId, int guess) {
	boolean isCorrectGuess = (guess == currentRandomNumber);
	if (isCorrectGuess) {
	    System.out.println("User " + userId + " guessed " + guess + " and was right! +1 coin");
	    currentRandomNumber = random.nextInt(maxRandomInt);
	}
	return isCorrectGuess;
    }

    /*
     * Increases the coin count of the user associated with the userId parameter
     */
    private void increaseCoinCount(String userId) {
	int newCoins = users.get(userId) + 1;
	users.replace(userId, newCoins);
    }

    /*
     * Function which takes a user's id and returns how many coins they have
     */
    public int getCoins(String userId) {
	return users.get(userId);
    }

    /*
     * Trades coins from one user to another. If the fromUser has less coins
     * than the amount, all of the fromUser's coins will be transfered instead
     * of the amount
     */
    public void tradeCoins(String fromUserId, String toUserId, int amount) {
	int fromCoinCount = users.get(fromUserId);
	int toCoinCount = users.get(toUserId);
	if (amount > fromCoinCount) {
	    amount = fromCoinCount;
	}
	users.replace(fromUserId, fromCoinCount - amount);
	users.replace(toUserId, toCoinCount + amount);
    }

}
