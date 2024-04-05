package com.demo.controller;

import com.demo.exception.UserAlreadyExistsException;
import com.demo.model.User;
import com.demo.payloads.UserDto;
import com.demo.request.LoginRequest;
import com.demo.response.JwtResponse;
import com.demo.security.jwt.JwtUtils;
import com.demo.security.user.HotelUserDetails;
import com.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000/")
public class AuthController {

    public final UserDetailsService userDetailsService;
    public final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    public final UserService userService;

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        try{
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successfull !!");
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @RequestBody LoginRequest request
            ) {
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token=this.jwtUtils.generateJwtTokenForUser(authentication);

        System.out.println("token controller: "+token);
        HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        JwtResponse response=new JwtResponse(userDetails.getId(), userDetails.getEmail(), token, roles);
        System.out.println(response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
