package com.dragonboat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * <p>
 * Game Class for Dragon Boat Game.
 * </p>
 * <p>
 * Initialises all the objects necessary for the game, starts music, creates
 * Lanes, randomises Obstacle spawns, initialises blank Player and Opponents,
 * initialises a Progress Bar and Leaderboard, and instantiates a Menu Screen.
 * </p>
 *
 * @see MenuScreen
 */
public class DragonBoatGame extends Game {

    // debug booleans
    protected boolean debug_speed = false;
    protected boolean debug_positions = false;
    protected boolean debug_norandom = false;
    protected boolean debug_verboseoutput = false;

    protected Random rnd;
    private MenuScreen menuScreen;
    public Lane[] lanes;
    public Player player;
    public Course course;
    public Opponent[] opponents;
    public ProgressBar progressBar;
    public Leaderboard leaderboard;
    public ArrayList<Integer>[] obstacleTimes;
    public ArrayList<Integer>[] powerUpTimes;
    public int noOfObstacles;
    public int noOfPowerUps;
    public int playerChoice;
    public int difficulty = 1;
    public Music music;
    public boolean ended = false;
    public FreeTypeFontGenerator generator;
    public FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private SpriteBatch batch;
    private BitmapFont font28;
    private Texture courseTexture;
    //used for loading a save
    public boolean save = false;
    public String level;


    /**
     * Sets up the game with settings and instantiation of objects.
     */
    @Override
    public void create() {
        int w = Gdx.graphics.getWidth() - 80;
        //moved from MenuScreen render method as is does not need to put in the background every time
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.3f, 1);


        if (debug_norandom) rnd = new Random(1);
        else rnd = new Random();

        setMusic();

        courseTexture = new Texture(Gdx.files.internal("background sprite.png"));
        lanes = new Lane[7];
        noOfObstacles = 8;
        noOfPowerUps = 4;

        obstacleTimes = new ArrayList[lanes.length];
        powerUpTimes = new ArrayList[lanes.length];

        /*
         * Instantiate each lane, and allocate obstacles and power-ups by creating a random sequence
         * of Y values for obstacles and power-ups to spawn at for each lane.
         */
        for (int x = 0; x < lanes.length; x++) {
            obstacleTimes[x] = new ArrayList<>();
            powerUpTimes[x] = new ArrayList<>();
            // new 40 is the offset
            lanes[x] = new Lane((x * w / lanes.length) + 40, (((x + 1) * w) / lanes.length) + 40);
            int maxY = (courseTexture.getHeight() - (5 * noOfObstacles)) / noOfObstacles;
            for (int y = 0; y < noOfObstacles; y++) {
                obstacleTimes[x].add(rnd.nextInt(maxY - 5) + 5 + maxY * y);
            }
            Collections.sort(obstacleTimes[x]);

            //"ASSESSMENT2:START"
            for (int z = 0; z < noOfPowerUps; z++) {
                powerUpTimes[x].add(rnd.nextInt(maxY - 5) + 5 + maxY * z);
            }
            Collections.sort(powerUpTimes[x]);
            //"ASSESSMENT2:END"
            if (debug_verboseoutput) {
                System.out.println("Lane " + x + " obstacles to spawn: ");
                for (Integer i : obstacleTimes[x]) {
                    System.out.print(i + ", ");
                }
                System.out.println();
            }
        }

        // Instantiate the course and player and opponent boats.
        course = new Course(courseTexture, lanes);
        player = new Player(0, 56, 182, lanes[3], "Player");

        opponents = new Opponent[6];
        for (int i = 0; i < opponents.length; i++) {
            /*
             * Ensure player is in the middle lane by skipping over lane 4.
             */
            int lane = i >= 3 ? i + 1 : i;
            opponents[i] = new Opponent(0, 56, 182, lanes[lane], "Opponent" + (i + 1));
        }

        // Instantiate the progress bar and leaderboard.
        progressBar = new ProgressBar(player, opponents);
        leaderboard = new Leaderboard(player, opponents);

        // Set up font.
        generator = new FreeTypeFontGenerator(Gdx.files.internal("8bitOperatorPlus-Regular.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 28;
        font28 = generator.generateFont(parameter);

        batch = new SpriteBatch();

        //"ASSESSMENT2:START"
        // Display the welcome screen.
        WelcomeScreen welcomeScreen = new WelcomeScreen(this);
        setScreen(welcomeScreen);
        //"ASSESSMENT2:END"
    }

    /**
     * Changes the screen to a new GameScreen and resets necessary attributes.
     */
    public void advanceLeg() {
        /*
         * Increase difficulty and set up next leg.
         */
        difficulty += 1;
        if (debug_norandom) rnd = new Random(1);
        else rnd = new Random();

        //"ASSESSMENT2:START"
        switch (level) {
            case "Easy":
                noOfObstacles += 1;
                break;
            case "Medium":
                noOfObstacles += 2;
                break;
            case "Hard":
                noOfObstacles = 3;
                break;
            default:
                noOfObstacles = 10;
        }
        //"ASSESSMENT2:END"

        obstacleTimes = new ArrayList[lanes.length];
        for (int x = 0; x < lanes.length; x++) {
            lanes[x].obstacles = new ArrayList<>();
            obstacleTimes[x] = new ArrayList<>();
            int maxY = (courseTexture.getHeight() - (5 * noOfObstacles)) / noOfObstacles;
            for (int y = 0; y < noOfObstacles; y++) {
                obstacleTimes[x].add(rnd.nextInt(maxY - 5) + 5 + maxY * y);
            }
            Collections.sort(obstacleTimes[x]);

            if (debug_verboseoutput) {
                System.out.println("Lane " + x + " obstacles to spawn: ");
                for (Integer i : obstacleTimes[x]) {
                    System.out.print(i + ", ");
                }
                System.out.println();
            }
        }

        //"ASSESSMENT2:START"
        noOfPowerUps = 4;

        powerUpTimes = new ArrayList[lanes.length];
        for (int x = 0; x < lanes.length; x++) {
            lanes[x].powerUps = new ArrayList<>();
            powerUpTimes[x] = new ArrayList<>();
            int maxY = (courseTexture.getHeight() - (5 * noOfPowerUps)) / noOfPowerUps;
            for (int y = 0; y < noOfPowerUps; y++) {
                powerUpTimes[x].add(rnd.nextInt(maxY - 5) + 5 + maxY * y);
            }
            Collections.sort(powerUpTimes[x]);
        }
        //"ASSESSMENT2:END"

        player.Reset();

        /*
         * Set up final leg.
         */
        if (difficulty == 4) {
            Boat[] finalists = leaderboard.getPodium();
            opponents = new Opponent[2];
            for (Boat b : finalists) {
                if (b.getName().startsWith("Opponent")) {
                    // set opponents lanes so that only the middle 3 lanes are used.
                    if (opponents[0] == null) {
                        opponents[0] = (Opponent) b;
                        b.setLane(lanes[2]);
                    } else {
                        opponents[1] = (Opponent) b;
                        b.setLane(lanes[4]);
                    }
                }
                b.ResetFastestLegTime();
            }
        }
        for (Opponent o : opponents) {
            o.Reset();
        }
        progressBar = new ProgressBar(player, opponents);
        setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        final DragonBoatGame game = this;
        /*
         * If the game hasn't ended, just call the current screen render function.
         */
        if (!this.ended)
            super.render();
        else {
            /*
             * Else, display an end screen and appropriate text and images.
             */
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            boolean playerWon = false;
            batch.begin();
            batch.draw(new Texture(Gdx.files.internal("end screen.png")), 0, 0);
            batch.end();
            Boat[] podium = leaderboard.getPodium();
            for (int i = 0; i < podium.length; i++) {
                /*
                 * If the player is in the top 3 boats, display the player's boat and
                 * appropriate medal.
                 */

                //"ASSESSMENT2:START"
                // new for if the boat broke
                if (player.getDurability() <= 0) {
                    batch.begin();
                    font28.draw(batch, "Sorry your boat is destroyed", 140, 250);
                    batch.end();
                    break;
                }
                //"ASSESSMENT2:END"
                if (podium[i].getName().startsWith("Player")) {
                    playerWon = true;
                    batch.begin();
                    batch.draw(player.texture, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 3);
                    batch.end();
                    switch (i) {
                        case 0:
                            batch.begin();
                            batch.draw(new Texture(Gdx.files.internal("medal gold.png")), Gdx.graphics.getWidth() / 3,
                                    Gdx.graphics.getHeight() / 3);
                            batch.end();
                            break;
                        case 1:
                            batch.begin();
                            batch.draw(new Texture(Gdx.files.internal("medal silver.png")), Gdx.graphics.getWidth() / 3,
                                    Gdx.graphics.getHeight() / 3);
                            batch.end();
                            break;
                        case 2:
                            batch.begin();
                            batch.draw(new Texture(Gdx.files.internal("medal bronze.png")), Gdx.graphics.getWidth() / 3,
                                    Gdx.graphics.getHeight() / 3);
                            batch.end();
                            break;
                    }
                    batch.begin();
                    font28.draw(batch, "Congratulations! You reached Super Saiyan!", 140, 140);
                    batch.end();
                }
            }
            if (!playerWon) {
                batch.begin();
                font28.draw(batch, "Unlucky, would you like to try again?", 140, 200);
                batch.end();
            }
        }
    }

    /**
     * Sets ended to true
     */
    public void endGame() {
        this.ended = true;

    }
    //"ASSESSMENT2:START"
    /**
     * Resizes the game screen.
     *
     * @param width  Width of the screen.
     * @param height Height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        this.getScreen().resize(width, height);
    }
    //"ASSESSMENT2:END"

    /**
     * Disposes of the current screen when it's no longer needed.
     */
    @Override
    public void dispose() {
        this.getScreen().dispose();
        batch.dispose();
        font28.dispose();

    }

    /**
     * Loads the game music and starts playing it on loop
     */
    public void setMusic(){
        music = Gdx.audio.newMusic(Gdx.files.internal("cantgobackwards.mp3"));
        music.setLooping(true);
        music.setVolume(0.4f);
        music.play();
    }
}
