package engine;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import screen.Background;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DrawManagerTest {

    @Nested
    class test_KAN_74_HSW{
        @Test
        void testLoadTitleBackgroundWithRealPath() {
            // Act: Background에서 실제 리소스 파일 경로를 통해 InputStream 가져오기
            InputStream imageStream = Background.getTitleBackgroundStream();

            // Assert: InputStream이 null이 아니어야 함
            assertNotNull(imageStream, "유효한 경로에서는 InputStream이 null이 아니어야 합니다.");

            // 이미지 로드 테스트
            try {
                BufferedImage backgroundImage = ImageIO.read(imageStream);
                assertNotNull(backgroundImage, "이미지가 올바르게 로드되어야 합니다.");
            } catch (Exception e) {
                fail("이미지를 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            }
        }

        @Test
        void testDrawTitleBackground() {
            // Mock Graphics 객체 생성
            DrawManager drawManager = new DrawManager();
            BufferedImage mockImage = mock(BufferedImage.class);
            DrawManager.backBufferGraphics = mock(java.awt.Graphics.class);

            try {
                // Act: backgroundImage를 설정
                Field backgroundImageField = DrawManager.class.getDeclaredField("backgroundImage");
                backgroundImageField.setAccessible(true); // private 필드 접근 허용
                backgroundImageField.set(drawManager, mockImage);

                // drawTitleBackground 호출
                drawManager.drawTitleBackground();

                // Assert: drawImage 호출 검증
                verify(DrawManager.backBufferGraphics, times(1)).drawImage(mockImage, 0, 0, null);
            } catch (Exception e) {
                fail("테스트 중 예외가 발생했습니다: " + e.getMessage());
            }
        }
    }
}
