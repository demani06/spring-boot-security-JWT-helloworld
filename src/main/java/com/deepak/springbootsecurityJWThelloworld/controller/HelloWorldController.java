package com.deepak.springbootsecurityJWThelloworld.controller;

import com.deepak.springbootsecurityJWThelloworld.model.AuthenticationRequest;
import com.deepak.springbootsecurityJWThelloworld.model.AuthenticationResponse;
import com.deepak.springbootsecurityJWThelloworld.security.JwtUtil;
import com.deepak.springbootsecurityJWThelloworld.service.MyUserDetailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class HelloWorldController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailServiceImpl userDetailService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/hello")
    public String helloWorld() {
        log.info("Hello World");
        return "Hello World!";
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{
        System.out.println("createAuthToken++++++++++++++++++++++");
       log.info("createAuthToken called and authenticationRequest = {}", authenticationRequest);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(), authenticationRequest.getPassword())
            );
        }
        catch (BadCredentialsException b){
            throw new Exception("Incorrect user or password", b);
        }

        final UserDetails userDetails = userDetailService.loadUserByUsername(authenticationRequest.getUserName());

        final String jwt = jwtUtil.generateToken(userDetails);

        log.info("token = {}", jwt);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }
}
