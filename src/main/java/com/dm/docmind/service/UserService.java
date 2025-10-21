package com.dm.docmind.service;

import com.dm.docmind.commonResponse.CommonResponse;
import com.dm.docmind.entity.User;

import java.security.NoSuchAlgorithmException;

/**
 * @author Gather
 */
public interface UserService {

    CommonResponse<Object> registerUser(User user) throws NoSuchAlgorithmException;

    CommonResponse<Object> loginUser(User user) throws NoSuchAlgorithmException;

}
