package com.dm.docmind.tool;

import org.springframework.stereotype.Repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Repository
public class HashPassword {

    private String password;

    public HashPassword() {
    }

    //加盐and密码哈希加密
    public String SHA256Gener(String number, String salt) throws NoSuchAlgorithmException {

        //暂定简单的加盐方式
        String saltNum = salt + number;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(saltNum.getBytes());
        byte[] digest = md.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    //盐
    public String saltGener() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(Integer.toHexString(random.nextInt(16)).toUpperCase());
        }
        while (sb.length() < 32) {
            sb.append('0');
        }

        return sb.toString();
    }

}
