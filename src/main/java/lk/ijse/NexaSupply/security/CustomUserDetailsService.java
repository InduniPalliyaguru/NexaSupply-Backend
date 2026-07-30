package lk.ijse.NexaSupply.security;

import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> optionalUser = userRepository.findByUserCodeAndDataStatus(email, DataStatus.ACTIVE);

        if (optionalUser.isEmpty()) {
            throw new CustomException(404, "User not found with email: " + email);
        }

        User user = optionalUser.get();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name().replace("ROLE_", ""))
                .build();
    }

}
