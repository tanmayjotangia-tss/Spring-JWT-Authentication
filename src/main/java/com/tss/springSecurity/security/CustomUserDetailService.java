package com.tss.springSecurity.security;

import com.tss.springSecurity.entity.Role;
import com.tss.springSecurity.entity.User;
import com.tss.springSecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role role = user.getRole();

        Set<GrantedAuthority> authorities = new HashSet<>();

//        For Multiple Roles of a single User
//        Set<GrantedAuthority> authorities = user.getRoles()
//                .stream()
//                .map((role) -> new SimpleGrantedAuthority(role.getRoleName()))
//                .collect(Collectors.toSet());

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+role.getRoleName());
        authorities.add(authority);

        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);
    }
}
