package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.RefundPercent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RefundPercentRepository extends JpaRepository<RefundPercent,Integer> {
    @Query(value = "select * from refund_percent where type_id =:typeId and status = true",nativeQuery = true)
    RefundPercent getRefundPercentByTypeId(int typeId);
}
