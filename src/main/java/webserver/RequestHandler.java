package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            String url = SplitUtil.urlSplit(read);
            log.debug("url : {}", url);

            String dataLength = null;

            while (!read.equals("") && read != null) {
                log.debug("read : {}", read);

                if(read.contains("Content-Length")) {
                    dataLength = read;
                    log.debug(dataLength);
                }
                read = bufferedReader.readLine();
            }

            String data = null;
            if(dataLength != null) {
                data = IOUtils.readData(bufferedReader, Integer.parseInt(SplitUtil.bodySplit(dataLength)));
                log.debug("data : {}", data);
            }

            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, body.length);
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

//    private Object createObject(String data) {
//        Map<String, String> bodys = HttpRequestUtils.parseQueryString(data);
//
//        for (String s : bodys.keySet()) {
//        }
//
//        return null;
//    }
}
