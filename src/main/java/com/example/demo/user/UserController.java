package com.example.demo.user;

import ch.qos.logback.core.net.SMTPAppenderBase;
import com.example.demo.core.JwtService;
import com.example.demo.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping(path = "api/v1/user")
public class UserController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/me")
    private UserProfileDTO getProfile() {
        JwtAuthenticationDetails details = (JwtAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Optional<User> use = userService.findById(details.getUserId());
        if (use.isPresent()) {
            User user = use.get();
            return convertToUserDTO(user);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
    }

    @PostMapping
    private User save(@RequestBody User user) {
        User userHash =  jwtService.hashPassword(user);
        return userService.saveOrUpdate(userHash);
    }

    @PatchMapping
    private UserProfileDTO patch(@RequestBody User user) {
        JwtAuthenticationDetails details = (JwtAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return convertToUserDTO(userService.update(details.getUserId(), user));
        } else {
            User userHash =  jwtService.hashPassword(user);;
            return convertToUserDTO(userService.update(details.getUserId(), userHash));
        }
    }

    @DeleteMapping("{userId}")
    private void delete(@PathVariable("userId") Long userId) {
        userService.deleteById(userId);
    }

    @PostMapping("/auth/login")
    private LoginResponse login(@RequestBody LoginUserDto userDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword())
            );
            User user = userService.findUserByEmail(userDto.getEmail());
            return jwtService.generateJwtToken(user);

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    public UserProfileDTO convertToUserDTO(User user) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(user, UserProfileDTO.class);
    }
}
