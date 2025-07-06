package com.kyj.fmk.sec.repository.jpa;

import com.kyj.fmk.sec.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
}
