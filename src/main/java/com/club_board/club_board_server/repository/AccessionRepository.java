package com.club_board.club_board_server.repository;

import com.club_board.club_board_server.domain.Accession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccessionRepository extends JpaRepository<Accession, Long> {
    @Query("SELECT a.club.id, a.role FROM Accession a WHERE a.user.id = :userId")
    List<Object[]> findClubIdAndRoleByUserId(@Param("userId") Long userId);
}
