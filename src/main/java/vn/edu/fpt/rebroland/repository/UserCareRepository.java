package vn.edu.fpt.rebroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.rebroland.entity.UserCare;

public interface UserCareRepository extends JpaRepository<UserCare,Integer> {
}
