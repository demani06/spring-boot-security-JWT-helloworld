package com.deepak.springbootsecurityJWThelloworld.filter;

import com.deepak.springbootsecurityJWThelloworld.security.JwtUtil;
import com.deepak.springbootsecurityJWThelloworld.service.MyUserDetailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter{

    @Autowired
    private MyUserDetailServiceImpl userDetailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = httpServletRequest.getHeader("Authorization");

        String userName = null;
        String jwt = null;

        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer")){
            jwt =  authorizationHeader.substring(7);
            userName = jwtUtil.extractUserName(jwt);
        }

        if(userName!=null && SecurityContextHolder.getContext().getAuthentication() == null){

            UserDetails userDetails = this.userDetailService.loadUserByUsername(userName);

            if(jwtUtil.validateToken(jwt, userDetails)){

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }

        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
