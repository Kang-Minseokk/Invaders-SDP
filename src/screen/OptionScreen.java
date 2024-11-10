package screen;

import engine.Core;

import java.awt.event.KeyEvent;
import engine.Cooldown;
import engine.SoundManager;


/**
 * Implements the pause screen.
 */
public class OptionScreen extends Screen {

    private int volumeSelectionCode;
    private Cooldown selectionCooldown;
    private static final int SELECTION_TIME = 200;
    private static SoundManager sm;

    public OptionScreen(int width, int height, int fps) {
        super(width, height, fps);
        this.volumeSelectionCode = Core.getSavedVolumeSelectionCode();
        this.returnCode = 1; // Default return code for resuming the game
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
        sm = SoundManager.getInstance();
    }

    private void handleHorizontalMenuNavigation() {
        if (inputManager.isKeyDown(KeyEvent.VK_LEFT) || inputManager.isKeyDown(KeyEvent.VK_A)) {
            if (this.volumeSelectionCode == 0)
                this.volumeSelectionCode = 5;

            else
                this.volumeSelectionCode--;
            sm.modifyAllBGMVolume(this.volumeSelectionCode);
            sm.modifyAllESVolume(this.volumeSelectionCode);
        }
        if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) || inputManager.isKeyDown(KeyEvent.VK_D)) {
            if (this.volumeSelectionCode == 5)
                this.volumeSelectionCode = 0;
            else
                this.volumeSelectionCode++;
            sm.modifyAllBGMVolume(this.volumeSelectionCode);
            sm.modifyAllESVolume(this.volumeSelectionCode);
        }
    }

    @Override
    public void update() {
        super.update();
        draw();

        if (inputManager.isKeyDown(KeyEvent.VK_P) && this.inputDelay.checkFinished()) {
            Core.setSavedVolumeSelectionCode(this.volumeSelectionCode);
            this.returnCode = 2; // GameScreen으로 돌아가기 위한 코드 (예시로 2)
            this.isRunning = false; // Exit the pause screen and return to the game
            Core.getLogger().info("P pressed: Exiting PauseScreen with returnCode 2.");
        }
        if(this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()){
            handleHorizontalMenuNavigation();
            this.selectionCooldown.reset();
        }

    }

    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawOption(this);
        drawManager.drawSoundControl(this,2,this.volumeSelectionCode);
        drawManager.completeDrawing(this);
    }
}
