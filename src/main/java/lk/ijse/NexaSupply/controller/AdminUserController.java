package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.auth.RegisterRequestDTO;
import lk.ijse.NexaSupply.dto.auth.UserResponseDTO;
import lk.ijse.NexaSupply.enumeration.Role;
import lk.ijse.NexaSupply.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/admin/users")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse createAdmin(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        userService.createAdmin(requestDTO);
        return new CommonResponse(201, "Admin user created successfully", "Success");
    }

    @PutMapping(value = "/approve/{userCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse approveUser(@PathVariable String userCode, @RequestParam double creditLimit) {
        userService.approveUser(userCode, creditLimit);
        return new CommonResponse(200, "User approved successfully", "Success");
    }

    @PutMapping(value = "/reject/{userCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse rejectUser(@PathVariable String userCode) {
        userService.rejectUser(userCode);
        return new CommonResponse(200, "User rejected successfully", "Success");
    }

    @GetMapping(value = "/pending", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPendingUsers() {
        List<UserResponseDTO> pendingUsers = userService.getPendingUsers();
        return new CommonResponse(200, pendingUsers, "Pending users fetched successfully");
    }

    @GetMapping(value = "/role/{role}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getUsersByRole(@PathVariable Role role) {
        List<UserResponseDTO> usersByRole = userService.getUsersByRole(role);
        return new CommonResponse(200, usersByRole, "Users fetched by role successfully");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllActiveUsers() {
        List<UserResponseDTO> allActiveUsers = userService.getAllActiveUsers();
        return new CommonResponse(200, allActiveUsers, "Users fetched successfully");
    }

    @GetMapping(value = "/search/code", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse searchUserByCode(@RequestParam String code) {
        List<UserResponseDTO> usersByCode = userService.getUsersByCode(code);
        return new CommonResponse(200, usersByCode, "Users fetched successfully");
    }

    @GetMapping(value = "/search/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse searchUserByEmail(@RequestParam String email) {
        List<UserResponseDTO> usersByEmail = userService.getUsersByEmail(email);
        return new CommonResponse(200, usersByEmail, "Users fetched successfully");
    }

    @DeleteMapping(value = "/{userCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteUser(@PathVariable String userCode) {
        userService.deleteUser(userCode);
        return new CommonResponse(200, "User deleted successfully", "Success");
    }

}