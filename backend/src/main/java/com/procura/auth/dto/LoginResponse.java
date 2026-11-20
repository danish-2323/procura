package com.procura.auth.dto;

import com.procura.user.dto.UserDto;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    UserDto user
) {}
