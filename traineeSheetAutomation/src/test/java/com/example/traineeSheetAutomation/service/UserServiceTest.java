package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.UserRequestDTO;
import com.example.traineeSheetAutomation.dto.UserResponseDTO;
import com.example.traineeSheetAutomation.entity.Role;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.entity.enums.RoleName;
import com.example.traineeSheetAutomation.repository.RoleRepository;
import com.example.traineeSheetAutomation.repository.UserRepository;
import com.example.traineeSheetAutomation.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private UserService userService;

    private Role sampleRole;
    private User sampleUser;
    private UserRequestDTO sampleRequest;

    @BeforeEach
    void setup(){
        sampleRole = new Role();
        sampleRole.setRoleID(1L);
        sampleRole.setRoleName(RoleName.TRAINEE);

        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("Aiman");
        sampleUser.setEmail("abc@example.com");
        sampleUser.setPassword("hashed_password");
        sampleUser.setRole(sampleRole);
        sampleUser.setCreatedAt(LocalDateTime.now());
        sampleUser.setUpdatedAt(LocalDateTime.now());

        sampleRequest = new UserRequestDTO("Aiman", "abc@example.com", "secret123", 1L);

    }

    @Test
    void createUser_whenCreated_returnSuccess() {

        when(userRepository.existsByEmail("abc@example.com")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(sampleRole));
        when(passwordUtil.hashPassword("secret123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponseDTO result = userService.createUser(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Aiman");
        assertThat(result.getEmail()).isEqualTo("abc@example.com");
        assertThat(result.getRoleName()).isEqualTo(RoleName.TRAINEE.name());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test

    void createUser_emailAlreadyExists_throwsException() {
        when(userRepository.existsByEmail("abc@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_roleNotFound_throwsException() {
        when(userRepository.existsByEmail("abc@example.com")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.createUser(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found with ID: 1");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_beforeSaving_passwordIsHashed() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(sampleRole));
        when(passwordUtil.hashPassword("secret123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {

            User saved = invocation.getArgument(0);
            saved.setUserId(1L);
            saved.setCreatedAt(LocalDateTime.now());
            saved.setUpdatedAt(LocalDateTime.now());
            return saved;
        });

        userService.createUser(sampleRequest);

        verify(passwordUtil, times(1)).hashPassword("secret123");
    }

    @Test
    void getAllUsers_whenSuccessfull_returnList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("abc@example.com");
    }

    @Test
    void getAllUsers_whenNoUsersExists_throwsException() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> userService.getAllUsers())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No users found");
    }

    @Test
    void getAllUsers_whenMultipleUsersExists_returnsAll() {
        User user2 = new User();
        user2.setUserId(2L);
        user2.setName("Ava");
        user2.setEmail("ava@example.com");
        user2.setPassword("hashed");
        user2.setRole(sampleRole);
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findAll()).thenReturn(List.of(sampleUser, user2));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserResponseDTO::getName)
                .containsExactlyInAnyOrder("Aiman", "Ava");
    }

    @Test
    void getUserById_whenExists_returnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Aiman");
    }

    @Test
    void getUserById_whenNotFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 99");
    }

    @Test
    void getUserById_whenExists_mapAllFieldsCorrectly() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserResponseDTO dto = userService.getUserById(1L);

        assertThat(dto.getUserId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Aiman");
        assertThat(dto.getEmail()).isEqualTo("abc@example.com");
        assertThat(dto.getRoleName()).isEqualTo(RoleName.TRAINEE.name());
        assertThat(dto.getCreatedAt()).isNotNull();
        assertThat(dto.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateUser_whenValidData_success() {
        UserRequestDTO updateRequest =
                new UserRequestDTO("Aiman Updated", "abc@example.com", "newpass", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(sampleRole));
        when(passwordUtil.hashPassword("newpass")).thenReturn("new_hashed");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponseDTO result = userService.updateUser(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_whenUserNotFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 99");
    }

    @Test
    void updateUser_whenEmailTakenByAnotherUser_throwsException() {
        sampleUser.setEmail("abc@example.com");
        UserRequestDTO requestWithNewEmail =
                new UserRequestDTO("Aiman", "cdf@example.com", "secret123", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail("cdf@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, requestWithNewEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void deleteUser_whenExists_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_whenDoesNotExists_throwsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 99");

        verify(userRepository, never()).deleteById(anyLong());
    }

}
