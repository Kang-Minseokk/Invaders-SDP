package screen;

import engine.Cooldown;
import engine.Core;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeyMappingOption extends Screen {
    private List<String> actions; // 액션 목록
    private Map<String, Integer> keyMappings; // 현재 키 매핑 정보
    private int selectedIndex; // 선택된 액션의 인덱스
    private boolean waitingForKeyInput; // 키 입력 대기 상태
    private Cooldown selectionCooldown; // 입력 쿨다운
    private static final int SELECTION_TIME = 120; // 쿨다운 시간(ms)

    public KeyMappingOption(int width, int height, int fps) {
        super(width, height, fps);

        this.actions = new ArrayList<>(Core.getKeyMappings().keySet()); // Core에서 액션 목록 가져오기
        this.keyMappings = Core.getKeyMappings(); // Core에서 현재 키 매핑 정보 가져오기
        this.selectedIndex = 0;
        this.waitingForKeyInput = false;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
    }

    @Override
    public final int run() {
        super.run();
        return this.returnCode;
    }

    @Override
    public void update() {
        super.update();
        draw();

        if (waitingForKeyInput) {
            handleKeyInput(); // 키 입력 대기 상태 처리
        } else {
            if ((inputManager.isKeyDown(Core.getKeyCode("GO_BACK")) || inputManager.isKeyDown(KeyEvent.VK_BACK_SPACE)) && this.selectionCooldown.checkFinished()) {
                Core.getLogger().info("Go back to OptionScreen.");
                inputManager.keyReleased(KeyEvent.VK_ESCAPE);
                this.isRunning = false; // 옵션 화면으로 돌아감a
            }

            if (this.selectionCooldown.checkFinished()) {
                handleVerticalMenuNavigation(); // 메뉴 탐색 처리
                handleKeyMappingSelection(); // 키 매핑 선택 처리
                this.selectionCooldown.reset();
            }
        }
    }

    private void handleVerticalMenuNavigation() {
        if (inputManager.isKeyDown(Core.getKeyCode("MOVE_UP"))) {
            previousOption(); // 이전 항목으로 이동
        }
        if (inputManager.isKeyDown(Core.getKeyCode("MOVE_DOWN"))) {
            nextOption(); // 다음 항목으로 이동
        }
    }

    private void handleKeyMappingSelection() {
        if (inputManager.isKeyDown(KeyEvent.VK_CAPS_LOCK) && this.selectionCooldown.checkFinished()) {
            waitingForKeyInput = true; // 키 입력 대기 상태로 전환
            this.selectionCooldown.reset();
        }
    }

    private void handleKeyInput() {
        for (int keyCode = 0; keyCode < 256; keyCode++) {
            if (inputManager.isKeyDown(keyCode) && this.selectionCooldown.checkFinished()) {
                String selectedAction = actions.get(selectedIndex);
                Core.updateKeyMapping(selectedAction, keyCode); // Core에 키 매핑 업데이트
                this.keyMappings = Core.getKeyMappings(); // 업데이트된 매핑 정보 가져오기
                waitingForKeyInput = false; // 입력 대기 상태 해제
                this.selectionCooldown.reset();
                break;
            }
        }
    }

    private void nextOption() {
        if (selectedIndex >= actions.size() - 1) {
            selectedIndex = 0; // 마지막 항목에서 첫 번째로 순환
        } else {
            selectedIndex++;
        }
    }

    private void previousOption() {
        if (selectedIndex <= 0) {
            selectedIndex = actions.size() - 1; // 첫 번째 항목에서 마지막으로 순환
        } else {
            selectedIndex--;
        }
    }

    public void draw() {
        drawManager.initDrawing(this);

        // 제목
        drawManager.backBufferGraphics.setColor(Color.GREEN);
        drawManager.drawCenteredBigString(this, "KEY MAPPING", this.height / 8);

        drawManager.backBufferGraphics.setColor(Color.GRAY);
        drawManager.drawCenteredRegularString(this, "Press CapsLock to Modify", this.height / 8*2 - drawManager.fontRegularMetrics.getHeight()*2);

        // 각 키 매핑 항목 표시
        for (int i = 0; i < actions.size(); i++) {
            String action = actions.get(i);
            String keyName = KeyEvent.getKeyText(keyMappings.get(action));

            // 선택된 항목 강조
            if (i == selectedIndex) {
                drawManager.drawHighlightedString(this, action + ": " + keyName, this.width / 4, this.height / 4 + i * 40 + drawManager.fontRegularMetrics.getHeight());
            } else {
                drawManager.drawString(this, action + ": " + keyName, this.width / 4, this.height / 4 + i * 40 + drawManager.fontRegularMetrics.getHeight());
            }
        }

        // 키 입력 대기 상태에서 안내 메시지 표시
        if (waitingForKeyInput) {
            drawManager.backBufferGraphics.setColor(Color.white);
            drawManager.drawCenteredRegularString(this, "Press a key to map", this.height / 4 + actions.size()*40 + drawManager.fontRegularMetrics.getHeight()*2);
        }

        drawManager.completeDrawing(this);
    }
}
