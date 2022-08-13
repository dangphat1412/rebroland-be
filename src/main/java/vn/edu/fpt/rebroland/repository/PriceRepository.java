package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price,Integer> {
    @Query(value = "select * from prices where type_id =:typeId and status = true ", nativeQuery = true)
    Price getPriceByTypeIdAndStatus(int typeId);

    @Query(value = "select * from prices where type_id =:typeId and status = true " +
            " AND unit_date = :unitDate", nativeQuery = true)
    Price getPrice(int typeId, int unitDate);

    @Query(value = "select * from prices where id =:priceId", nativeQuery = true)
    Price getPriceBroker(int priceId);

    @Query(value = "select * from prices where type_id =:typeId and status = true", nativeQuery = true)
    List<Price> getListPriceBroker(int typeId);
}
