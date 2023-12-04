package brickBreaker;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.concurrent.ThreadLocalRandom;

public class GamePlay extends JPanel implements KeyListener, ActionListener {
 	
	private boolean play = false; // to tell when a game should be running
	private int score = 0; // starting score
	private int totalBricks = 21;
	private Timer timer; // setting the speed of ball
	private int delay = 8; // speed given to timer
	private int playerX = 310; // starting position of slider
	private int ballposX = ThreadLocalRandom.current().nextInt(100, 650); // starting x position of ball (RNG)
	private int ballposY = 350; // starting y position of ball
	private int ballXdir = -1; //
	private int ballYdir = -2; // the ball should move twice as fast vertically than horizontally
	private MapGenerator map;

	public GamePlay() {
		map = new MapGenerator(3, 7); // three blocks vertical, seven blocks across
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		timer = new Timer(delay, this);
		timer.start(); // enables the motion of the ball
	}

	// drawing the elements
	public void paint(Graphics g) {
		// set background
		g.setColor(Color.BLACK);
		g.fillRect(1, 1, 692, 592);

		// drawing map
		map.draw((Graphics2D)g);

		if (play) {
			// set border to yellow while playing
			g.setColor(Color.YELLOW);
			g.fillRect(0, 0, 3, 592);
			g.fillRect(0, 0, 692, 3);
			g.fillRect(691, 0, 3, 592);
		}

		// scores
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 25));
		g.drawString(""+score, 590, 30);

		// set paddle
		g.setColor(Color.WHITE);
		g.fillRect(playerX, 550, 100, 8);

		// create the ball
		g.setColor(Color.WHITE);
		g.fillOval(ballposX, ballposY, 20, 20);

		if (totalBricks <= 0) { // if all bricks are broken
			// stop the game and the ball's motion
			play = false;
			ballXdir = 0;
			ballYdir = 0;
			// set text to green when game is won
			g.setColor(Color.GREEN);
			g.setFont(new Font("arial", Font.BOLD, 40));
			g.drawString("You Won! ", 250, 300);
			g.setFont(new Font("arial", Font.BOLD, 20));
			g.drawString("Press Enter to Restart", 240, 350);
			// set border to green when won
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, 3, 592);
			g.fillRect(0, 0, 692, 3);
			g.fillRect(691, 0, 3, 592);
		}

		if (ballposY > 570) { // if the ball fell through the bottom
			// stop the game and the ball's motion
			play = false;
			ballXdir = 0;
			ballYdir = 0;
			// set text to red 
			g.setColor(Color.RED);
			g.setFont(new Font("arial", Font.BOLD, 40));
			g.drawString("Game Over", 237, 300);
			g.setFont(new Font("arial", Font.BOLD, 20));
			g.drawString("Press Enter to Restart", 240, 350);
			// set border to red
			g.setColor(Color.RED);
			g.fillRect(0, 0, 3, 592);
			g.fillRect(0, 0, 692, 3);
			g.fillRect(691, 0, 3, 592);
		}

		g.dispose();
	}


 	@Override
 	public void actionPerformed(ActionEvent e) {
 		timer.start();
 		if (play) {
 			// when the ball hits the slider, switch direction.
 			if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
 				int rand = ThreadLocalRandom.current().nextInt(0, 2); // randomly switch horiz directions 
 				int randDir = ThreadLocalRandom.current().nextInt(2, 4); // randomly switch speeds
 				if (rand == 1) ballXdir = randDir;
 				else if (rand == 0) ballXdir = -randDir;
 				ballYdir = -randDir;
 			}

 			// setting hitboxes for the bricks
 			bricks: for (int i=0; i<map.map.length; i++) {
 				for (int j=0; j<map.map[0].length; j++) {
 					if (map.map[i][j] > 0) {
 						int brickX = j*map.brickWidth + 80;
 						int brickY = i*map.brickHeight + 50;
 						int brickWidth = map.brickWidth;
 						int brickHeight = map.brickHeight;

 						Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
 						Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
 						Rectangle brickRect = rect;

 						// if the ball hits a block
 						if (ballRect.intersects(brickRect)) {
 							map.setBrickValue(0,i,j);
 							totalBricks--;
 							score += 5;

 							if (ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width)
 								ballXdir = -ballXdir;
 							else
 								ballYdir = -ballYdir;

 							break bricks; // break parent for-loop
 						}
 					}
 				}
 			}
 			// motion of the ball
 			ballposX += 2*ballXdir;
 			ballposY += 2*ballYdir;
 			// switch directions if...
 			if (ballposX < 0) // ball hits the side
 				ballXdir = -ballXdir;
 			if (ballposY < 0) // ball hits the side
 				ballYdir = -ballYdir;
 			if (ballposX > 670) // ball hits the roof
 				ballXdir = -ballXdir;
 		}

 		repaint(); // to update the image as it is being moved;
 	}

 	@Override
 	public void keyTyped(KeyEvent e) {}

 	@Override
 	public void keyReleased(KeyEvent e) {}

 	@Override
 	public void keyPressed(KeyEvent e) {
 		// if the right arrow key is pressed, move slider to right
 		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
 			if (playerX >= 600) // if it goes out of bounds
 				playerX = 600;
 			else 
 				moveRight();
 		}

 		// if the left arrow key is pressed, move slider to left
 		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
 			if (playerX < 10) // if it goes out of bounds
 				playerX = 10; // reset it
 			else 
 				moveLeft();
 		}
 		// if the enter key is pressed 
 		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
 			if (!play) { // while the game is over
 				play = true; // start the game
 				// reset variables
 				ballposX = ThreadLocalRandom.current().nextInt(100, 650); // RNG
 				ballposY = 350;
 				ballXdir = -1;
 				ballYdir = -2;
 				score = 0;
 				totalBricks = 21;
 				map = new MapGenerator(3, 7);

 				repaint(); // refresh image
 			}
 		}
 	}

 	public void moveRight() {
 		play = true;
 		playerX += 20;
 	}

 	public void moveLeft() {
 		play = true;
 		playerX -= 20;
 	}

}