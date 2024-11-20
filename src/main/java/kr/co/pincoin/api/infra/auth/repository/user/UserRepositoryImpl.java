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
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(userMapper::toModel);
    }

    @Override
    public User save(User user) {
        return userMapper.toModel(userJpaRepository.save(user.toEntity()));
    }
}