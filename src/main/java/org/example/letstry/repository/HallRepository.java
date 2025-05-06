package org.example.letstry.repository;

import org.example.letstry.model.Building;
import org.example.letstry.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;



@Repository
public interface HallRepository extends JpaRepository<Hall, Long>{
    List<Hall> findByBuilding(Building building);
}
