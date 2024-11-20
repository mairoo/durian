package kr.co.pincoin.api.domain.auth.repository.user;

import kr.co.pincoin.api.domain.auth.model.user.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);
}