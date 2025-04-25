package com.example.bankingapp.service;

import com.example.bankingapp.dto.AuthDto;
import com.example.bankingapp.dto.UserAuthDto;
import com.example.bankingapp.dto.UserDto;
import com.example.bankingapp.dto.UserFilter;
import com.example.bankingapp.exception.InvalidCredentialsException;
import com.example.bankingapp.exception.UserNotFoundException;
import com.example.bankingapp.model.EmailData;
import com.example.bankingapp.model.PhoneData;
import com.example.bankingapp.model.User;
import com.example.bankingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Cacheable(value = "users", key = "#id")
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return convertToDto(user);
    }


    public User authenticateUser(AuthDto authDto) {
        User user = userRepository.findByEmail(authDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException(authDto.getEmail()));

        if(!passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
            log.warn("Failed login for email {}: wrong password", authDto.getEmail());
            throw new InvalidCredentialsException();
        }

        return user;
    }

    public UserAuthDto getFullUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return convertToAuthDto(user);
    }

    public Page<UserDto> searchUsers(UserFilter filter, Pageable pageable) {
        Specification<User> spec = Specification.where(null);

        if (filter.getName() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("name"), filter.getName() + "%"));
        }

        if (filter.getDateOfBirth() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThan(root.get("dateOfBirth"), filter.getDateOfBirth()));
        }

        if (filter.getEmail() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("emails").get("email"), filter.getEmail()));
        }

        if (filter.getPhone() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("phones").get("phone"), filter.getPhone()));
        }

        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(this::convertToDto);
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .emails(user.getEmails().stream()
                        .map(EmailData::getEmail)
                        .collect(Collectors.toSet()))
                .phones(user.getPhones().stream()
                        .map(PhoneData::getPhone)
                        .collect(Collectors.toSet()))
                .build();
    }

    private UserAuthDto convertToAuthDto(User user) {
        return UserAuthDto.builder()
                .id(user.getId())
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .emails(user.getEmails().stream()
                        .map(EmailData::getEmail)
                        .collect(Collectors.toSet()))
                .phones(user.getPhones().stream()
                        .map(PhoneData::getPhone)
                        .collect(Collectors.toSet()))
                .balance(user.getAccount().getBalance())
                .build();
    }
}
