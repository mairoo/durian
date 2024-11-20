package kr.co.pincoin.api.app.member.user.service;

import kr.co.pincoin.api.app.member.user.request.EmailUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.PasswordUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.UsernameUpdateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Create

    // Read
    public User
    find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private User
    findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    // Update
    @Transactional
    public User
    updateUsername(Long id, UsernameUpdateRequest request) {
        User user = findUser(id);
        user.updateUsername(request.getUsername());
        return userRepository.save(user);
    }

    @Transactional
    public User
    updateEmail(Long id, EmailUpdateRequest request) {
        User user = findUser(id);
        user.updateEmail(request.getEmail());
        return userRepository.save(user);
    }

    @Transactional
    public User
    updatePassword(Long id, PasswordUpdateRequest request) {
        User user = findUser(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encodedPassword);
        return userRepository.save(user);
    }

    // Delete
}