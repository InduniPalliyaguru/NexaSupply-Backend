package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.RegisterRequestDTO;
import lk.ijse.NexaSupply.dto.UpdateProfileDTO;
import lk.ijse.NexaSupply.dto.UserDTO;
import lk.ijse.NexaSupply.dto.UserResponseDTO;
import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.ProfileStatus;
import lk.ijse.NexaSupply.enumeration.Role;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.UserRepository;
import lk.ijse.NexaSupply.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerRetailer(RegisterRequestDTO request) {
        log.info("Execute Register Retailer method");

        if (userRepository.existsActiveByEmail(request.getEmail())) {
            throw new CustomException(400, "Email is already registered");
        }

        int currentYear = Year.now().getValue();
        long count = userRepository.countAllUsers() + 1;
        String userCode = String.format("USR-%d-%04d", currentYear, count);

        User user = new User();
        user.setUserCode(userCode);
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setShopName(request.getShopName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(Role.ROLE_RETAILER);
        user.setProfileStatus(ProfileStatus.PENDING);
        user.setCreditLimit(0.0);
        user.setDataStatus(DataStatus.ACTIVE);

        userRepository.save(user);
    }

    @Override
    public void createAdmin(RegisterRequestDTO request) {
        log.info("Execute Register Admin method");

        if (userRepository.existsActiveByEmail(request.getEmail())) {
            throw new CustomException(400, "Email is already registered");
        }
        int currentYear = Year.now().getValue();
        long count = userRepository.countAllUsers() + 1;
        String admCode = String.format("ADM-%d-%04d", currentYear, count);

        User admin = new User();
        admin.setUserCode(admCode);
        admin.setFullName(request.getFullName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setPhone(request.getPhone());
        admin.setAddress(request.getAddress());
        admin.setRole(Role.ROLE_ADMIN);
        admin.setProfileStatus(ProfileStatus.APPROVED);
        admin.setDataStatus(DataStatus.ACTIVE);

        userRepository.save(admin);
    }

    @Override
    public UserDTO getUserDetails(String email, String password) {
        log.info("Execute Get User Details method");

        Optional<User> activeByEmail = userRepository.findActiveByEmail(email);

        if (activeByEmail.isEmpty()) {
            throw new CustomException(404, "Invalid email!");
        }

        User user = activeByEmail.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(401, "Password doesn't match!");
        }
        if (user.getProfileStatus() == ProfileStatus.PENDING) {
            throw new CustomException(403, "Your account is pending admin approval!");
        }
        if (user.getProfileStatus() == ProfileStatus.REJECTED) {
            throw new CustomException(403, "Your account has been rejected by admin!");
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUserCode(user.getUserCode());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setProfileStatus(user.getProfileStatus());

        return userDTO;
    }

    @Override
    public void approveUser(String userCode, double creditLimit) {
        log.info("Execute Approve User method");

        Optional<User> optionalUser = userRepository.findByUserCodeAndDataStatus(userCode, DataStatus.ACTIVE);
        if (optionalUser.isEmpty()) {
            throw new CustomException(404, "User not found with code: " + userCode);
        }
        User user = optionalUser.get();
        user.setProfileStatus(ProfileStatus.APPROVED);
        user.setCreditLimit(creditLimit);

        userRepository.save(user);
    }

    @Override
    public void rejectUser(String userCode) {
        log.info("Execute Reject User method");

        Optional<User> optionalUser = userRepository.findByUserCodeAndDataStatus(userCode, DataStatus.ACTIVE);
        if (optionalUser.isEmpty()) {
            throw new CustomException(404, "User not found with code: " + userCode);
        }
        User user = optionalUser.get();
        user.setProfileStatus(ProfileStatus.REJECTED);
        user.setDataStatus(DataStatus.INACTIVE);

        userRepository.save(user);
    }

    @Override
    public List<UserResponseDTO> getPendingUsers() {
        log.info("Execute Get Pending User Details method");

        List<User> userList = userRepository.findByProfileStatusAndDataStatus(ProfileStatus.PENDING, DataStatus.ACTIVE);

        return mapToDTOList(userList);
    }

    @Override
    public List<UserResponseDTO> getUsersByRole(Role role) {
        log.info("Execute Get User Details By Role method");

        List<User> users = userRepository.findByRoleAndDataStatus(role, DataStatus.ACTIVE);

        return mapToDTOList(users);
    }

    @Override
    public List<UserResponseDTO> getAllActiveUsers() {
        log.info("Execute Get All Active Users method");

        List<User> allActiveUsers = userRepository.findAllActiveUsers();

        return mapToDTOList(allActiveUsers);
    }

    @Override
    public UserResponseDTO getUserProfile(String email) {
        log.info("Execute Get User Profile method");

        Optional<User> active = userRepository.findActiveByEmail(email);
        if (active.isEmpty()) {
            throw new CustomException(404, "User not found with email: " + email);
        }
        User user = active.get();
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setUserCode(user.getUserCode());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setAddress(user.getAddress());
        userDTO.setCreditLimit(user.getCreditLimit());

        return userDTO;
    }

    @Override
    public List<UserResponseDTO> getUsersByCode(String code) {
        log.info("Execute Get User Details By Code method");

        List<User> users = userRepository.findByUserCodeContainingIgnoreCaseAndDataStatus(code, DataStatus.ACTIVE);

        return mapToDTOList(users);
    }

    @Override
    public List<UserResponseDTO> getUsersByEmail(String email) {
        log.info("Execute Get User Details By Email method");

        List<User> userList = userRepository.findByEmailContainingIgnoreCaseAndDataStatus(email, DataStatus.ACTIVE);

        return mapToDTOList(userList);
    }

    @Override
    public void deleteUser(String userCode) {
        log.info("Execute Delete User method");

        Optional<User> optionalUser = userRepository.findByUserCodeAndDataStatus(userCode, DataStatus.ACTIVE);
        if (optionalUser.isEmpty()) {
            throw new CustomException(404, "User not found with code: " + userCode);
        }
        User user = optionalUser.get();
        user.setDataStatus(DataStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    public UserResponseDTO updateProfile(String email, UpdateProfileDTO request) {
        log.info("Execute Update User method");

        Optional<User> activeByEmail = userRepository.findActiveByEmail(email);
        if (activeByEmail.isEmpty()) {
            throw new CustomException(404, "User not found with email: " + email);
        }
        User user = activeByEmail.get();
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        if (request.getShopName() != null && !request.getShopName().trim().isEmpty()) {
            user.setShopName(request.getShopName());
        }
        User updateUser = userRepository.save(user);

        return mapToUserResponse(updateUser);
    }

    private UserResponseDTO mapToUserResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getUserId());
        dto.setUserCode(user.getUserCode());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setShopName(user.getShopName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        dto.setProfileStatus(user.getProfileStatus());
        dto.setCreditLimit(user.getCreditLimit());
        return dto;
    }

    private List<UserResponseDTO> mapToDTOList(List<User> users) {
        List<UserResponseDTO> dtoList = new ArrayList<>();
        for (User user : users) {
            dtoList.add(mapToUserResponse(user));
        }
        return dtoList;
    }
}
