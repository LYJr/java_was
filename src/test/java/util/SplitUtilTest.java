package util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SplitUtilTest {

    @Test
    public void split () {
        String url = "GET /index.html /뭐 하나 더 있음";
        String html = SplitUtil.split(url);

        assertThat(html).isEqualTo("/index.html");
    }
}
