package com.dragonboat.game;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Represents a goose obstacle on the course
 */
public class Goose extends Obstacle {

	public String direction = "South"; //Facing south by default.
	public Lane givenLane;
	
	/**
	 * Creates a goose instance
	 * @param xPosition X coordinate position
	 * @param yPosition Y coordinate position
	 * @param texture Texture asset for the goose
	 * @param lane Lane that the goose will spawn in
	 */
	public Goose(int xPosition, int yPosition, Texture texture, Lane lane) {
		// Joe: geese will have a set damage value, could increase with difficulty.
		//      they'll also have a set width and height. just put some placeholders in for now.
		// Ben: geese can also face N, S, E, W.
		//      width and height will swap places when  switching between N/S and E/W.
		super(10, xPosition, yPosition, texture.getWidth(), texture.getHeight(), texture);
		this.givenLane = lane;
	}
	
	/**
	 * Changes the direction of the Goose to an appropriate albeit random cardinal direction.
	 */
	public void changeDirection() {
		HashMap<String, ArrayList<String>> cardinals = new HashMap<String, ArrayList<String>>();
		
		cardinals.put("East", new ArrayList<String>(Arrays.asList("South")));
		cardinals.put("South", new ArrayList<String>(Arrays.asList("East", "West")));
		cardinals.put("West", new ArrayList<String>(Arrays.asList("South")));
		
		direction = cardinals.get(direction).get(new Random().nextInt(cardinals.get(direction).size()));
	}

	/**
	 * Moves the goose
	 * @param moveVal Distance to move Goose by
	 * @param backgroundOffset Offset from screen to course coordinates
	 */
	public void Move(float moveVal, int backgroundOffset) {

		boolean canGoEast, canGoWest;

		if(this.getX() > givenLane.GetLeftBoundary() && this.getX() + this.width < givenLane.GetRightBoundary()) {
			//Goose is within the lane boundaries.
			canGoEast = true;
			canGoWest = true;
		}
		else if(this.getX() <= givenLane.GetLeftBoundary()) {
			//Goose is on left boundary.
			canGoEast = true;
			canGoWest = false;
		}
		else {
			//Goose is on right boundary.
			canGoEast = false;
			canGoWest = true;
		}

		int randomMove = 20;
		if(new Random().nextInt(randomMove) == randomMove - 1) {
			changeDirection();
		}

		if(canGoEast && this.direction == "East") {
			this.setX(this.getX() + moveVal);
			if(backgroundOffset > 0 && backgroundOffset < 2160) {
				this.setY(this.getY() - moveVal);
			}
			
			//changeDirection();
		}
		else if(canGoWest && this.direction == "West") {
			this.setX(this.getX() - moveVal);
			if(backgroundOffset > 0 && backgroundOffset < 2160) {
				this.setY(this.getY() - moveVal);
			}
			//changeDirection();
		}

		else {
			this.setY(this.getY() - moveVal);
		}
		//this.setY(this.getY() + backgroundOffset);
	}
	
}
