package screen;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class BackgroundTest {
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
}