package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Withdraw;
import vn.edu.fpt.rebroland.payload.WithdrawDTO;
import vn.edu.fpt.rebroland.repository.WithdrawRepository;
import vn.edu.fpt.rebroland.service.WithdrawService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class WithdrawServiceImpl implements WithdrawService {
    private WithdrawRepository withdrawRepository;

    private ModelMapper modelMapper;

    public WithdrawServiceImpl(WithdrawRepository withdrawRepository, ModelMapper modelMapper) {
        this.withdrawRepository = withdrawRepository;
        this.modelMapper = modelMapper;
    }

    private WithdrawDTO mapToDTO(Withdraw withdraw) {
        return modelMapper.map(withdraw, WithdrawDTO.class);
    }

    private Withdraw mapToEntity(WithdrawDTO withdrawDTO) {
        return modelMapper.map(withdrawDTO, Withdraw.class);
    }
}
