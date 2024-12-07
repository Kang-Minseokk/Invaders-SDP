package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import engine.Score;
import engine.Core;

/**
 *  Implements past user recorded score
 */
public class RecordScreen extends Screen {

    /** List of user recent scores */
    private List<Score> recentScores;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public RecordScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.returnCode = 1;

        try {
            this.recentScores = Core.getFileManager().loadRecentScores();
        } catch (NumberFormatException | IOException e) {
            logger.warning("Couldn't load records!");
        }
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    @Override
    public final int run() {
        super.run();

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    @Override
    protected final void update() {
        super.update();

        draw();
        if (inputManager.isKeyDown(KeyEvent.VK_SPACE)
                && this.inputDelay.checkFinished())
            this.isRunning = false;
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawRecordMenu(this);
        drawManager.drawRecentScores(this, this.recentScores);

        super.drawPost();
        drawManager.completeDrawing(this);
    }
}
