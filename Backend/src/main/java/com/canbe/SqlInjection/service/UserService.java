package com.canbe.SqlInjection.service;

import com.canbe.SqlInjection.dto.LoginRequest;
import com.canbe.SqlInjection.dto.RegistrationRequest;
import com.canbe.SqlInjection.dto.Response;
import com.canbe.SqlInjection.dto.UserDto;
import com.canbe.SqlInjection.model.User;

public interface UserService {

    Response loginUser(LoginRequest loginRequest);
    Response registerUser(RegistrationRequest registrationRequest);
    Response getAllUsers();
    Response getOwnAccountDetails();
    User getCurrentLoggedInUser();
    Response updateOwnAccount(UserDto userDto);
    Response deleteOwnAccount();
}