package com.isims.smartcampus.dto;

import com.isims.smartcampus.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private boolean success;
    private String message;
    private String userId;
    private String name;
    private UserRole role;
}
