package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import util.HttpRequestUtils;
import util.IOUtils;
import util.SplitUtil;

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

            if(read == null) {
                return;
            }

            String[] url = SplitUtil.urlSplit(read);
            log.debug("url : {}", url[1]);

            String dataLength = null;

            while (!read.equals("")) {
                log.debug("read : {}", read);

                if(read.contains("Content-Length")) {
                    dataLength = read;
                    log.debug(dataLength);
                }
                read = bufferedReader.readLine();
            }

            DataOutputStream dos = new DataOutputStream(out);

            String data = null;
            if(url[0].equals("POST")) {
                if(dataLength != null && url[1].equals("/user/create.html")) {
                    data = IOUtils.readData(bufferedReader, Integer.parseInt(SplitUtil.bodySplit(dataLength)));
                    log.debug("data create : {}", data);
                    User user = createObject(data);
                    DataBase.addUser(user);
                    response302Header(dos);
                }

                //login
                if(dataLength != null && url[1].equals("/user/login")) {
                    data = IOUtils.readData(bufferedReader, Integer.parseInt(SplitUtil.bodySplit(dataLength)));
                    log.debug("data login : {}", data);
                    login302Header(dos, data);
                }
            } else {
                byte[] body = Files.readAllBytes(new File("./webapp" + url[1]).toPath());

                response200Header(dos, body.length);
                responseBody(dos, body);
            }


        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        String location = "Location: /index.html";
        try{
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes(location);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void login302Header(DataOutputStream dos, String data) {
        try{
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes(UserService.location(data));
            UserService.setCookie(dos, data);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private User createObject(String body) {
        Map<String, String> user = HttpRequestUtils.parseQueryString(body);

        return new User(user.get("userId"),
                user.get("password"),
                user.get("name"),
                user.get("email"));
    }
}
