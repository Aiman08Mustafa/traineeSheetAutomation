package com.example.traineeSheetAutomation.repository;

import com.example.traineeSheetAutomation.entity.Role;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.entity.enums.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager tem;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role sampleRole;

    @BeforeEach
    void setUp(){
        sampleRole = new Role();
        sampleRole.setRoleName(RoleName.TRAINEE);
        sampleRole = tem.persistAndFlush(sampleRole);
    }

    private User createAndSaveUser(String name, String email){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("hashed_password");
        user.setRole(sampleRole);
        return tem.persistAndFlush(user);
    }

    @Test
    void findByEmail_whenExists_returnsUser() {
        createAndSaveUser("Aiman", "abc@example.com");

        Optional<User> found = userRepository.findByEmail("abc@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Aiman");
    }

    @Test
    void findByEmail_whenNotFound_returnsEmpty() {
        Optional<User> found = userRepository.findByEmail("nobody@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_whenCaseSensitive_ReturnEmpty() {
        createAndSaveUser("Aiman", "abc@example.com");

        Optional<User> found = userRepository.findByEmail("ABC@EXAMPLE.COM");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_whenEmailIsTaken_returnsTrue() {
        createAndSaveUser("Aiman", "abc@example.com");
        assertThat(userRepository.existsByEmail("abc@example.com")).isTrue();
    }

    @Test
    void existsByEmail_whenEmailIsFree_returnsFalse() {
        assertThat(userRepository.existsByEmail("new@example.com")).isFalse();
    }

    @Test
    void findById_whenExists_returnUser() {
        User saved = createAndSaveUser("Bob", "bob@example.com");

        Optional<User> found = userRepository.findById(saved.getUserId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void findAllUsers_whenUserExists_returnsAll() {
        createAndSaveUser("Ava", "ava@example.com");
        createAndSaveUser("Bob",   "bob@example.com");

        assertThat(userRepository.findAll()).hasSize(2);
    }

    @Test
    void deleteById_whenIdExists_removesUser() {
        User saved = createAndSaveUser("Carol", "carol@example.com");
        Long id = saved.getUserId();

        userRepository.deleteById(id);
        tem.flush();

        assertThat(userRepository.existsById(id)).isFalse();
    }

    @Test
    void existsById_whenExists_returnsTrue() {
        User saved = createAndSaveUser("Dave", "dave@example.com");
        assertThat(userRepository.existsById(saved.getUserId())).isTrue();
    }

    @Test
    void existsById_whenDoesNotExists_returnsFalse() {
        assertThat(userRepository.existsById(9999L)).isFalse();
    }
}
