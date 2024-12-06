package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileManagerTest {

    private FileManager fileManager;

    @BeforeEach
    void setUp() {
        // FileManager의 인스턴스를 초기화
        fileManager = FileManager.getInstance();
    }

    @Nested
    class test_KAN_72_KSM {
        @Test
        void testLoadSprite() {
            // Arrange: spriteMap 초기화
            Map<DrawManager.SpriteType, Color[][]> spriteMap = new LinkedHashMap<>();

            // SpriteType 순서대로 정의
            spriteMap.put(DrawManager.SpriteType.Obstacle, new Color[24][24]); // Obstacle
            spriteMap.put(DrawManager.SpriteType.Ship, new Color[16][26]);     // Ship

            try {
                // Act: loadSprite 호출
                fileManager.loadSprite(spriteMap);

                // Assert: Obstacle 및 Ship 데이터 확인
                Color[][] obstacleSprite = spriteMap.get(DrawManager.SpriteType.Obstacle);
                Color[][] shipSprite = spriteMap.get(DrawManager.SpriteType.Ship);

                assertNotNull(obstacleSprite, "Obstacle sprite should not be null.");
                assertNotNull(shipSprite, "Ship sprite should not be null.");

                // Obstacle 및 Ship 픽셀 데이터 검증
                assertEquals(Color.decode("0x080808"), obstacleSprite[0][7], "Pixel [0][7] of Obstacle sprite should be 0x080808.");
                assertEquals(Color.decode("0x50321e"), shipSprite[0][10], "Pixel [0][10] of Ship sprite should be 0x50321e.");
            } catch (Exception e) {
                fail("Exception 발생: " + e.getMessage());
            }
        }
    }
}
