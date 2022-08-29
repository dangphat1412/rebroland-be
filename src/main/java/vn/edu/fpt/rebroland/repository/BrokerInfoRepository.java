package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.BrokerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface BrokerInfoRepository extends JpaRepository<BrokerInfo, Integer> {
    @Query(value = " SELECT * FROM `broker_info`" +
            " WHERE end_date < :date ", nativeQuery = true)
    List<BrokerInfo> getExpiredBrokerByDate(Date date);
}
