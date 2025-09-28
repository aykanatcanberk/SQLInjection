package com.canbe.SqlInjection;

import com.canbe.SqlInjection.model.User;
import com.canbe.SqlInjection.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositorySqlInjectionTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByEmail should treat payloads as data (no SQL injection)")
    void findByEmail_shouldNotBeVulnerableToSqlInjection() {
        // arrange: save a normal user
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("dummy")
                .phoneNumber("0123456789")
                .userRole(null)
                .isActive(true)
                .build();
        userRepository.save(user);

        String payload = "test@example.com' OR '1'='1";
        var result = userRepository.findByEmail(payload);

        assertThat(result).isEmpty();

        var correct = userRepository.findByEmail("test@example.com");
        assertThat(correct).isPresent();
        assertThat(correct.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("storing values that contain SQL metacharacters should not break queries")
    void storeEmailWithSqlChars_andFetchByExactValue() {
        String trickyEmail = "weird'email\"+;--@example.com";
        User u = User.builder()
                .firstName("Weird")
                .lastName("Chars")
                .email(trickyEmail)
                .password("dummy")
                .phoneNumber("000")
                .userRole(null)
                .isActive(true)
                .build();
        userRepository.save(u);

        var found = userRepository.findByEmail(trickyEmail);

        // repository should return the same record (literal match)
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(trickyEmail);
    }
}
