package screen;

import engine.Core;

import java.awt.event.KeyEvent;

/**
 * Implements the pause screen.
 */
public class OptionScreen extends Screen {

    public OptionScreen(int width, int height, int fps) {
        super(width, height, fps);
        this.returnCode = 1; // Default return code for resuming the game
    }

    @Override
    public void update() {
        super.update();
        draw();

        if (inputManager.isKeyDown(KeyEvent.VK_P) && this.inputDelay.checkFinished()) {
            this.returnCode = 2; // GameScreen으로 돌아가기 위한 코드 (예시로 2)
            this.isRunning = false; // Exit the pause screen and return to the game
            Core.getLogger().info("P pressed: Exiting PauseScreen with returnCode 2.");
        }
    }

    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawOption(this);
        drawManager.completeDrawing(this);
    }
}
