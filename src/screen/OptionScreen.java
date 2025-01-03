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

    public List<String> bgmOptions;
    protected int bgmState; // Tracks the current BGM option
    public final int maxBGMState = 2;
    protected int volumeSelectionCode;
    public Cooldown selectionCooldown;
    public static final int SELECTION_TIME = 120;
    public static SoundManager sm;
    public GameScreen gameScreen;

    public static boolean isTestEnv = false;


    public static void setTestEnv(){
        isTestEnv = true;
    }

    public OptionScreen(int width, int height, int fps, GameScreen gamesc) {
        super(width, height, fps);
        this.volumeSelectionCode = Core.getSavedVolumeSelectionCode();
        this.returnCode = 1; // Default return code for resuming the game
        this.gameScreen = gamesc;
        if(!isTestEnv)
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

        if ((inputManager.isKeyDown(Core.getKeyCode("GO_BACK")) || inputManager.isKeyDown(Core.getKeyCode("PAUSE")) || inputManager.isKeyDown(KeyEvent.VK_Q))
                && this.inputDelay.checkFinished()) {
//            if(inputManager.isKeyDown(Core.getKeyCode("GO_BACK")))
//                inputManager.keyPressed(Core.getKeyCode("PAUSE"));

            Core.getLogger().info("Resuming GameScreen.");
//            Core.popScreen();
            Core.setSavedVolumeSelectionCode(this.volumeSelectionCode); // GameScreen으로 돌아가기 위한 코드\
            this.isRunning = false;
        }

        if (this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()) {
            handleVerticalMenuNavigation();
            handleBGMCode();
            handleVolumeCode();
            openKeyMapping();
            this.selectionCooldown.reset();
        }
    }

    public void handleVerticalMenuNavigation() {
        if (inputManager.isKeyDown(Core.getKeyCode("MOVE_UP"))) {
            Core.getLogger().info("Pressed uping to " + returnCode);
            previousOptionItem();
            this.selectionCooldown.reset();
        }
        if (inputManager.isKeyDown(Core.getKeyCode("MOVE_DOWN"))) {
            Core.getLogger().info("Pressed Downing to " + returnCode);
            nextOptionItem();
            this.selectionCooldown.reset();
        }
    }
    public void handleBGMCode() {
        if (this.returnCode == 1) {
            if (inputManager.isKeyDown(Core.getKeyCode("MOVE_LEFT"))) {
                previousBGMState();
                this.selectionCooldown.reset();
                playSelectedBGM();
                playMenuSelectSound();
            }
            if (inputManager.isKeyDown(Core.getKeyCode("MOVE_RIGHT"))) {
                nextBGMState();
                this.selectionCooldown.reset();
                playSelectedBGM();
                playMenuSelectSound();
            }
        }
    }

    public void handleVolumeCode() {
        if (this.returnCode == 2) {
            if (inputManager.isKeyDown(KeyEvent.VK_LEFT) || inputManager.isKeyDown(KeyEvent.VK_A)) {
                playMenuSelectSound();
                if (this.volumeSelectionCode == 0)
                    this.volumeSelectionCode = 5;

                else
                    this.volumeSelectionCode--;
                if(!isTestEnv){
                    sm.modifyAllBGMVolume(this.volumeSelectionCode);
                    sm.modifyAllESVolume(this.volumeSelectionCode);
                }
            }
            if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) || inputManager.isKeyDown(KeyEvent.VK_D)) {
                playMenuSelectSound();
                if (this.volumeSelectionCode == 5)
                    this.volumeSelectionCode = 0;
                else
                    this.volumeSelectionCode++;
                if(!isTestEnv){
                    sm.modifyAllBGMVolume(this.volumeSelectionCode);
                    sm.modifyAllESVolume(this.volumeSelectionCode);
                }
            }
        }
    }


    public void openKeyMapping() {
        if (this.returnCode == 3) {
            if (inputManager.isKeyDown(KeyEvent.VK_ENTER) || inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                Core.getLogger().info("Open Key Mapping");
                Core.pushScreen(new KeyMappingOption(this.width, this.height, this.fps));
                Core.getLogger().info("Close Key Mapping");
//                Core.getFrame().setScreen(new KeyMappingOption(this.width, this.height, this.fps));
            }
        }
    }
    public void nextOptionItem() {
        if (this.returnCode >= 3)
            this.returnCode = 1;
        else
            this.returnCode++;
        playMenuSelectSound();
    }

    public void previousOptionItem() {
        if (this.returnCode <= 1)
            this.returnCode = 3;
        else
            this.returnCode--;
        playMenuSelectSound();
    }

    private void playMenuSelectSound() {
        if(!isTestEnv)
            SoundManager.getInstance().playES("menuSelect_es");
    }

    public void nextBGMState() {
        if (this.bgmState == maxBGMState)
            this.bgmState = 0; // Wrap around to the first BGM option
        else
            this.bgmState++; // Move to the next BGM option
    }

    public void previousBGMState() {
        if (this.bgmState == 0)
            this.bgmState = maxBGMState; // Wrap around to the last BGM option
        else
            this.bgmState--; // Move to the previous BGM option
    }

    public void playSelectedBGM() {
        String selectedBGM = bgmOptions.get(bgmState);
        if(!isTestEnv){
            sm.stopAllBGM();
            sm.playBGM(selectedBGM);
        }
        Core.getLogger().info("Playing selected BGM: " + selectedBGM);
    }

    public void draw() {
        drawManager.initDrawing(this);
        drawManager.drawOption(this);
        drawManager.drawSoundOption(this, this.returnCode, this.volumeSelectionCode, this.bgmState, this.bgmOptions);
        drawManager.completeDrawing(this);
    }

}
