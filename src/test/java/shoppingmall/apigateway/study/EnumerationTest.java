package shoppingmall.apigateway.study;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.junit.jupiter.api.Test;

public class EnumerationTest {

    @Test
    void enumeration_test() {
        List<String> list = new ArrayList<>();
        Enumeration<String> enumeration = Collections.enumeration(list);

        assertThat(enumeration.hasMoreElements()).isFalse();
    }

}
