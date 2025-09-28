package com.canbe.SqlInjection.service.impl;

import com.canbe.SqlInjection.dto.LoginRequest;
import com.canbe.SqlInjection.dto.RegistrationRequest;
import com.canbe.SqlInjection.dto.Response;
import com.canbe.SqlInjection.dto.UserDto;
import com.canbe.SqlInjection.enums.UserRole;
import com.canbe.SqlInjection.exception.InvalidCredentialException;
import com.canbe.SqlInjection.exception.NotFoundException;
import com.canbe.SqlInjection.model.User;
import com.canbe.SqlInjection.repository.UserRepository;
import com.canbe.SqlInjection.security.JwtUtils;
import com.canbe.SqlInjection.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ModelMapper modelMapper;
    private final InputSanitizer inputSanitizer;

    @Override
    public Response registerUser(RegistrationRequest registrationRequest) {

        inputSanitizer.validateAndSanitizeRegistrationRequest(registrationRequest);

        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserRole role = UserRole.USER;
        if (registrationRequest.getRole() != null) {
            role = registrationRequest.getRole();
        }

        User user  = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .userRole(role)
                .isActive(Boolean.TRUE)
                .createdAt(LocalDate.now())
                .build();

        userRepository.save(user);

        return Response.builder()
                .status(200)
                .message("User registered successfully")
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {

        inputSanitizer.validateAndSanitizeLoginRequest(loginRequest);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new NotFoundException("Password or username incorrect"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialException("Password or username incorrect");
        }

        String token = jwtUtils.generateToken(user.getEmail());

        return Response.builder()
                .status(200)
                .message("User logged in successfully")
                .role(user.getUserRole())
                .token(token)
                .isActive(user.isActive())
                .expirationTime("1 day")
                .build();
    }

    @Override
    public Response getAllUsers() {

        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = modelMapper.map(users, new TypeToken<List<UserDto>>(){}.getType());


        return Response.builder()
                .status(200)
                .message("success")
                .users(userDtos)
                .build();
    }

    @Override
    public Response getOwnAccountDetails() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("User not found"));

        UserDto userDto = modelMapper.map(user, UserDto.class);

        return Response.builder()
                .status(200)
                .message("success")
                .user(userDto)
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("User Not Found"));
    }

    @Override
    public Response updateOwnAccount(UserDto userDto) {

        User existingUser = getCurrentLoggedInUser();

        if (userDto.getEmail() != null) existingUser.setEmail(userDto.getEmail());

        if (userDto.getFirstName() != null) existingUser.setFirstName(userDto.getFirstName());

        if (userDto.getLastName() != null) existingUser.setLastName(userDto.getLastName());

        if (userDto.getPhoneNumber() != null) existingUser.setPhoneNumber(userDto.getPhoneNumber());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        userRepository.save(existingUser);

        return Response.builder()
                .status(200)
                .message("user updated successfully")
                .build();
    }

    @Override
    public Response deleteOwnAccount() {

        User user = getCurrentLoggedInUser();
        userRepository.delete(user);

        return Response.builder()
                .status(200)
                .message("user deleted successfully")
                .build();
    }

}
