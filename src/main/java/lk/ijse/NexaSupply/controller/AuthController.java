package lk.ijse.NexaSupply.controller;

import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.auth.*;
import lk.ijse.NexaSupply.security.JwtUtil;
import lk.ijse.NexaSupply.service.AuditLogService;
import lk.ijse.NexaSupply.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuditLogService auditLogService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse registerRetailer(@Valid @RequestBody RegisterRequestDTO request) {
        userService.registerRetailer(request);
        return new CommonResponse(201, "Retailer registered successfully", "Success");
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse authLogin(@Valid @RequestBody LoginRequestDTO authDTO) {
        UserDTO userDetails = userService.getUserDetails(authDTO.getEmail(), authDTO.getPassword());

        String token = jwtUtil.generateToken(
                userDetails.getEmail(),
                userDetails.getRole().name(),
                userDetails.getUserCode()
        );

        UserDataDTO userDataDTO = new UserDataDTO();
        userDataDTO.setUserId(userDetails.getUserId());
        userDataDTO.setUserCode(userDetails.getUserCode());
        userDataDTO.setRole(userDetails.getRole().name());
        userDataDTO.setToken(token);

        auditLogService.logAction(userDetails.getEmail(), "USER_LOGIN_SUCCESS");
        return new CommonResponse(200, userDataDTO, "Login successfully!");
    }

    @PostMapping(value = "/forgot-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse sendForgotPasswordOtp(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        userService.sendForgotPasswordOtp(dto);
        return new CommonResponse(200, "OTP sent successfully to your email!", "Success");
    }

    @PostMapping(value = "/verify-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO dto) {
        boolean isValid = userService.verifyOtp(dto);
        return new CommonResponse(200, isValid, "OTP verified successfully!");
    }

    @PostMapping(value = "/reset-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        userService.resetPassword(dto);
        return new CommonResponse(200, "Password reset successfully!");
    }

}
