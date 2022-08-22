package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.PercentDTO;
import vn.edu.fpt.rebroland.payload.RefundPercentDTO;

import java.util.Map;

public interface RefundPercentService {
    RefundPercentDTO getActiveRefundPercent(int typeId);
    Map<String, Object> createRefundPercent(PercentDTO refundPercentDTO);

    Map<String, Object> getListRefundPercent();
}
