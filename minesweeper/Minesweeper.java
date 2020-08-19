package minesweeper;

import java.util.Random;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Minesweeper extends Application {
	
	// VIEW SETTINGS
	static final int WIDTH = 20;
	static final int HEIGHT = 20;
	static final int CELL_SIZE = 30;
	static final double BORDER = 0.5;
	
	static GraphicsContext gc;
	static Random rnd = new Random();
	
	// Game Storage
	static boolean[][] mines = new boolean[WIDTH][HEIGHT];
	static int[][] neighbors = new int[WIDTH][HEIGHT];
	static boolean[][] revealed = new boolean[WIDTH][HEIGHT];
	
	// Game Settings
	static int mineCount = 40;
	
	// Game Stuff/States
	static boolean dead = false;
	
	@Override
    public void start(Stage primaryStage) {
		
	    Group root = new Group();
    	Scene s = new Scene(root, WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE, Color.BLACK);

    	final Canvas canvas = new Canvas(WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE);
    	canvas.setFocusTraversable(true);
    	gc = canvas.getGraphicsContext2D();
    	gc.setTextAlign(TextAlignment.CENTER);
    	gc.setTextBaseline(VPos.CENTER);
    	gc.setFont(new Font(CELL_SIZE * 0.8));

    	root.getChildren().add(canvas);
    
    	primaryStage.initStyle(StageStyle.UNIFIED);
    	primaryStage.setTitle("Minesweeper");
    	primaryStage.setScene(s);
    	primaryStage.setResizable(false);
    	primaryStage.show();
		
    	canvas.setOnMouseClicked(e -> {
    		int x = (int) e.getX() / CELL_SIZE;
    		int y = (int) e.getY() / CELL_SIZE;
    		if (mines[x][y]) {
    			dead = true;
    			draw();
    		}
    		else {
    			floodFill(x, y);
    			draw();
    		}
    	});
    	
    	canvas.setOnKeyTyped(e -> {
    		if (dead) {
    			start();
    		}
    	});
    	
    	start();
	}
	
	public static void draw() {
		gc.clearRect(0, 0, WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE);
		
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				if (!revealed[i][j] && !dead) {
					gc.setFill(Color.DIMGREY);
					gc.fillRect(i * CELL_SIZE + BORDER, j * CELL_SIZE + BORDER, CELL_SIZE - BORDER * 2, CELL_SIZE - BORDER * 2);
				}
				else {
					if (mines[i][j]) {
						gc.setFill(Color.RED);
						gc.fillRect(i * CELL_SIZE + BORDER, j * CELL_SIZE + BORDER, CELL_SIZE - BORDER * 2, CELL_SIZE - BORDER * 2);
					}
					else {
						gc.setFill(Color.LIGHTGREY);
						gc.fillRect(i * CELL_SIZE + BORDER, j * CELL_SIZE + BORDER, CELL_SIZE - BORDER * 2, CELL_SIZE - BORDER * 2);
						if (neighbors[i][j] > 0) {
							gc.setFill(Color.BLACK);
							gc.fillText(neighbors[i][j] + "", i * CELL_SIZE + CELL_SIZE / 2, j * CELL_SIZE + CELL_SIZE / 2);
						}
					}
				}
			}
		}
	}
	
	public static void floodFill(int x, int y) {
		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return;
		if (mines[x][y])
			return;
		if (neighbors[x][y] > 0) {
			revealed[x][y] = true;
			return;
		}
		if (!revealed[x][y]) {
			revealed[x][y] = true;
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i == 0 && j == 0)
						continue;
					floodFill(x + i, y + j);
				}
			}
		}
	}
	
	public static int getNeighbors(int x, int y) {
		if (mines[x][y])
			return 0;
		int neighbors = 0;
		for (int i = x-1; i <= x+1; i++) {
			for (int j = y-1; j <= y+1; j++) {
				if (x == i && y == j)
					continue;
				if (i >= 0 && j >= 0 && i < WIDTH && j < HEIGHT && mines[i][j])
					neighbors++;
			}
		}
		return neighbors;
	}
	
	public static void start() {
		dead = false;
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				mines[i][j] = false;
				neighbors[i][j] = 0;
				revealed[i][j] = false;
			}
		}
		
		int m = 0;
		while (m < mineCount) {
			int x = rnd.nextInt(WIDTH);
			int y = rnd.nextInt(HEIGHT);
			
			if (!mines[x][y]) {
				mines[x][y] = true;
				m++;
			}
		}
		
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				neighbors[i][j] = getNeighbors(i,j);
			}
		}
		draw();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
