/**A concurrent Rock, Paper, Scissors game. 
*@author Neil Millward - P13197943
*/
import java.util.Scanner;

/**
 * The Main game class initially greets the player and asks them to input details regarding player amount and turns.
 * Once the games player amount and turns have been decided the referee thread is created and then started. 
 * Once the referee thread has started then a simple for loop is used it initialise and start the player threads
 * 
 * Near the end of this class is a while loop used to gracefully shut down the game. It is further explained at the loop itself
 */

public class Main {
	public static void main(String[] args) {

		System.out.println("Welcome to Rock, Paper, Scissors!\n");
		Scanner s = new Scanner(System.in);									//scanner used to allow the user to enter player amount and turns

		System.out.println("Please enter the amount of players to participate followed by the enter key...\n");
		int a = s.nextInt();												//int used for player amount
		
		
		System.out.println("Please enter the amount of turns to be played followed by the enter key...\n");
		int b = s.nextInt();												//int used for amount of turns to be played
		
		if(a %2 != 0 && b %2 !=0){
			System.out.println("Can't have odd players with odd turns...Please restart");
			Thread.currentThread().stop();									//Unsafe way of ending the thread, yet convenient and safe in this instance as this will not effect other threads monitors as those threads have not been started at this point
		}																	//The main game will gracefully shut down the referee thread later when the game runs and concludes
		else if(a == 1 || b == 0 ){
			System.out.println("Can't have a single player/zero turns...Please restart");
			Thread.currentThread().stop();									//Unsafe way of ending the thread, yet again safe and will not effect other threads monitors as those threads have not been started at this point
		}
		
		
		int playerCount = a;
		int turns = b;
		s.close();															//closing scanner as not needed anymore

		Referee referee = new Referee();									//implementing a new referee object of type referee
		Thread ref = new Thread(referee);									//implementing a new thread of type referee
		ref.start();														//start thread

		Player[] players = new Player[playerCount + 1]; 					//Array of players


		for(int i = 1; i<= playerCount; i++){								//for loop used to create threads for number of players
			players[i] = new Player(i, turns);								//Creating new player objects
			players[i].ref = referee;										//passing by reference to the referee Thread
			Thread playerThread = new Thread(players[i]); 					//Creating new threads of type player
			playerThread.start(); 											//start threads

		}
		
		/**
		 * Used to gracefully shut down the referee thread.
		 * 
		 * While the game is playing the thread will wait, then the if statement will check if the player queue is empty(no more players left to play)
		 * if the condition is true the shutdown method from the referee is run, the method updates the refs playing boolean to false and therefore ends the thread
		 * the playing boolean of this thread is then set to false to stop the while loop.
		 * A message is then displayed to let the user know that all payers have finished and that the ref thread is gracefully shutting down
		 */
		boolean playing = true;
        while(playing) {
            try {
				Thread.sleep(4000); 										 //Have to have a sleep as the playerQueue initially starts empty, need time for the queue to put its first elements
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
            if(referee.playerQueue.isEmpty()) {								//queue is empty when no more turns left to play
                referee.shutdown();  										//calling the shutdown method from referee
                playing = false;    										//updating playing to false to stop this loop
                System.out.println("no more players, referee thread gracefully shutting down");
            }
        }
	}
}
