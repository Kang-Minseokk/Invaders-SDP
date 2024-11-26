package screen;

import engine.Core;

import java.awt.event.KeyEvent;
import engine.Cooldown;
import engine.SoundManager;
import engine.Frame;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the pause screen.
 */
public class OptionScreen extends Screen {

    private List<String> bgmOptions;
    private int bgmState; // Tracks the current BGM option
    private final int maxBGMState = 2;
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

        bgmOptions = new ArrayList<>();
        bgmOptions.add("inGame_bgm");
        bgmOptions.add("mainMenu_bgm");
        bgmOptions.add("highScore_bgm");

        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
    }

    @Override
    public final int run() {
        super.run(); // Screen 클래스의 run 메서드를 실행하여 업데이트 루프를 처리
        return this.returnCode; // returnCode를 반환하여 Core가 다음 스크린을 결정하게 함
    }

    @Override
    public void update() {
        super.update();
        draw();

        if ((inputManager.isKeyDown(KeyEvent.VK_P) || inputManager.isKeyDown(KeyEvent.VK_Q))
                && this.inputDelay.checkFinished()) {
            Core.setSavedVolumeSelectionCode(this.volumeSelectionCode); // GameScreen으로 돌아가기 위한 코드
            this.isRunning = false; // Exit the pause screen and return to the game
        }

        if(this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()){
            handleVerticalMenuNavigation();
            handleVolumeCode();
            handleBGMCode();
            this.selectionCooldown.reset();
        }

    }

    private void handleVerticalMenuNavigation() {
        if (inputManager.isKeyDown(KeyEvent.VK_UP) || inputManager.isKeyDown(KeyEvent.VK_W)) {
            previousOptionItem();
            this.selectionCooldown.reset();
        }
        if (inputManager.isKeyDown(KeyEvent.VK_DOWN) || inputManager.isKeyDown(KeyEvent.VK_S)) {
            nextOptionItem();
            this.selectionCooldown.reset();
        }
    }

    private void handleBGMCode() {
        if (this.returnCode == 1) {
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
    }

    private void handleVolumeCode() {
        if (this.returnCode == 2) {
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
    }

    private void nextOptionItem() {
        if (this.returnCode >= 2)
            this.returnCode = 1;
        else
            this.returnCode++;
    }

    private void previousOptionItem() {
        if (this.returnCode <= 1)
            this.returnCode = 2;
        else
            this.returnCode--;
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
        sm.stopAllBGM();
        sm.playBGM(selectedBGM);
        Core.getLogger().info("Playing selected BGM: " + selectedBGM);
    }

    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawOption(this);
        drawManager.drawSoundOption(this, this.returnCode,this.volumeSelectionCode, this.bgmState, this.bgmOptions);
        drawManager.completeDrawing(this);
    }
    
}
