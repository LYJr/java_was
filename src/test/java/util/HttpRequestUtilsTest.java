package util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import util.HttpRequestUtils.Pair;

public class HttpRequestUtilsTest {


    @Test
    public void parseQueryString() {
        String queryString = "userId=javajigi";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));

        queryString = "userId=javajigi&password=password2";
        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is("password2"));
    }

    @Test
    public void parseQueryString_null() {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString("");
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString(" ");
        assertThat(parameters.isEmpty(), is(true));
    }

    @Test
    public void parseQueryString_invalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));
    }

    @Test
    public void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";
        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
        assertThat(parameters.get("logined"), is("true"));
        assertThat(parameters.get("JSessionId"), is("1234"));
        assertThat(parameters.get("session"), is(nullValue()));
    }

    @Test
    public void getKeyValue() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertThat(pair, is(new Pair("userId", "javajigi")));
    }

    @Test
    public void getKeyValue_invalid() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertThat(pair, is(nullValue()));
    }

    @Test
    public void parseHeader() throws Exception {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertThat(pair, is(new Pair("Content-Length", "59")));
    }

    @Test
    public void getPackageNames() {
        Reflections reflections = new Reflections("model", new SubTypesScanner(false));
        Set<? extends Object> objects = reflections.getSubTypesOf(Object.class);

        assertThat(objects.size(), is(1));
    }

    @Test
    public void createGetNameClass () throws ClassNotFoundException {
        String path = "Class model.User";

        String[] split = path.split(" ");
        String name = split[1];

        Class<?> clazz = Class.forName(name);

        //todo 아래 sout 메서드 차이 비교해두기
//        System.out.println(clazz.getName());
//        System.out.println(clazz.getClass());
//        System.out.println(clazz.getCanonicalName());
//        System.out.println(clazz.getTypeName());
//        System.out.println(clazz.getSimpleName());

        assertThat(clazz.getSimpleName(), is("User"));
    }

    @Test
    public void getFiled() throws ClassNotFoundException {
        String name = "model.User";
        Class<?> clazz = Class.forName(name);

        Field[] fields = clazz.getDeclaredFields();

        assertThat(fields.length, is(4));
    }

    @Test
    public void isFiledSize() throws ClassNotFoundException {
        String data = "userId=adsfa&password=1234131&name=aaaa&email=b%40b.b";
        Map<String, String> parse = HttpRequestUtils.parseQueryString(data);

        String name = "model.User";
        Class<?> clazz = Class.forName(name);

        Field[] fields = clazz.getDeclaredFields();

        /**
         * 1. map 사용
         * 2. 필드를 지우기?
         * 3. for문 비교
         * 4. 그 외 다른 방법 추천 좀 ㅇㅇㅇ............
         */

    }


}
