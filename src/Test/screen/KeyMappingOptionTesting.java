package screen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class KeyMappingOptionTesting {

    private KeyMappingOption keyMappingOption;

    @BeforeEach
    void setUp() {
        keyMappingOption = new KeyMappingOption(630, 720, 60);
        keyMappingOption.actions = Arrays.asList("MOVE_UP", "MOVE_DOWN", "SHOOT"); // 옵션 리스트 초기화
        keyMappingOption.selectedIndex = 0; // 초기 선택 인덱스
    }

    @Nested
    class nextOption{
        @Test
        public void testNextOption_CyclesToFirst() {
            // GIVEN: 선택된 인덱스가 마지막 항목
            keyMappingOption.selectedIndex = keyMappingOption.actions.size() - 1;

            // WHEN: nextOption 호출
            keyMappingOption.nextOption();

            // THEN: 인덱스가 첫 번째로 순환
            assertEquals(0, keyMappingOption.selectedIndex, "Index should cycle to the first option");
        }

        @Test
        public void testNextOption_IncrementIndex() {
            // GIVEN: 선택된 인덱스가 마지막이 아님
            keyMappingOption.selectedIndex = 1;

            // WHEN: nextOption 호출
            keyMappingOption.nextOption();

            // THEN: 인덱스가 1 증가
            assertEquals(2, keyMappingOption.selectedIndex, "Index should increment by 1");
        }
    }

    @Nested
    class previousOption {
        @Test
        public void testPreviousOption_CyclesToLast() {
            // GIVEN: 선택된 인덱스가 첫 번째 항목
            keyMappingOption.selectedIndex = 0;

            // WHEN: previousOption 호출
            keyMappingOption.previousOption();

            // THEN: 인덱스가 마지막 항목으로 순환
            assertEquals(keyMappingOption.actions.size() - 1, keyMappingOption.selectedIndex,
                    "Index should cycle to the last option");
        }

        @Test
        public void testPreviousOption_DecrementsIndex() {
            // GIVEN: 선택된 인덱스가 첫 번째가 아님
            keyMappingOption.selectedIndex = 2;

            // WHEN: previousOption 호출
            keyMappingOption.previousOption();

            // THEN: 인덱스가 1 감소
            assertEquals(1, keyMappingOption.selectedIndex, "Index should decrement by 1");
        }
    }
}