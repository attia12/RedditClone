package com.example.demo.service;

import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.model.NotificationEmail;
import com.example.demo.model.User;
import com.example.demo.model.VerificationToken;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import com.example.demo.security.JwtProvider;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional

public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private RefreshTokenService refreshTokenService;


    private final  AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;
    @Transactional
    public void signup(RegisterRequest registerRequest) throws SpringRedditException {
        User user=new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);
        String token =generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account", user.getEmail(), "Thank you for signing up to Spring Reddit, \" +\n" +
                "                \"please click on the below url to activate your account : \" +\n" +
                "                \"http://localhost:8080/api/auth/accountVerification/"+token));


    }
    private String generateVerificationToken(User user)
    {
        String token=UUID.randomUUID().toString();
        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;

    }

    public void verifyAccount(String token) throws SpringRedditException {
        Optional<VerificationToken>verificationToken=verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(()->new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }
    @Transactional
    void fetchUserAndEnable(VerificationToken verificationToken) throws SpringRedditException {
        String username=verificationToken.getUser().getUsername();
       User user= userRepository.findByUsername(username).orElseThrow(()->new SpringRedditException("User not found with name "+username));
       user.setEnabled(true);
       userRepository.save(user);

    }

    public AuthenticationResponse login(LoginRequest loginRequest) throws SpringRedditException {
      Authentication authentication=  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token= jwtProvider.generateToken(authentication);
        return  AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();

    }
    @Transactional(readOnly = true)
//    public User getCurrentUser() {
//        Jwt principal = (Jwt) SecurityContextHolder.
//                getContext().getAuthentication().getPrincipal();
//        return userRepository.findByUsername(principal.getSubject())
//                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getSubject()));
//    }
//    public User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
//            Jwt jwt = (Jwt) authentication.getPrincipal();
//            String username = jwt.getClaim("username");
//
//            // Fetch the user details using the username from the JWT token
//            return userRepository.findByUsername(username)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found for username - " + username));
//        } else {
//            // Handle cases where user is not authenticated with a JWT token
//            return null; // or throw an exception, depending on your application's logic
//        }
//    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                String username = jwt.getSubject();

                return userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + username));
            } else if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                return userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + username));
            }
        }

        throw new UsernameNotFoundException("User not found");
    }


    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) throws SpringRedditException {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token=jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
}
