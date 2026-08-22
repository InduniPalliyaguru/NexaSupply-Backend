package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.auth.UpdateProfileDTO;
import lk.ijse.NexaSupply.dto.auth.UserResponseDTO;
import lk.ijse.NexaSupply.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        UserResponseDTO userProfile = userService.getUserProfile(email);
        return new CommonResponse(200, userProfile, "User profile fetched successfully");
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateProfile(Authentication authentication, @Valid @RequestBody UpdateProfileDTO update) {
        String email = authentication.getName();
        UserResponseDTO userProfile = userService.updateProfile(email, update);
        return new CommonResponse(200, userProfile, "User profile updated successfully");
    }

}
