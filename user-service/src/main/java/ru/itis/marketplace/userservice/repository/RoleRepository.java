package ru.itis.marketplace.userservice.repository;

import ru.itis.marketplace.userservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {

}
