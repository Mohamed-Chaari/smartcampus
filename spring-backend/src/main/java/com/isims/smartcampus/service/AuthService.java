package com.isims.smartcampus.service;

import com.isims.smartcampus.dto.LoginRequestDto;
import com.isims.smartcampus.dto.LoginResponseDto;
import com.isims.smartcampus.entity.CampusUser;
import com.isims.smartcampus.repository.CampusUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final CampusUserRepository userRepository;

    public AuthService(CampusUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Optional<CampusUser> userOpt = userRepository.findByUserId(request.getUserId());
        
        if (userOpt.isPresent()) {
            CampusUser user = userOpt.get();
            if (user.getPassword().equals(request.getPassword())) {
                return new LoginResponseDto(true, "Login successful", user.getUserId(), user.getName(), user.getRole());
            } else {
                return new LoginResponseDto(false, "Invalid password", null, null, null);
            }
        }
        
        return new LoginResponseDto(false, "User not found", null, null, null);
    }
}
