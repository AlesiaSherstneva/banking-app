package com.example.bankingapp.service;

import com.example.bankingapp.dto.AuthDto;
import com.example.bankingapp.dto.UserAuthDto;
import com.example.bankingapp.dto.UserDto;
import com.example.bankingapp.dto.UserFilter;
import com.example.bankingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto getUserById(Long id);

    User authenticateUser(AuthDto authDto);

    UserAuthDto getFullUserById(Long id);

    Page<UserDto> searchUsers(UserFilter filter, Pageable pageable);
}
