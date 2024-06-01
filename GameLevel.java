class GameLevel {
    private final int levelNumber;
    private final int snakeSpeed;
    private final int initialSnakeLength;
    private final int requiredScoreToAdvance;

    public GameLevel(int levelNumber, int snakeSpeed, int initialSnakeLength, int requiredScoreToAdvance) {
        this.levelNumber = levelNumber;
        this.snakeSpeed = snakeSpeed;
        this.initialSnakeLength = initialSnakeLength;
        this.requiredScoreToAdvance = requiredScoreToAdvance;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public int getSnakeSpeed() {
        return snakeSpeed;
    }

    public int getInitialSnakeLength() {
        return initialSnakeLength;
    }

    public int getRequiredScoreToAdvance() {
        return requiredScoreToAdvance;
    }
}
