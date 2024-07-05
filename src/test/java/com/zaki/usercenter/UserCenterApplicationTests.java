package com.zaki.usercenter;

import org.apache.logging.log4j.message.Message;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
class UserCenterApplicationTests {

    @Test
    void testDigest() throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String newPassword = DigestUtils.md5DigestAsHex(("abcd" + "mypassword").getBytes(StandardCharsets.UTF_8));
        System.out.println(newPassword);

    }
    @Test
    void contextLoads() {

    }

}
