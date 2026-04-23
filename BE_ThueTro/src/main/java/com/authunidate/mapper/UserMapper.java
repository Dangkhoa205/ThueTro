package com.authunidate.mapper;

import com.authunidate.dto.request.RegisterRequest;
import com.authunidate.dto.response.UserResponse;
import com.authunidate.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getEmail())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .gender(user.getGender())
                .yob(user.getDob())
                .avatar(user.getAvatarUrl())
                .verified(user.isActive())
                .build();
    }

    public User toUser(RegisterRequest request) {
        return User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .gender(request.getGender())
                .dob(request.getYob())
                .active(true)
                .build();
    }
}
