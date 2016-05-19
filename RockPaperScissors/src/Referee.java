import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Referee implements Runnable {
	public Queue<Player> playerQueue;												//Creating a Queue of type player
	public boolean playing = true;													//Used for while loop
	


	public Referee() {
	playerQueue = new ConcurrentLinkedQueue<Player>();								//Setting ready players to a thread safe ConcurrentLinkedQueue
	}

	@Override
	public void run() {
		while (playing) {															// Will run as playing is set to true
			if (playerQueue.size() >= 2) {											// Condition to get 2 players at a time
				try { 																// The Try catch is used in conjunction with the graceful shut down
																					//in order for the if statement in the MainGame to be true the thread has to wait in order for 2 more players to be put in the queue.
																					//if not the shutdown in MainGame will occur early.
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				getResult(); 														//Running the get result method of this thread. Put into a method because of design choice

			}

		}

	}

	/**
	 * The getResult() method firstly polls 2 players from the concurrentlinked playerQueue and assigns them to player1 and 2 accordingly
	 *
	 * If else statements then run to determine the winner of the round. Uses the getChosenShape method to compare player 1 and player 2's selected shapes
	 * A print statement is used to display the result of the round
	 * An MVar is then put in the recentResult array for the player thread to then take the MVar
	 * This is what synchronises the referee and the player threads, they wait for the MVars to be put before continuing running after taking the MVar
	 * The string in the MVar is what the player thread uses to update its internal win, draws and loss state.
	 * The loop then runs again for the next round
	 */
	public void getResult(){

		Player player1 = playerQueue.poll();										//first player from the queue is assigned player1
		Player player2 = playerQueue.poll();										//second player from the queue is assigned player2


		//Print statement used to display the game in a user friendly, easy to read manner
		System.out.println("Player "+player1.getPlayerId()+" shape selected: " +player1.getChosenShape()+ " <-VS-> " + "Player "+player2.getPlayerId()+" shape selected: "+player2.getChosenShape());

		if(player1.getChosenShape().equals(player2.getChosenShape())){ 			//comparing each players chosen shape
			System.out.println("draw\n");       								//Will print out draw on the terminal
			player1.recentResult.putMVar("DRAW");								//Puts draw in an MVar which will be used by the player to update its internal state and is also what synchronises threads
			player2.recentResult.putMVar("DRAW");								//Puts draw in an MVar which will be used by the other player to update its internal state and is also what synchronises threads
		}

		 else if 
			 (player1.getChosenShape().equals("Rock") && player2.getChosenShape().equals("Paper")) {   //Same as the previous if statement but instead of checking if the shape in the index's are equal, it checks if one is rock and the other paper.
				System.out.println("Player "+player2.getPlayerId()+" wins\n");
				player1.recentResult.putMVar("LOSER"); 												//Puts draw in an MVar which will be used by the player to update its internal state and is also what synchronises threads
				player2.recentResult.putMVar("WINNER");												//Puts draw in an MVar which will be used by the other player to update its internal state and is also what synchronises threads
			}

			//The else if code below is the same as above except what shapes to compare against and updating the MVars accordingly
			else if (player1.getChosenShape().equals("Rock") && player2.getChosenShape().equals("Scissors")) {
				System.out.println("Player "+player1.getPlayerId()+" wins\n");
				player1.recentResult.putMVar("WINNER");
				player2.recentResult.putMVar("LOSER");
			}
			else if (player1.getChosenShape().equals("Paper") && player2.getChosenShape().equals("Rock")) {
				System.out.println("Player "+player1.getPlayerId()+" wins\n");
				player1.recentResult.putMVar("WINNER");
				player2.recentResult.putMVar("LOSER");
			}
			else if (player1.getChosenShape().equals("Paper") && player2.getChosenShape().equals("Scissors")){
				System.out.println("Player "+player2.getPlayerId()+" wins\n");
				player1.recentResult.putMVar("LOSER");
				player2.recentResult.putMVar("WINNER");
			}
			else if (player1.getChosenShape().equals("Scissors") && player2.getChosenShape().equals("Rock")){
				System.out.println("Player "+player2.getPlayerId()+"  wins\n");
				player1.recentResult.putMVar("LOSER");
				player2.recentResult.putMVar("WINNER");
			}
			else 
			{   if(player1.getChosenShape().equals("Scissors") && player2.getChosenShape().equals("Paper")){
				System.out.println("Player "+player1.getPlayerId()+" wins\n");
				player1.recentResult.putMVar("WINNER");
				player2.recentResult.putMVar("LOSER");

			}

		}
	}

	/**
	 * Method used in the main game class, used for the graceful shut down of this thread
	 */
	public void shutdown() {
		playing = false; 																			// setting playing to false will stop the playing loop of this thread, in turn shutting down the thread
	}
}

