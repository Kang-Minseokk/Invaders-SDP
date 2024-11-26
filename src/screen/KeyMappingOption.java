package screen;

import engine.Cooldown;
import engine.Core;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeyMappingOption extends Screen {
    List<String> actions; // 액션 목록
    public Map<String, Integer> keyMappings; // 현재 키 매핑 정보
    int selectedIndex; // 선택된 액션의 인덱스
    public boolean waitingForKeyInput; // 키 입력 대기 상태
    public Cooldown selectionCooldown; // 입력 쿨다운
    public static final int SELECTION_TIME = 120; // 쿨다운 시간(ms)
    String osType;

    public KeyMappingOption(int width, int height, int fps) {
        super(width, height, fps);
        osType = System.getProperty("os.name").toLowerCase();



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

    void handleVerticalMenuNavigation() {
        if (inputManager.isKeyDown(Core.getKeyCode("MOVE_UP"))) {
            previousOption(); // 이전 항목으로 이동
            inputManager.keyReset();
        }
        if (inputManager.isKeyDown(Core.getKeyCode("MOVE_DOWN"))) {
            nextOption(); // 다음 항목으로 이동
            inputManager.keyReset();
        }
    }

    public void handleKeyMappingSelection() {
        if (osType.contains("mac")) {
            if (inputManager.isKeyDown(KeyEvent.VK_META) && this.selectionCooldown.checkFinished()) {
                waitingForKeyInput = true; // 키 입력 대기 상태로 전환
                this.selectionCooldown.reset();
            }
        } else {
            if (inputManager.isKeyDown(KeyEvent.VK_CAPS_LOCK) && this.selectionCooldown.checkFinished()) {
                waitingForKeyInput = true; // 키 입력 대기 상태로 전환
                this.selectionCooldown.reset();
            }
        }
    }

    public void handleKeyInput() {
        if(waitingForKeyInput){
            for (int keyCode = 0; keyCode < 256; keyCode++) {
                if (inputManager.isKeyDown(keyCode) && this.selectionCooldown.checkFinished()) {
                    String selectedAction = actions.get(selectedIndex);
                    boolean check = true;

                    // 중복 키코드 검사
                    for(Integer val : keyMappings.values()){
                        if (keyCode == val) {
                            String duplicateMessage = "Duplicate key is not allowed! Try again.";
                            drawManager.drawCenteredRegularString(this, duplicateMessage, this.height / 8*2 - drawManager.fontRegularMetrics.getHeight()*2);
                            draw();
                            check = false;
                        }


                    }
                    // 커맨드 키(윈도우 키)가 Go back으로 설정되는 경우, 다시 키 설정 못하게 되는 경우를 방지를 위해서 157 키코드를 추가했어요.
                    // 강민석
                    if(keyCode == 20 || keyCode == 157)
                        check = false;

                    if (check){
                        Core.updateKeyMapping(selectedAction, keyCode); // Core에 키 매핑 업데이트
                        this.keyMappings = Core.getKeyMappings(); // 업데이트된 매핑 정보 가져오기
                        waitingForKeyInput = false; // 입력 대기 상태 해제
                        this.selectionCooldown.reset();
                        break;
                    }else{
                        waitingForKeyInput = false; // 입력 대기 상태 해제
                        this.selectionCooldown.reset();
                        break;
                    }

                }
            }
        }
    }

    void nextOption() {
        if (selectedIndex >= actions.size() - 1) {
            selectedIndex = 0; // 마지막 항목에서 첫 번째로 순환
        } else {
            selectedIndex++;
        }
    }

    void previousOption() {
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

        if (osType.contains("mac")) {
            drawManager.drawCenteredRegularString(this, "Press Command-key to Modify", this.height / 8*2 - drawManager.fontRegularMetrics.getHeight()*2);
        }else{
            drawManager.drawCenteredRegularString(this, "Press CapsLock to Modify", this.height / 8*2 - drawManager.fontRegularMetrics.getHeight()*2);
        }




        // 각 키 매핑 항목 표시
        for (int i = 0; i < actions.size(); i++) {
            String action = actions.get(i);
            String keyName = KeyEvent.getKeyText(keyMappings.get(action));
            // Mac OS에서 스페이스와 esc키의 모양이 나오지 않아서 코드를 추가했습니다.
            // 강민석
            if ("␣".equalsIgnoreCase(keyName)) {
                keyName = "space"; // 스페이스 키의 텍스트 변경
            } else if ("⎋".equalsIgnoreCase(keyName)) {
                keyName = "esc"; // ESC 키의 텍스트 변경
            }

            // 선택된 항목 강조
            if (i == selectedIndex) {
                drawManager.drawHighlightedString(this, action + " " + keyName, this.width / 4, this.height / 4 + i * 40 + drawManager.fontRegularMetrics.getHeight());
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
