package com.dm.docmind.controller;

import com.dm.docmind.commonResponse.CommonResponse;
//import com.dm.docmind.context.GlobalUserContext;
import com.dm.docmind.entity.User;
import com.dm.docmind.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@CrossOrigin(origins = "*")
@RequestMapping("/account")
@RestController
public class AccountController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public CommonResponse<Object> register(@RequestParam String userId, @RequestParam String password)
            throws NoSuchAlgorithmException {
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public CommonResponse<Object> login(@RequestParam String userId, @RequestParam String password, HttpSession session)
            throws NoSuchAlgorithmException {
        session.setAttribute("userId", userId);
        User user = new User();
        user.setPassword(password);
        user.setUserId(userId);
        return userService.loginUser(user);
    }
}
