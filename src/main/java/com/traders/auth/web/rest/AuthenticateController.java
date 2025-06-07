package com.traders.auth.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.nimbusds.jose.util.Pair;
import com.traders.auth.domain.User;
import com.traders.auth.repository.UserRepository;
import com.traders.auth.security.CustomUserDetails;
import com.traders.auth.service.RedisService;
import com.traders.auth.web.rest.model.LoginVM;
import com.traders.auth.web.rest.model.PasswordRecord;
import com.traders.common.security.SecurityUtils;
import com.traders.common.utils.UserIdSupplier;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class AuthenticateController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticateController.class);

    private final JwtEncoder jwtEncoder;

    @Value("${config.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${config.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;
    private final RedisService redisService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;

    public AuthenticateController(JwtEncoder jwtEncoder, RedisService redisService, AuthenticationManagerBuilder authenticationManagerBuilder, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.redisService = redisService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userRepository = userRepository;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(!Strings.isNullOrEmpty(loginVM.getFcmToken())) {
            CustomUserDetails userDetails= (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId();
            userRepository.updateFcmToken(userId, loginVM.getFcmToken());
        }
        String jwt = this.createToken(authentication, loginVM.isRememberMe());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/client/authenticate")
    public ResponseEntity<Boolean> authorize(@RequestBody PasswordRecord passwordRecord) {
        Long userId = UserIdSupplier.getUserId();

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getLogin(),
                passwordRecord.password()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails= (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(Objects.equals(userDetails.getId(), userId));
    }


    /**
     * {@code GET /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param principal the authentication principal.
     * @return the login if the user is authenticated.
     */
    @GetMapping(value = "/authenticate", produces = MediaType.TEXT_PLAIN_VALUE)
    public String isAuthenticated(Principal principal) {
        LOG.debug("REST request to check if the current user is authenticated");
        return principal == null ? null : principal.getName();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        CustomUserDetails userDetails= (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getUserId();
        Instant now = Instant.now();
        Instant validity;
        if (rememberMe) {
            validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        }
        long instant = LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(SecurityUtils.AUTHORITIES_KEY, authorities)
            .claim("userId", userId)
            .claim("creationTimeStamp", instant)
            .build();
        redisService.saveToCustomCacheWithTTL("tokenManager",userId,instant,24,TimeUnit.HOURS);
        JwsHeader jwsHeader = JwsHeader.with(SecurityUtils.JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

    @GetMapping("/greet")
    public String authorize() {
        return "Greet";
    }
}
