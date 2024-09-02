//package com.example.demo.config;
//
//import com.example.demo.domain.repository.UserRepository;
//import com.example.demo.exception.NoSuchUserException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.UUID;
//
//public class UserFilter extends BasicAuthenticationFilter {
//
//    private final UserRepository userRepository;
//
//    public UserFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
//        super(authenticationManager);
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String token = request.getHeader("Authorization");
//
//        if (token != null && !token.isEmpty()) {
//            try {
//                UUID tokenId = UUID.fromString(token);
//                var user = userRepository.findUserByToken(tokenId)
//                        .orElseThrow(() -> new NoSuchUserException("User with given token does not exist"));
//
//                if (SecurityContextHolder.getContext().getAuthentication() == null) {
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            } catch (NoSuchUserException e) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token format");
//                return;
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}