package screen;

import engine.Core;
import engine.Cooldown;
import engine.InputManager;
import engine.SoundManager;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.awt.event.KeyEvent;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OptionScreenTest {

    private static MockedStatic<Core> mockedCore;
    private OptionScreen optionScreen;
    private InputManager mockInputManager;
    private Cooldown mockSelectionCooldown;
    private SoundManager mockSoundManager;

    @BeforeAll
    static void setUpGlobalMocks() {
        mockedCore = mockStatic(Core.class);
        Logger mockLogger = mock(Logger.class);
        mockedCore.when(Core::getLogger).thenReturn(mockLogger);
        doNothing().when(mockLogger).info(anyString());
        Core.setTestEnv();
    }

    @AfterAll
    static void tearDownGlobalMocks() {
        mockedCore.close(); // MockedStatic 리소스 해제
    }

    @BeforeEach
    void setUp() {
        // Mock Core
        mockedCore.when(() -> Core.getCooldown(anyInt())).thenAnswer(invocation -> mock(Cooldown.class));
        mockedCore.when(() -> Core.getKeyCode("MOVE_UP")).thenReturn(KeyEvent.VK_W);
        mockedCore.when(() -> Core.getKeyCode("MOVE_DOWN")).thenReturn(KeyEvent.VK_S);
        mockedCore.when(() -> Core.getKeyCode("GO_BACK")).thenReturn(KeyEvent.VK_ESCAPE);

        // Mock InputManager
        mockInputManager = mock(InputManager.class);
        mockSelectionCooldown = mock(Cooldown.class);

        // Mock SoundManager 정적 메서드 설정
        try (MockedStatic<SoundManager> mockedSoundManagerStatic = mockStatic(SoundManager.class)) {
            mockSoundManager = mock(SoundManager.class);
            mockedSoundManagerStatic.when(SoundManager::getInstance).thenReturn(mockSoundManager);

            // OptionScreen 생성
            optionScreen = new OptionScreen(800, 600, 60, null);
            optionScreen.setTestEnv();
        }

        optionScreen.inputManager = mockInputManager;
        optionScreen.selectionCooldown = mockSelectionCooldown;
    }

    @Test
    void testHandleVerticalMenuNavigation_MoveUp() {
        when(mockInputManager.isKeyDown(KeyEvent.VK_W)).thenReturn(true);

        int initialReturnCode = optionScreen.returnCode;
        optionScreen.handleVerticalMenuNavigation();

        int expectedReturnCode = (initialReturnCode - 1 < 1) ? 3 : initialReturnCode - 1;
        assertEquals(expectedReturnCode, optionScreen.returnCode);
        verify(mockSelectionCooldown).reset();
    }

    @Test
    void testHandleVerticalMenuNavigation_MoveDown() {
        when(mockInputManager.isKeyDown(KeyEvent.VK_S)).thenReturn(true);

        int initialReturnCode = optionScreen.returnCode;
        optionScreen.handleVerticalMenuNavigation();

        int expectedReturnCode = (initialReturnCode + 1 > 3) ? 1 : initialReturnCode + 1;
        assertEquals(expectedReturnCode, optionScreen.returnCode);
        verify(mockSelectionCooldown).reset();
    }
    @Test
    void testNextBGMState() {
        optionScreen.bgmState = 0;
        optionScreen.nextBGMState();
        assertEquals(1, optionScreen.bgmState, "bgmState should update to 1");

        optionScreen.bgmState = 2;
        optionScreen.nextBGMState();
        assertEquals(0, optionScreen.bgmState, "bgmState should wrap around to 0");
    }

    @Test
    void testPreviousBGMState() {
        optionScreen.bgmState = 0;
        optionScreen.previousBGMState();
        assertEquals(2, optionScreen.bgmState, "bgmState should wrap around to 2");

        optionScreen.bgmState = 1;
        optionScreen.previousBGMState();
        assertEquals(0, optionScreen.bgmState, "bgmState should update to 0");
    }



    @Test
    void testHandleVolumeCode_DecreaseVolume() {
        // Mock InputManager
        when(mockInputManager.isKeyDown(KeyEvent.VK_LEFT)).thenReturn(true);

        optionScreen.returnCode = 2; // Volume 조정 상태
        optionScreen.volumeSelectionCode = 3; // 중간 볼륨 상태

        optionScreen.handleVolumeCode();

        // Assert and Verify
        assertEquals(2, optionScreen.volumeSelectionCode); // 볼륨 감소 확인
    }

    @Test
    void testHandleVolumeCode_IncreaseVolume() {
        // Mock InputManager
        when(mockInputManager.isKeyDown(KeyEvent.VK_RIGHT)).thenReturn(true);

        // Mock SoundManager 주입
        optionScreen.returnCode = 2; // Volume 조정 상태
        optionScreen.volumeSelectionCode = 4; // 중간 볼륨 상태

        optionScreen.handleVolumeCode();

        assertEquals(5, optionScreen.volumeSelectionCode); // 볼륨 증가 확인
    }
}
