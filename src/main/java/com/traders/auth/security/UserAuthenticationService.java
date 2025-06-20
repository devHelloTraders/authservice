package com.traders.auth.security;

import com.traders.auth.domain.Authority;
import com.traders.auth.domain.User;
import com.traders.auth.exception.UserNotActivatedException;
import com.traders.auth.repository.UserRepository;
import com.traders.common.utils.EncryptionUtil;
import lombok.SneakyThrows;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserAuthenticationService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationService.class);

    private final UserRepository userRepository;

    public UserAuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String login) {
        LOG.debug("Authenticating {}", login);

        if (new EmailValidator().isValid(login, null)) {
            return userRepository
                .findOneWithAuthoritiesByEmailIgnoreCase(login)
                .map(user -> createSpringSecurityUser(login, user))
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + login + " not found in the database"));
        }

        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        return userRepository
            .findOneWithAuthoritiesByLogin(lowercaseLogin)
            .map(user -> createSpringSecurityUser(lowercaseLogin, user))
            .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " not found in the database"));
    }
    @SneakyThrows
    private CustomUserDetails createSpringSecurityUser(String lowercaseLogin, User user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        if(user.getDeleteFlag() == 1){
            throw new UserNotActivatedException("User "+ lowercaseLogin +" not found in the database");
        }

        List<SimpleGrantedAuthority> grantedAuthorities = user
            .getAuthorities()
            .stream()
            .map(Authority::getName)
            .map(SimpleGrantedAuthority::new)
            .toList();
        var userDetails = new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), grantedAuthorities);
        return new CustomUserDetails(user.getId(),EncryptionUtil.encrypt(user.getId().toString()),userDetails);
    }
}
