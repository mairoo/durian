package kr.co.pincoin.api.infra.auth.repository.user;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    private final UserQueryRepository userQueryRepository;

    private final UserMapper userMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userMapper::toModel);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userJpaRepository.findById(id).map(userMapper::toModel);
    }

    @Override
    public User save(User user) {
        return userMapper.toModel(userJpaRepository.save(user.toEntity()));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(user.toEntity());
    }
}