package service;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import db.DataBase;
import util.HttpRequestUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class UserService {
    private static final Logger log =  LoggerFactory.getLogger(UserService.class);

    private static boolean isLogin(String data) {
        Map<String, String> parse = HttpRequestUtils.parseQueryString(data);
        if(DataBase.findUserById(parse.get("userId")) == null) {
            return false;
        }
        return DataBase.findUserById(parse.get("userId")).getPassword().equals(parse.get("password"));
    }

    public static String location(String data) {
        return isLogin(data) ? "Location: /index.html\r\n" : "Location: /user/login_failed.html\r\n";
    }

    public static void setCookie(DataOutputStream dos, String data) throws IOException {
        dos.writeBytes(isLogin(data) ? "Set-Cookie: logined=true; Path=/" : "Set-Cookie: logined=false; Path=/");
    }
}
