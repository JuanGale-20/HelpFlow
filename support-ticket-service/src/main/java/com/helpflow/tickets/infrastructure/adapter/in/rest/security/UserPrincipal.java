package com.helpflow.tickets.infrastructure.adapter.in.rest.security;

import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private final UserId userId;
    private final String email;
    private final Role role;
    private final String name;

    public UserPrincipal(UserId userId, String email, Role role, String name) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.name = name;
    }

    public UserId getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
