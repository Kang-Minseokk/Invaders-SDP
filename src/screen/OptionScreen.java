package screen;

import engine.Core;
import engine.SoundManager;
import engine.Cooldown;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the pause screen.
 */
public class OptionScreen extends Screen {

    private List<String> bgmOptions;
    private SoundManager soundManager;
    private Cooldown selectionCooldown;
    private static final int SELECTION_TIME = 200;
    private int bgmState; // Tracks the current BGM option
    private final int maxBGMState = 2;

    public OptionScreen(int width, int height, int fps) {

        super(width, height, fps);
        this.returnCode = 1; // Default return code for resuming the game
        this.soundManager = SoundManager.getInstance();

        bgmOptions = new ArrayList<>();
        bgmOptions.add("inGame_bgm");
        bgmOptions.add("mainMenu_bgm");
        bgmOptions.add("highScore_bgm");

        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();

    }

    @Override
    public void update() {
        super.update();
        draw();

        if (this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()) {
            handleBGMCode();
        }

        if (inputManager.isKeyDown(KeyEvent.VK_P) && this.inputDelay.checkFinished()) {
            this.returnCode = 2; // GameScreen으로 돌아가기 위한 코드 (예시로 2)
            this.isRunning = false; // Exit the pause screen and return to the game
            Core.getLogger().info("P pressed: Exiting PauseScreen with returnCode 2.");
        }
    }

    private void handleBGMCode() {
        if (inputManager.isKeyDown(KeyEvent.VK_LEFT) || inputManager.isKeyDown(KeyEvent.VK_A)) {
            previousBGMState();
            this.selectionCooldown.reset();
            playSelectedBGM();
        }
        if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) || inputManager.isKeyDown(KeyEvent.VK_D)) {
            nextBGMState();
            this.selectionCooldown.reset();
            playSelectedBGM();
        }
    }

    private void nextBGMState() {
        if (this.bgmState == maxBGMState)
            this.bgmState = 0; // Wrap around to the first BGM option
        else
            this.bgmState++; // Move to the next BGM option
    }

    private void previousBGMState() {
        if (this.bgmState == 0)
            this.bgmState = maxBGMState; // Wrap around to the last BGM option
        else
            this.bgmState--; // Move to the previous BGM option
    }

    private void playSelectedBGM() {
        String selectedBGM = bgmOptions.get(bgmState);
        soundManager.stopAllBGM();
        soundManager.playBGM(selectedBGM);
        Core.getLogger().info("Playing selected BGM: " + selectedBGM);
    }

    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawOption(this);
        drawManager.drawBGMOption(this, bgmState, bgmOptions);
        drawManager.completeDrawing(this);

    }
}
