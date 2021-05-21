package com.Project.CoronaTracker;

import com.Project.CoronaTracker.Corona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * repository class which extends JpaRepository
 */
@Repository
public interface CoronaRepository extends JpaRepository<Corona,Long> {

    /**
     * Method to check if table is empty
     * @return integer representing table is empty or not. 0 - Empty table, 1 - Non-Empty table
     */
    @Query(value = "SELECT EXISTS(SELECT 1 FROM Corona)",nativeQuery = true)
    Integer checkEmptyTable();

}
