import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Player implements Runnable {


	int playerId; 												//used to identify each player
	int maxTurns; 												//max turns to be played
	int turnsPlayed; 											//amount of turns taken, incremented at the end of each turn
	int wins = 0;  												//used to keep track of wins
	int losses = 0; 											//used to keep track of losses
	int draws = 0; 												//used to keep track of draws

	String chosenShape;											//used to store the players chosen shape into
	MVar<String> recentResult;									//An MVar used to store replying message from referee
	List<String> results; 										//Used to store the messages taken from the MVar
	Referee ref;												//an instance of referee called ref, used to call the playerQueue from referee

	//Player Custom Constructor
	public Player(int id, int maxTurns) {
		this.playerId = id;											//setting playerId
		this.maxTurns = maxTurns;									//setting maxTurns
		this.results = new ArrayList<String>();						//used to store the result of the takeMvar() method
	}



	/**
	 * When this thread executes the player selects a shape and adds itself to the playerQueue for the referee to use when it is the players turn to play.
	 * The player thread will then wait for an outcome of the round by the referee before continuing, outcome being a return communication from the putMVar() method(thus synchronising the threads).
	 * The return communication from the referee is then stored into an array in order to be compared against to determine which one of the players internal states is incremented.
	 * Once the players turns played are equal to the games max turns then a message will be displayed telling the user this players total wins, draws and losses before concluding.
	 */
	@Override
	public void run() {
		while (turnsPlayed != maxTurns) { 						//allows the thread to run until it reaches the max turns
			recentResult = new MVar<String>();					//Create an MVar to allow synchronous communication between threads
			chosenShape = getShape();							//Calls the getShape method and assigns the shape to current Shape

			ref.playerQueue.add(this);							//Adding this thread to a concurrent linked queue, when the player is added it is a way of notifying the referee that this player is ready to play.
																//This player will then be taken out the referees playerQueue when it is this players turn to play

			results.add(recentResult.takeMVar());				//calling the takeMVar method and storing the contents from putMvar(ref thread)into results to then be compared

			if(results.contains("WINNER")){						//condition to check array contents, if == "WINNER" go into body
				wins++;											//update the internal state of wins accordingly
			}
			else if(results.contains("LOSER")){					//condition to check array contents, if == "LOSER" go into body
				losses++;										//update the internal state of losses accordingly
			}
			else{												//will == "Draw", go into body
				draws++;									    //update the internal state of draws accordingly
			}

			results.clear(); 									//clear results so if statement above functions correctly, remove this line and the end results become skewed
			turnsPlayed++;										//incrementing turns played after each "play"

		}
		if (turnsPlayed == maxTurns) {						//condition to determine when the player has played its last turn

			//print statement will then display the current threads overall wins, draws and losses
			System.out.println(">>>>>>Player " + getPlayerId() + " Finshed its turns --" + " Wins: " + wins
					+ " Draws: " + draws + " Losses: " + losses + "<<<<<<");

		}

	}



	/**Used to randomly generate a shape to store into chosenShape
	 * 
	 * @return random shape
	 */
	public String getShape() {
		Random r = new Random(); 								//creating a random object called r
		String[] shapes = { "Rock", "Paper", "Scissors" };		//string array containing the players shape
		return shapes[r.nextInt(3)]; 							//Randomly selecting a shape
	}

	/**Used to get the individual player threads id
	 * 
	 * @return players id
	 */
	public int getPlayerId() {
		return playerId;
	}

	/**Used in the referee thread to get the shape chosen by this thread
	 * 
	 * @return shape stored from getShape method
	 */
	public String getChosenShape() {
		return chosenShape;
	}



}
