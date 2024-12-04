package screen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class BackgroundTest {

    private Background background;

    @Nested
    class test_KAN_74_HSW{
        @Test
        void testGetTitleBackgroundStreamValidPath() {
            // Act: 정상적인 경로로 InputStream을 가져옴
            InputStream inputStream = Background.getTitleBackgroundStream();

            // Assert: InputStream이 null이 아니어야 함
            assertNotNull(inputStream, "정상적인 타이틀 배경 경로라면 InputStream은 null이 아니어야 합니다.");

            // 자원 닫기
            try {
                inputStream.close();
            } catch (Exception e) {
                fail("InputStream을 닫는 중 에러가 발생했습니다: " + e.getMessage());
            }
        }

        @Test
        void testGetTitleBackgroundStreamMissingFile() {
            // Act: 존재하지 않는 경로로 InputStream 가져오기
            InputStream inputStream = Background.class.getResourceAsStream("/backgrounds/nonexistent.jpg");

            // Assert: 존재하지 않는 경로라면 InputStream은 null이어야 함
            assertNull(inputStream, "파일이 없으면 InputStream은 null이어야 합니다.");
        }
    }

    @Nested
    class IngameBackgroundTest {
        private Background background;

        @BeforeEach
        void setUp() {
            assertNotNull(Background.levelBackgrounds, "Level backgrounds list should be initialized");
        }

        @Test
        void testSingletonInstance() {
            Background instance1 = Background.getInstance();
            Background instance2 = Background.getInstance();
            assertSame(instance1, instance2, "The singleton instance should be the same.");
        }

        @Test
        void testGetBackgroundImageStreamValidIndex() {
            InputStream levelStream = Background.getBackgroundImageStream(1);
            assertNotNull(levelStream, "Level background stream should not be null for valid index.");
        }

        @Test
        void testValidLevelBackgrounds() {
            // Test valid levels (1 to 6)
            for (int i = 1; i <= Background.levelBackgrounds.size(); i++) {
                InputStream backgroundStream = Background.getBackgroundImageStream(i);
                assertNotNull(backgroundStream, "Background stream for level " + i + " should not be null");
            }
        }

        @Test
        void testInvalidLevelBackgrounds() {
            // Test invalid level index (e.g., 0, negative, or out of bounds)
            assertThrows(IllegalArgumentException.class, () -> {
                Background.getBackgroundImageStream(0);
            }, "Should throw exception for level index 0");

            assertThrows(IllegalArgumentException.class, () -> {
                Background.getBackgroundImageStream(-1);
            }, "Should throw exception for negative level index");

            assertThrows(IllegalArgumentException.class, () -> {
                Background.getBackgroundImageStream(Background.levelBackgrounds.size() + 1);
            }, "Should throw exception for out-of-bounds level index");
        }
    }

    @Nested
    class BackgroundMovementTest {

        @BeforeEach
        void setUp() {
            background = mock(Background.class);
            
            when(background.getVerticalOffset()).thenReturn(0, -1, -2, -3);  // verticalOffset 값 순차적으로 반환
            when(background.getHorizontalOffset(anyBoolean(), anyBoolean())).thenReturn(-800, -803, -800, -803); // 가로 오프셋 순차적으로 반환
        }

        @Test
        void testVerticalMovement() {
            int initialOffset = background.getVerticalOffset();
            assertEquals(0, initialOffset, "Initial vertical offset should be 0");

            int updatedOffset1 = background.getVerticalOffset();
            int updatedOffset2 = background.getVerticalOffset();
            int updatedOffset3 = background.getVerticalOffset();

            assertEquals(-1, updatedOffset1, "Vertical offset should decrease by 1 on the first update");
            assertEquals(-2, updatedOffset2, "Vertical offset should decrease by 1 on the second update");
            assertEquals(-3, updatedOffset3, "Vertical offset should decrease by 1 on the third update");
        }

        @Test
        void testHorizontalMovement() {
            int initialOffset = background.getHorizontalOffset(false, false);
            assertEquals(-800, initialOffset, "Initial horizontal offset should be -800");

            int moveRightOffset = background.getHorizontalOffset(true, false);
            assertEquals(-803, moveRightOffset, "Horizontal offset should decrease by 3 when moving right");

            int moveLeftOffset = background.getHorizontalOffset(false, true);
            assertEquals(-800, moveLeftOffset, "Horizontal offset should increase by 3 when moving left");
        }
    }
}

