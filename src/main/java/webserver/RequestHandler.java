package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import util.SplitUtil;

import javax.xml.stream.Location;

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

            String url = SplitUtil.urlSplit(read);
            log.debug("url : {}", url);

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
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

            String data = null;
            if(dataLength != null && url.equals("/user/create.html")) {
                data = IOUtils.readData(bufferedReader, Integer.parseInt(SplitUtil.bodySplit(dataLength)));
                User user = createObject(data);
                log.debug("User : {}", user);
                response302Header(dos);
            } else {
                response200Header(dos, body.length);
            }

            responseBody(dos, body);
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
        try{
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: /index.html");
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
