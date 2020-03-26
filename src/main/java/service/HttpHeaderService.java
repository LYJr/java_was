package service;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaderService {

    private static final Logger log =  LoggerFactory.getLogger(HttpHeaderService.class);

    public static String contentType(String url) {
        String stylesheet = url.split("/")[1];
        log.debug("stylesheet : {}", stylesheet);

        return insertStylesheet().get(stylesheet);
    }

    private static Map<String, String> insertStylesheet() {
        Map<String, String> stylesheetList = new HashMap<>();
        stylesheetList.put("doc", "/application/msword;");
        stylesheetList.put("pdf", "/application/pdf;");
        stylesheetList.put("xls", "/application/vnd.ms-excel;");
        stylesheetList.put("js", "/application/x-javascript;");
        stylesheetList.put("zip", "/application/zip;");
        stylesheetList.put("jpeg", "/image/jpeg;"); //jpg, jpe 키값 추가할 것
        stylesheetList.put("css", "/css;");
        stylesheetList.put("html", "/html;"); //htm 추가
        stylesheetList.put("txt", "/plain;");
        stylesheetList.put("xml", "/xml;");
        stylesheetList.put("xsl", "/xsl;");
        return stylesheetList;
    }

}
