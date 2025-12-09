package ru.practicum.ewm.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u " +
            "WHERE (:ids IS NULL OR u.id IN :ids)")
    Page<User> findByIdIn(
            @Param("ids") Iterable<Long> ids,
            Pageable pageable
    );

    boolean existsByEmail(String email);
}