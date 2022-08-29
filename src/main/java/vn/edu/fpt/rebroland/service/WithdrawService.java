package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.WithdrawDTO;
import vn.edu.fpt.rebroland.payload.WithdrawResponse;

import java.util.List;

public interface WithdrawService {
    WithdrawDTO createWithdraw(WithdrawDTO withdrawDTO);
    WithdrawResponse getAllDirectWithdraw(int pageNumber, int pageSize, String keyword, String option);
    WithdrawResponse getAllTransferWithdraw(int pageNumber, int pageSize, String keyword, String option);
    boolean acceptWithdraw(int withdrawId);
    boolean rejectWithdraw(int withdrawId, String comment);
}
