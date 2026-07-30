package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.RegisterRequestDTO;
import lk.ijse.NexaSupply.dto.UserDTO;
import lk.ijse.NexaSupply.dto.UserResponseDTO;
import lk.ijse.NexaSupply.enumeration.Role;

import java.util.List;

public interface UserService {

    void registerRetailer(RegisterRequestDTO request);

    void createAdmin(RegisterRequestDTO request);

    UserDTO getUserDetails(String email, String password);

    void approveUser(String userCode, double creditLimit);

    void rejectUser(String userCode);

    List<UserResponseDTO> getPendingUsers();

    List<UserResponseDTO> getUsersByRole(Role role);

    List<UserResponseDTO> getAllActiveUsers();

    UserResponseDTO getUserProfile(String email);

    List<UserResponseDTO> getUsersByCode(String code);

    List<UserResponseDTO> getUsersByEmail(String email);

    void deleteUser(String userCode);

}
