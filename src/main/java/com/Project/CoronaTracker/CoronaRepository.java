package com.Project.CoronaTracker;

import com.Project.CoronaTracker.Corona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CoronaRepository extends JpaRepository<Corona,Long> {
    List<Corona> findByLastUpdateBetween(LocalDateTime from, LocalDateTime to);

    List<Corona> findByCombinedKey(String combinedKey);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM Corona)",nativeQuery = true)
    Integer checkEmptyTable();

}
