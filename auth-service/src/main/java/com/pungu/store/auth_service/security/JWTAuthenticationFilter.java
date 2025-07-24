package com.pungu.store.auth_service.security;

import com.pungu.store.auth_service.service.CustomUserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that validates JWT tokens in incoming HTTP requests.
 * Extends OncePerRequestFilter to ensure it's executed once per request.
 */
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenHelper jwtTokenHelper;
    private final ApplicationContext context;

    /**
     * Validates the JWT from the request header and sets the authentication
     * in the security context if the token is valid.
     *
     * @param request     HTTP request object
     * @param response    HTTP response object
     * @param filterChain Filter chain for forwarding the request
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        // Extract token from Authorization header
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7);
            try {
                username = jwtTokenHelper.getUserNameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.info("Illegal argument while fetching the username from token.");
            } catch (ExpiredJwtException e) {
                logger.info("JWT token is expired.");
            } catch (MalformedJwtException e) {
                logger.info("JWT token is malformed.");
            } catch (Exception e) {
                logger.info("Unexpected error while parsing token: " + e.getMessage());
            }
        } else {
            logger.info("Authorization header missing or does not begin with Bearer.");
        }

        // Validate token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(CustomUserDetailsServiceImpl.class)
                    .loadUserByUsername(username);

            boolean isTokenValid = jwtTokenHelper.validateToken(token, userDetails);

            if (isTokenValid) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                logger.info("JWT token validation failed.");
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}