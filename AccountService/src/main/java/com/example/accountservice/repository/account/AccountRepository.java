package com.example.accountservice.repository.account;

import com.example.accountservice.model.entity.Account;
import com.example.accountservice.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {
    long countByFullname(String fullname);

    @Query("SELECT a.fullname, COUNT(a) FROM Account a WHERE a.fullname IN :fullnames GROUP BY a.fullname")
    List<Object[]> countFullnames(@Param("fullnames") List<String> fullnames);

    List<Account> findAll();

}
