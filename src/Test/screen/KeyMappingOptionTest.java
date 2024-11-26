package screen;

import org.junit.jupiter.api.*;
import engine.Core;
import engine.InputManager;
import engine.Cooldown;

import java.awt.event.KeyEvent;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;


public class KeyMappingOptionTest {

    private KeyMappingOption keyMappingOption;
    private InputManager mockInputManager;
    private Map<String, Integer> mockKeyMappings;
    private List<String> mapIndex;
    private Cooldown mockSelectionCooldown;
    private static boolean checksum = true;

    @BeforeEach
    void setUp() {
        mockInputManager = mock(InputManager.class);
        mockSelectionCooldown = mock(Cooldown.class);

        // KeyMappings를 모의 데이터로 설정
        mockKeyMappings = new HashMap<>();
        mockKeyMappings.put("MOVE_UP", KeyEvent.VK_W);
        mockKeyMappings.put("MOVE_DOWN", KeyEvent.VK_S);
        mockKeyMappings.put("MOVE_LEFT", KeyEvent.VK_A);
        mockKeyMappings.put("MOVE_RIGHT", KeyEvent.VK_D);
        mockKeyMappings.put("SHOOT", KeyEvent.VK_SPACE);
        mockKeyMappings.put("PAUSE", KeyEvent.VK_P);
        mockKeyMappings.put("GO_BACK", KeyEvent.VK_ESCAPE);

        // 0:Pause, 1:MOVE_UP, 2:MOVE_DOWN, 3:MOVE_LEFT, 4:MOVE_RIGHT, 5:SHOOT, 6:GO_BACK
        mapIndex = new ArrayList<>(mockKeyMappings.keySet());

        // Core 클래스에서 키 매핑 정보를 가져오지 않고 직접 데이터 설정
        keyMappingOption = new KeyMappingOption(800, 600, 60);
        keyMappingOption.keyMappings = mockKeyMappings; // KeyMappings 주입
        keyMappingOption.inputManager = mockInputManager; // InputManager Mock 설정
        keyMappingOption.selectionCooldown = mockSelectionCooldown;
        keyMappingOption.actions = new ArrayList<>(mockKeyMappings.keySet());
    }

    @AfterAll
    static void final_check(){
        if(checksum){
            System.out.println("All processes terminated successfully");
        }
    }


    @Nested
    class testHandleVerticalMenuNavigation{
        @AfterEach
        void trackTestResult(TestInfo testInfo){
            checksum = checksum && !testInfo.getTags().contains("FAILED");
        }

        @Test
        void testMoveUp() {
            System.out.println("test MOVE_UP");
            for(int i = 0; i < mockKeyMappings.size(); i++){
                // 위로 이동 키 눌림 상태 모의
                System.out.print((i+1)+" > MOVE_UP > ");
                when(mockInputManager.isKeyDown(Core.getKeyCode("MOVE_UP"))).thenReturn(true);

                int initialIndex = keyMappingOption.selectedIndex;
                keyMappingOption.handleVerticalMenuNavigation();
                assertEquals((initialIndex - 1 + mockKeyMappings.size()) % mockKeyMappings.size(), keyMappingOption.selectedIndex);
            }
        }

        @Test
        void testMoveDown() {
            System.out.println("test MOVE_DOWN");
            int initialIndex;

            for(int i = 0; i < mockKeyMappings.size(); i++){
                System.out.print((i+1)+" > MOVE_DOWN > ");
                when(mockInputManager.isKeyDown(Core.getKeyCode("MOVE_DOWN"))).thenReturn(true);

                initialIndex = keyMappingOption.selectedIndex;
                keyMappingOption.handleVerticalMenuNavigation();
                System.out.println("need: "+(initialIndex + 1) % mockKeyMappings.size()+"    real: "+keyMappingOption.selectedIndex);
                assertEquals((initialIndex + 1) % mockKeyMappings.size(), keyMappingOption.selectedIndex);
            }
        }

        @Test
        void testMoveRandom(){
            System.out.println("test MOVE Randomly");
            Random ran = new Random();

            int randInt;
            int initialIndex;

            for(int i = 0; i < 100; i++){
                randInt = ran.nextInt(2);
                if(randInt == 0){   // move up
                    System.out.print((i+1)+" > MOVE_UP > ");
                    when(mockInputManager.isKeyDown(Core.getKeyCode("MOVE_UP"))).thenReturn(true);
                    when(mockInputManager.isKeyDown(Core.getKeyCode("MOVE_DOWN"))).thenReturn(false);

                    initialIndex = keyMappingOption.selectedIndex;
                    keyMappingOption.handleVerticalMenuNavigation();
                    System.out.println("need: "+(initialIndex - 1 + mockKeyMappings.size()) % mockKeyMappings.size()+"   real: "+keyMappingOption.selectedIndex);
                    assertEquals((initialIndex - 1 + mockKeyMappings.size()) % mockKeyMappings.size(), keyMappingOption.selectedIndex);
                }else{  //move down
                    System.out.print((i+1)+" > MOVE_DOWN > ");
                    when(mockInputManager.isKeyDown(Core.getKeyCode("MOVE_UP"))).thenReturn(false);
                    when(mockInputManager.isKeyDown(Core.getKeyCode("MOVE_DOWN"))).thenReturn(true);

                    initialIndex = keyMappingOption.selectedIndex;
                    keyMappingOption.handleVerticalMenuNavigation();
                    System.out.println("need: "+(initialIndex + 1) % mockKeyMappings.size()+"    real: "+keyMappingOption.selectedIndex);
                    assertEquals((initialIndex + 1) % mockKeyMappings.size(), keyMappingOption.selectedIndex);
                }
            }


        }

    }


    @Nested
    class testHandleKeyMappingSelection{
        @AfterEach
        void trackTestResult(TestInfo testInfo){
            checksum = checksum && !testInfo.getTags().contains("FAILED");
        }

        @Test
        void testMac() {
            keyMappingOption.osType = "mac";

            when(mockInputManager.isKeyDown(KeyEvent.VK_META)).thenReturn(true);
            when(mockSelectionCooldown.checkFinished()).thenReturn(true);

            keyMappingOption.handleKeyMappingSelection();

            assertTrue(keyMappingOption.waitingForKeyInput);
            verify(mockSelectionCooldown).reset();
        }

        @Test
        void testMacButNotPressed() {
            keyMappingOption.osType = "mac";

            when(mockInputManager.isKeyDown(KeyEvent.VK_META)).thenReturn(false);
            when(mockSelectionCooldown.checkFinished()).thenReturn(true);

            keyMappingOption.handleKeyMappingSelection();

            assertFalse(keyMappingOption.waitingForKeyInput);
            verify(mockSelectionCooldown, never()).reset();
        }

        @Test
        void testMacButNotCooldown() {
            keyMappingOption.osType = "mac";

            when(mockInputManager.isKeyDown(KeyEvent.VK_META)).thenReturn(true);
            when(mockSelectionCooldown.checkFinished()).thenReturn(false);

            keyMappingOption.handleKeyMappingSelection();

            assertFalse(keyMappingOption.waitingForKeyInput);
            verify(mockSelectionCooldown, never()).reset();
        }

        @Test
        void testNotMac() {
            keyMappingOption.osType = "windows";

            when(mockInputManager.isKeyDown(KeyEvent.VK_CAPS_LOCK)).thenReturn(true);
            when(mockSelectionCooldown.checkFinished()).thenReturn(true);

            keyMappingOption.handleKeyMappingSelection();

            assertTrue(keyMappingOption.waitingForKeyInput);
            verify(mockSelectionCooldown).reset();
        }

        @Test
        void testNotMacButNotPressed() {
            keyMappingOption.osType = "windows";

            when(mockInputManager.isKeyDown(KeyEvent.VK_CAPS_LOCK)).thenReturn(false);
            when(mockSelectionCooldown.checkFinished()).thenReturn(true);

            keyMappingOption.handleKeyMappingSelection();

            assertFalse(keyMappingOption.waitingForKeyInput);
            verify(mockSelectionCooldown, never()).reset();
        }

        @Test
        void testNotMacButNotCooldown() {
            keyMappingOption.osType = "windows";

            when(mockInputManager.isKeyDown(KeyEvent.VK_CAPS_LOCK)).thenReturn(true);
            when(mockSelectionCooldown.checkFinished()).thenReturn(false);

            keyMappingOption.handleKeyMappingSelection();

            assertFalse(keyMappingOption.waitingForKeyInput);
            verify(mockSelectionCooldown, never()).reset();
        }

        @Test
        void testMacInNotMac(){
            keyMappingOption.osType = "windows";

            when(mockInputManager.isKeyDown(KeyEvent.VK_META)).thenReturn(true);
            when(mockSelectionCooldown.checkFinished()).thenReturn(true);

            keyMappingOption.handleKeyMappingSelection();

            assertFalse(keyMappingOption.waitingForKeyInput);
            verify(mockSelectionCooldown, never()).reset();
        }

        @Test
        void testNotMacInMac(){
            keyMappingOption.osType = "mac";

            when(mockInputManager.isKeyDown(KeyEvent.VK_CAPS_LOCK)).thenReturn(true);
            when(mockSelectionCooldown.checkFinished()).thenReturn(true);

            keyMappingOption.handleKeyMappingSelection();

            assertFalse(keyMappingOption.waitingForKeyInput);
            verify(mockSelectionCooldown, never()).reset();
        }
    }

    @Nested
    class testHandleKeyInput{
        // 인덱스별 옵션
        // 0:Pause, 1:MOVE_UP, 2:MOVE_DOWN, 3:MOVE_LEFT, 4:MOVE_RIGHT, 5:SHOOT, 6:GO_BACK
        @Test
        void testBasicWithLoop() {
            // Static 메서드 모의
            try (MockedStatic<Core> mockedCore = mockStatic(Core.class)) {
                // Core.updateKeyMapping 호출 여부 모의
                mockedCore.when(() -> Core.updateKeyMapping(anyString(), anyInt())).thenAnswer(invocation -> {
                    // 모의 동작: KeyMapping 업데이트
                    String action = invocation.getArgument(0);
                    int keyCode = invocation.getArgument(1);
                    System.out.println("Mocked updateKeyMapping called with: " + action + ", " + keyCode);
                    mockKeyMappings.put(action, keyCode); // 업데이트된 매핑
                    return null;
                });

                // Core.getKeyMappings 모의
                mockedCore.when(Core::getKeyMappings).thenReturn(mockKeyMappings);

                // index를 0부터 6까지 순회
                for (int index = 0; index <= 6; index++) {
                    // Key 설정
                    int mockKeyCode = index;
                    String selectedAction = mapIndex.get(index); // 각 index에 해당하는 액션
                    keyMappingOption.waitingForKeyInput = true;
                    keyMappingOption.selectedIndex = index;

                    // 조건 설정: 키코드 입력 및 쿨다운 완료
                    when(mockInputManager.isKeyDown(mockKeyCode)).thenReturn(true);
                    when(mockSelectionCooldown.checkFinished()).thenReturn(true);

                    // 메서드 실행
                    keyMappingOption.handleKeyInput();

                    // 검증: 매핑이 업데이트되었는지 확인
                    assertEquals(mockKeyCode, mockKeyMappings.get(selectedAction)); // 매핑 업데이트 확인
                    assertFalse(keyMappingOption.waitingForKeyInput); // 입력 대기 상태 해제 확인
                    verify(mockSelectionCooldown).reset(); // 쿨다운 리셋 호출 확인

                    // Core.updateKeyMapping 호출 여부 확인
                    mockedCore.verify(() -> Core.updateKeyMapping(selectedAction, mockKeyCode), times(1));

                    // 상태 초기화
                    reset(mockInputManager);
                    reset(mockSelectionCooldown);
                }
            }
        }

        @Test
        void testIfNotCheckFinished(){
            // Static 메서드 모의
            try (MockedStatic<Core> mockedCore = mockStatic(Core.class)) {
                // Core.updateKeyMapping 호출 여부 모의
                mockedCore.when(() -> Core.updateKeyMapping(anyString(), anyInt())).thenAnswer(invocation -> {
                    // 모의 동작: KeyMapping 업데이트
                    String action = invocation.getArgument(0);
                    int keyCode = invocation.getArgument(1);
                    System.out.println("Mocked updateKeyMapping called with: " + action + ", " + keyCode);
                    mockKeyMappings.put(action, keyCode); // 업데이트된 매핑
                    return null;
                });

                // Core.getKeyMappings 모의
                mockedCore.when(Core::getKeyMappings).thenReturn(mockKeyMappings);

                // index를 0부터 6까지 순회
                for (int index = 0; index <= 6; index++) {
                    // Key 설정
                    int mockKeyCode = index;
                    String selectedAction = mapIndex.get(index); // 각 index에 해당하는 액션
                    keyMappingOption.waitingForKeyInput = true;
                    keyMappingOption.selectedIndex = index;

                    int prevKeyCode = mockKeyMappings.get(selectedAction);

                    // 조건 설정: 키코드 입력 및 쿨다운 완료
                    when(mockInputManager.isKeyDown(mockKeyCode)).thenReturn(true);
                    when(mockSelectionCooldown.checkFinished()).thenReturn(false);

                    // 메서드 실행
                    keyMappingOption.handleKeyInput();

                    // 검증: 매핑이 업데이트되었는지 확인
                    assertEquals(prevKeyCode, mockKeyMappings.get(selectedAction)); // 매핑 업데이트 확인
                    assertTrue(keyMappingOption.waitingForKeyInput); // 입력 대기 상태 해제 확인
                    verify(mockSelectionCooldown, never()).reset(); // 쿨다운 리셋 호출 확인

                    // Core.updateKeyMapping 호출 여부 확인
                    mockedCore.verify(() -> Core.updateKeyMapping(selectedAction, mockKeyCode), times(0));

                    // 상태 초기화
                    reset(mockInputManager);
                    reset(mockSelectionCooldown);
                }
            }
        }

        @Test
        void testIfNotWaitingForKeyInput(){
            // Static 메서드 모의
            try (MockedStatic<Core> mockedCore = mockStatic(Core.class)) {
                // Core.updateKeyMapping 호출 여부 모의
                mockedCore.when(() -> Core.updateKeyMapping(anyString(), anyInt())).thenAnswer(invocation -> {
                    // 모의 동작: KeyMapping 업데이트
                    String action = invocation.getArgument(0);
                    int keyCode = invocation.getArgument(1);
                    System.out.println("Mocked updateKeyMapping called with: " + action + ", " + keyCode);
                    mockKeyMappings.put(action, keyCode); // 업데이트된 매핑
                    return null;
                });

                // Core.getKeyMappings 모의
                mockedCore.when(Core::getKeyMappings).thenReturn(mockKeyMappings);

                // index를 0부터 6까지 순회
                for (int index = 0; index <= 6; index++) {
                    // Key 설정
                    int mockKeyCode = index;
                    String selectedAction = mapIndex.get(index); // 각 index에 해당하는 액션
                    keyMappingOption.waitingForKeyInput = false;
                    keyMappingOption.selectedIndex = index;

                    int prevKeyCode = mockKeyMappings.get(selectedAction);

                    // 조건 설정: 키코드 입력 및 쿨다운 완료
                    when(mockInputManager.isKeyDown(mockKeyCode)).thenReturn(true);
                    when(mockSelectionCooldown.checkFinished()).thenReturn(true);

                    // 메서드 실행
                    keyMappingOption.handleKeyInput();

                    // 검증: 매핑이 업데이트되었는지 확인
                    assertEquals(prevKeyCode, mockKeyMappings.get(selectedAction)); // 매핑 업데이트 확인
                    assertFalse(keyMappingOption.waitingForKeyInput); // 입력 대기 상태 해제 확인
                    verify(mockSelectionCooldown, never()).reset(); // 쿨다운 리셋 호출 확인

                    // Core.updateKeyMapping 호출 여부 확인
                    mockedCore.verify(() -> Core.updateKeyMapping(selectedAction, mockKeyCode), times(0));

                    // 상태 초기화
                    reset(mockInputManager);
                    reset(mockSelectionCooldown);
                }
            }
        }

        @Test
        void testDupblicateKeyInput(){
            // Static 메서드 모의
            try (MockedStatic<Core> mockedCore = mockStatic(Core.class)) {
                // Core.updateKeyMapping 호출 여부 모의
                mockedCore.when(() -> Core.updateKeyMapping(anyString(), anyInt())).thenAnswer(invocation -> {
                    // 모의 동작: KeyMapping 업데이트
                    String action = invocation.getArgument(0);
                    int keyCode = invocation.getArgument(1);
                    System.out.println("Mocked updateKeyMapping called with: " + action + ", " + keyCode);
                    mockKeyMappings.put(action, keyCode); // 업데이트된 매핑
                    return null;
                });

                // Core.getKeyMappings 모의
                mockedCore.when(Core::getKeyMappings).thenReturn(mockKeyMappings);

                // index를 0부터 6까지 순회
                for (int index = 0; index <= 6; index++) {
                    // Key 설정
                    int mockKeyCode = mockKeyMappings.get(mapIndex.get((index+1)%mockKeyMappings.size()));
                    String selectedAction = mapIndex.get(index); // 각 index에 해당하는 액션
                    keyMappingOption.waitingForKeyInput = true;
                    keyMappingOption.selectedIndex = index;

                    int prevKeyCode = mockKeyMappings.get(selectedAction);

                    // 조건 설정: 키코드 입력 및 쿨다운 완료
                    when(mockInputManager.isKeyDown(mockKeyCode)).thenReturn(true);
                    when(mockSelectionCooldown.checkFinished()).thenReturn(true);

                    // 메서드 실행
                    keyMappingOption.handleKeyInput();

                    // 검증: 매핑이 업데이트되었는지 확인
                    assertEquals(prevKeyCode, mockKeyMappings.get(selectedAction)); // 매핑 업데이트 확인
                    assertFalse(keyMappingOption.waitingForKeyInput); // 입력 대기 상태 해제 확인
                    verify(mockSelectionCooldown).reset(); // 쿨다운 리셋 호출 확인

                    // Core.updateKeyMapping 호출 여부 확인
                    mockedCore.verify(() -> Core.updateKeyMapping(selectedAction, mockKeyCode), times(0));

                    // 상태 초기화
                    reset(mockInputManager);
                    reset(mockSelectionCooldown);
                }
            }
        }
    }

}
