package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.RefundPercentDTO;

public interface RefundPercentService {
    RefundPercentDTO getActiveRefundPercent(int typeId);
}
