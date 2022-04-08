package com.art.artcommon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * description
 *
 * @author lou
 * @create 2022/4/7
 */
@Component
public class EmailConfig {

    private static String hostName;
    private static String charset;
    private static String email;
    private static String name;
    private static String userName;
    private static String password;
    private static String subject;

    @Value("${send-email.data.hostName}")
    private String v1;
    @Value("${send-email.data.charset}")
    private String v2;
    @Value("${send-email.data.email}")
    private String v3;
    @Value("${send-email.data.name}")
    private String v4;
    @Value("${send-email.data.userName}")
    private String v5;
    @Value("${send-email.data.password}")
    private String v6;
    @Value("${send-email.data.subject}")
    private String v7;

    @PostConstruct
    public void getValue(){
        hostName = this.v1;
        charset = this.v2;
        email = this.v3;
        name = this.v4;
        userName = this.v5;
        password = this.v6;
        subject = this.v7;
    }

    public static String getHostName() {
        return hostName;
    }

    public static String getCharset() {
        return charset;
    }

    public static String getEmail() {
        return email;
    }

    public static String getName() {
        return name;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getPassword() {
        return password;
    }

    public static String getSubject() {
        return subject;
    }
}
