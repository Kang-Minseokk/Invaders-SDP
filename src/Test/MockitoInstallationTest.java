import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class MockitoInstallationTest {

    public interface Calculator {
        int add(int a, int b);
    }

    @Test
    public void testMockitoInstallation() {
        // Mockito로 Mock 객체 생성
        Calculator calculator = Mockito.mock(Calculator.class);

        // Mock 객체의 동작 설정
        when(calculator.add(2, 3)).thenReturn(5);

        // Mock 객체 메서드 호출
        int result = calculator.add(2, 3);

        // 결과 검증
        assert result == 5 : "Mockito is not working correctly";

        // 호출 여부 검증
        verify(calculator).add(2, 3);
    }
}
