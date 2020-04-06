package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import util.SplitUtil;
import util.TypeUtil;

public class RequestHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String read = bufferedReader.readLine();
            String[] url = SplitUtil.urlSplit(read);
            log.debug("url : {}", url[1]);

            String dataLength = null;
            String cookie = null;

            while (!read.equals("") && read != null) {
                log.debug("read : {}", read);

                if(read.contains("Content-Length")) {
                    dataLength = read;
                    log.debug(dataLength);
                }

                if(read.contains("logined")) {
                    cookie = read;
                    log.debug(cookie);
                }
                read = bufferedReader.readLine();
            }

            String data = null;
            DataOutputStream dos = new DataOutputStream(out);

            if(url[0].equals("POST")) {
                if(dataLength != null && url[1].equals("/user/create") ) {
                    data = IOUtils.readData(bufferedReader, Integer.parseInt(SplitUtil.bodySplit(dataLength)));
                    User user = createObject(data);
                    DataBase.addUser(user);

                    response302Header(dos);
                }

                if(dataLength != null && url[1].equals("/user/login")) {
                    data = IOUtils.readData(bufferedReader, Integer.parseInt(SplitUtil.bodySplit(dataLength)));
                    login302Header(dos, data, cookie);
                }
            } else {
                //get 일 때 진행
                if(url[1].equals("/user/list")) {
                    list302Header(dos, cookie);
                }
            }

            byte[] body = Files.readAllBytes(new File("./webapp" + url[1]).toPath());
            response200Header(dos, body.length, SplitUtil.stylesheetSplit(url[1]));
            responseBody(dos, body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private User createObject(String data) {
        Map<String, String> parse = util.HttpRequestUtils.parseQueryString(data);
         return new User(
                parse.get("userId"),
                parse.get("password"),
                parse.get("name"),
                parse.get("email"));
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String stylesheet) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+ TypeUtil.stylesheet(stylesheet) + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        String location = "/index.html";
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void login302Header(DataOutputStream dos, String query, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes(location(query));
            dos.writeBytes(setCookie(query, cookie));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void list302Header (DataOutputStream dos, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes(cookieLocation(cookie));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String cookieLocation(String cookie) {
        return isCookieLogined(cookie) ? "Location: /user/list.html\r\n" : "Location: /user/login.html\r\n";
    }

    private boolean isCookieLogined(String cookie) {
        return cookie != null && cookie.contains("true");
    }

    private String setCookie(String query, String cookie) {
        Map<String, String> parse = HttpRequestUtils.parseQueryString(query);

        return cookie != null &&
                isLogin(parse.get("userId"), parse.get("Password")) ?
                "Set-Cookie: logined=true; Path=/" : "Set-Cookie: logined=false; Path=/";
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String location(String query) {
        Map<String, String> parse = HttpRequestUtils.parseQueryString(query);
        return isLogin(parse.get("userId"), parse.get("password")) ?
                "Location: /user/list.html\r\n" : "Location: /user/login_failed.html\r\n";
    }

    private boolean isLogin(String userId, String password) {
        return DataBase.findUserById(userId) != null && DataBase.findUserById(userId).getPassword().equals(password);
    }
}
