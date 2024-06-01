import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.awt.event.KeyEvent;

public class GamePanelTest {

    @Test
    public void testInitialGameState() {
        GamePanel gp = new GamePanel();

        assertEquals(3, gp.getSnakeLength());
        assertEquals(0, gp.getScore());
        assertTrue(gp.isRunning());
    }

    @Test
    public void testMoveSnake() {
        GamePanel gp = new GamePanel();

        // Ensure the game is running and the direction is set correctly
        gp.setRunning(true);
        gp.setSnakeDirection(KeyEvent.VK_RIGHT);

        int initialX = gp.getSnakeX()[0];
        int initialY = gp.getSnakeY()[0];

        
        gp.move();

    
        assertEquals(initialX + gp.getUnitSize(), gp.getSnakeX()[0]);
        assertEquals(initialY, gp.getSnakeY()[0]);
    }

    @Test
    public void testCheckFoodCollision() {
        GamePanel gp = new GamePanel();

        gp.setFoodPosition(gp.getSnakeX()[0], gp.getSnakeY()[0]);
        int initialLength = gp.getSnakeLength();
        int initialScore = gp.getScore();

        gp.checkFood();

        assertEquals(initialLength + 1, gp.getSnakeLength());
        assertTrue(gp.getScore() > initialScore);
    }

    @Test
    public void testCheckSelfCollision() {
        GamePanel gp = new GamePanel();

        gp.setSnakePosition(0, 0);
        gp.getSnakeX()[1] = 0;
        gp.getSnakeY()[1] = 0;

        gp.checkCollisions();

        assertFalse(gp.isRunning());
    }

    @Test
    public void testCheckWallCollision() {
        GamePanel gp = new GamePanel();
        
        gp.setSnakePosition(-gp.getUnitSize(), gp.getSnakeY()[0]);

        gp.checkCollisions();

        assertFalse(gp.isRunning());
    }
    @Test
    public void testGetAndSetRunning() {
        GamePanel gp = new GamePanel();

        gp.setRunning(false);
        assertFalse(gp.isRunning());

        gp.setRunning(true);
        assertTrue(gp.isRunning());
    }
}
