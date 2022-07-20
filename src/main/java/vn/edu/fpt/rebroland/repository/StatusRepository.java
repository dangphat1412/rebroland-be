package vn.edu.fpt.rebroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.rebroland.entity.Status;

public interface StatusRepository extends JpaRepository<Status,Integer> {
}
