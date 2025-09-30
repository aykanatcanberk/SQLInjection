package com.canbe.SqlInjection.vulnerable;
import com.canbe.SqlInjection.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VulnerableUserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE email = '" +
            ":email" + "' AND password = '" + ":password" + "'",
            nativeQuery = true)
    Optional<User> vulnerableLogin(@Param("email") String email,
                                   @Param("password") String password);
}