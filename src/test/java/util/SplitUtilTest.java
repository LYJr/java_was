package util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SplitUtilTest {

    @Test
    public void urlSplit () {
        String url = "GET /index.html /뭐 하나 더 있음";
        String html = SplitUtil.urlSplit(url)[1];

        assertThat(html).isEqualTo("/index.html");
    }

    @Test
    public void dataSplit () {
        String data = "Content-Length: 47";
        assertThat("47").isEqualTo(SplitUtil.bodySplit(data));
    }

    @Test
    public void stylesheet() {
        String data = "/index.html";
        assertThat(SplitUtil.stylesheetSplit(data)).isEqualTo("html");
    }
}
