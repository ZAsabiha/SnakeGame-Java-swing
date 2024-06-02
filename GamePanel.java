import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.sound.sampled.*;

class GamePanel extends JPanel implements ActionListener {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 500;
    private static final int UNIT_SIZE = 30;
    private static final int GAME_UNITS = (WIDTH / UNIT_SIZE) * (HEIGHT / UNIT_SIZE);
    Image background;
    private GameLevel[] levels = {
            new GameLevel(1, 300, 3, 10),
            new GameLevel(2, 250, 4, 15),
            new GameLevel(3, 200, 5, 20)
    };
    private int currentLevelIndex = 0;

    private Timer timer;
    private boolean running = false;
    private boolean gameWon = false;
    private boolean levelCompleted = false;
    private int snakeLength;
    private int snakeSpeed;
    private int[] snakeX = new int[GAME_UNITS];
    private int[] snakeY = new int[GAME_UNITS];
    private int snakeDirection = KeyEvent.VK_RIGHT;
    private int score = 0;

    private int foodX;
    private int foodY;

    private final Color[] foodColors = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE,
            Color.MAGENTA };
    private final int[] foodColorPoints = { 1, 2, 3, 4, 5, 6 };
    private Color foodColor;

    private Random random = new Random();

    private Clip backgroundMusicClip;
    private Clip eatAppleSoundClip;
    private Clip levelCompleteSoundClip;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        background = new ImageIcon("C:/Users/Fiza/Downloads/images.jpg").getImage();

        playBackgroundMusic("C:/Users/Fiza/Music/sound.wav");
        loadSoundEffects();
        initLevel();
    }

    // Getters and Setters for Unit Testing
    public int getSnakeLength() {
        return snakeLength;
    }

    public void setSnakeLength(int snakeLength) {
        this.snakeLength = snakeLength;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getUnitSize() {
        return UNIT_SIZE;
    }

    public int[] getSnakeX() {
        return snakeX;
    }

    public void setSnakeX(int[] snakeX) {
        this.snakeX = snakeX;
    }

    public int[] getSnakeY() {
        return snakeY;
    }

    public void setSnakeY(int[] snakeY) {
        this.snakeY = snakeY;
    }

    public int getSnakeDirection() {
        return snakeDirection;
    }

    public void setSnakeDirection(int snakeDirection) {
        this.snakeDirection = snakeDirection;
    }

    public void setFoodPosition(int foodX, int foodY) {
        this.foodX = foodX;
        this.foodY = foodY;
    }

    public void setSnakePosition(int x, int y) {
        this.snakeX[0] = x;
        this.snakeY[0] = y;
    }

    private void loadSoundEffects() {
        eatAppleSoundClip = loadSoundClip("C:/Users/Fiza/Downloads/demo1.wav");
        levelCompleteSoundClip = loadSoundClip("C:/Users/Fiza/Downloads/dwmo2wav.wav");
    }

    private Clip loadSoundClip(String soundFileName) {
        try {
            File file = new File(soundFileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void initLevel() {
        if (currentLevelIndex < levels.length) {
            GameLevel currentLevel = levels[currentLevelIndex];
            snakeLength = currentLevel.getInitialSnakeLength();
            snakeSpeed = currentLevel.getSnakeSpeed();
            snakeDirection = KeyEvent.VK_RIGHT;

            int centerX = (WIDTH / 2) / UNIT_SIZE * UNIT_SIZE;
            int centerY = (HEIGHT / 2) / UNIT_SIZE * UNIT_SIZE;

            for (int i = 0; i < snakeLength; i++) {
                snakeX[i] = centerX - (i * UNIT_SIZE);
                snakeY[i] = centerY;
            }

            spawnFood();

            timer = new Timer(snakeSpeed, this);
            timer.start();
            running = true;
            levelCompleted = false;
            score = 0;
        }
    }

    private void spawnFood() {
        foodX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        foodColor = foodColors[random.nextInt(foodColors.length)];

        System.out.println("Food spawned at: (" + foodX + ", " + foodY + ")");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
            repaint();

            GameLevel currentLevel = levels[currentLevelIndex];
            if (score >= currentLevel.getRequiredScoreToAdvance()) {
                levelCompleted = true;
                running = false;
                timer.stop();
                playSound(levelCompleteSoundClip);
                if (currentLevelIndex == levels.length - 1) {
                    gameWon = true;
                }
            }
        }
    }

    public void move() {
        for (int i = snakeLength; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (snakeDirection) {
            case KeyEvent.VK_UP:
                snakeY[0] -= UNIT_SIZE;
                break;
            case KeyEvent.VK_DOWN:
                snakeY[0] += UNIT_SIZE;
                break;
            case KeyEvent.VK_LEFT:
                snakeX[0] -= UNIT_SIZE;
                break;
            case KeyEvent.VK_RIGHT:
                snakeX[0] += UNIT_SIZE;
                break;
        }

        // Correct the snake's head position to stay within the game bounds
        if (snakeX[0] < 0) {
            snakeX[0] = 0;
        } else if (snakeX[0] >= WIDTH) {
            snakeX[0] = WIDTH - UNIT_SIZE;
        }
        if (snakeY[0] < 0) {
            snakeY[0] = 0;
        } else if (snakeY[0] >= HEIGHT) {
            snakeY[0] = HEIGHT - UNIT_SIZE;
        }
        System.out.printf("Snake's head at (%d,%d); food at (%d,%d)\n", snakeX[0], snakeY[0], foodX, foodY);
    }

    public void checkFood() {
        if (snakeX[0] == foodX && snakeY[0] == foodY) {
            snakeLength++;

            for (int i = 0; i < foodColors.length; i++) {
                if (foodColor.equals(foodColors[i])) {
                    score += foodColorPoints[i];
                    break;
                }
            }

            playSound(eatAppleSoundClip);
            spawnFood();
        }
    }

    public void checkCollisions() {
        for (int i = snakeLength; i > 0; i--) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                running = false;
                timer.stop();
                stopBackgroundMusic();
            }
        }

        if (snakeX[0] < 0 || snakeX[0] >= WIDTH || snakeY[0] < 0 || snakeY[0] >= HEIGHT) {
            running = false;
            timer.stop();
            stopBackgroundMusic();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        g2D.drawImage(background, 0, 0, null);

        if (running) {
            g.setColor(foodColor);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.setColor(Color.blue);
                } else {
                    g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                }
                g.fillRect(snakeX[i], snakeY[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.red);
            g.setFont(new Font("Arial Black", Font.BOLD, 35));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + score, (WIDTH - metrics.stringWidth("Score: " + score)) / 2,
                    g.getFont().getSize() * 2);
            g.drawString("Level: " + levels[currentLevelIndex].getLevelNumber(), WIDTH - 200,
                    g.getFont().getSize() * 2);
        } else if (levelCompleted) {
            displayLevelCompletion(g);
        } else {
            displayGameOver(g);
        }
    }

    private void displayLevelCompletion(Graphics g) {
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial Black", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());

        if (currentLevelIndex == levels.length - 1 && levelCompleted) {
            g.drawString("Congratulations!", (WIDTH - metrics.stringWidth("Congratulations!")) / 2, HEIGHT / 2 - 50);
            g.drawString("You Completed the Game!",
                    (WIDTH - metrics.stringWidth("You Completed the Game!")) / 2, HEIGHT / 2);
        } else {
            g.drawString("Congratulations!", (WIDTH - metrics.stringWidth("Congratulations!")) / 2, HEIGHT / 2 - 50);
            g.drawString("You Proceeded to the Next Level",
                    (WIDTH - metrics.stringWidth("You Proceeded to the Next Level")) / 2, HEIGHT / 2);
            g.setFont(new Font("Arial Black", Font.BOLD, 30));
            g.drawString("Press any key to continue",
                    (WIDTH - metrics.stringWidth("Press any key to continue")) / 2, HEIGHT / 2 + 100);
        }
    }

    private void displayGameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Arial Black", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        if (gameWon) {
            g.drawString("You Won!", (WIDTH - metrics.stringWidth("You Won!")) / 2, HEIGHT / 2);
        } else {
            g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);
        }

        g.setFont(new Font("Arial Black", Font.BOLD, 36));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + score, (WIDTH - metrics.stringWidth("Score: " + score)) / 2, HEIGHT / 2 + 50);
    }

    private void playBackgroundMusic(String soundFileName) {
        backgroundMusicClip = loadSoundClip(soundFileName);
        if (backgroundMusicClip != null) {
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
        }
    }

    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (running) {
                handleDirectionChange(e);
            } else if (levelCompleted) {
                if (gameWon) {
                    currentLevelIndex = 0;
                    gameWon = false;
                } else {
                    currentLevelIndex++;
                }
                initLevel();
            } else {
                currentLevelIndex = 0;
                initLevel();
            }
        }

        private void handleDirectionChange(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (snakeDirection != KeyEvent.VK_DOWN) {
                        snakeDirection = KeyEvent.VK_UP;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (snakeDirection != KeyEvent.VK_UP) {
                        snakeDirection = KeyEvent.VK_DOWN;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (snakeDirection != KeyEvent.VK_RIGHT) {
                        snakeDirection = KeyEvent.VK_LEFT;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (snakeDirection != KeyEvent.VK_LEFT) {
                        snakeDirection = KeyEvent.VK_RIGHT;
                    }
                    break;
            }
        }
    }
}
