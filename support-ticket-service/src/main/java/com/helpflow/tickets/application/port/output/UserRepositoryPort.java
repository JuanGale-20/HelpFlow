package com.helpflow.tickets.application.port.output;

import com.helpflow.tickets.domain.model.User;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(String email);

    Optional<User> findByDocument(String document);

    boolean existsByEmail(String email);

    boolean existsByDocument(String document);
}
