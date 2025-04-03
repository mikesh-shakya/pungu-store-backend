package com.pungu.store.auth_service.security;

import com.pungu.store.auth_service.service.CustomUserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTokenHelper jwtTokenHelper;
    @Autowired
    ApplicationContext context;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization"); // requestHeader will be in the form "Bearer hdvsksjvfjsvfksvfusdsdchs..."
        String username = null;
        String token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7); // gets everything after Bearer...
            try {
                username = this.jwtTokenHelper.getUserNameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.info("Illegal argument while fetching the username!!!");
            } catch (ExpiredJwtException e) {
                logger.info("Token is already expired...");
            } catch (MalformedJwtException e) {
                logger.info("Some changes have been made... Invalid token!!!");
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        } else {
            logger.info("Invalid Header Value");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            UserDetails userDetails = context.getBean(CustomUserDetailsServiceImpl.class).loadUserByUsername(username);
            boolean validateToken = this.jwtTokenHelper.validateToken(token, userDetails);


            if (validateToken) {
                // setting authentication
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                logger.info("Validation fails !!!");
            }
        }
        filterChain.doFilter(request, response);
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        // 1. Get token
//        String requestToken = request.getHeader("Authorization"); //Bearer 243535csf
//        String username = null;
//        String token = null;
//
//        if (requestToken  != null && requestToken.startsWith("Bearer"))
//        {
//            token = requestToken.substring(7);
//            try {
//                username = this.jwtTokenHelper.getUserNameFromToken(token);
//            } catch(IllegalArgumentException e){
//                System.out.println("Unable to get JWT token...");
//            } catch(ExpiredJwtException e){
//                System.out.println("Token is already expired...");
//            } catch(MalformedJwtException e){
//                System.out.println("Invalid JWT token...");
//            }
//
//        }else{
//            System.out.println("JWT Token doesn't begin with Bearer..");
//        }
//
//
//        // Now we will validate token
//        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//            if (this.jwtTokenHelper.validateToken(token, userDetails)){
//                //Authenticating...
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//            } else{
//                System.out.println("Invalid JWT token...");
//            }
//        }else{
//            System.out.println("Username is null or context is not null...");
//        }
//        filterChain.doFilter(request, response) ;
//    }
}
