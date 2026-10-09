package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT obj USER " +
            "FROM User obj " +
            "WHERE UPPER(obj.firstName) LIKE UPPER(CONCAT('%',:name,'%'))")
    Page<User> searchByName(String name, Pageable pageable);
}
