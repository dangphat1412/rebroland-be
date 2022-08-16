package vn.edu.fpt.rebroland.service.impl;


import vn.edu.fpt.rebroland.entity.RefundPercent;
import vn.edu.fpt.rebroland.payload.RefundPercentDTO;
import vn.edu.fpt.rebroland.repository.RefundPercentRepository;
import vn.edu.fpt.rebroland.service.RefundPercentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class RefundPercentServiceImpl implements RefundPercentService{
    private RefundPercentRepository refundPercentRepository;

    private ModelMapper modelMapper;

    public RefundPercentServiceImpl(RefundPercentRepository refundPercentRepository, ModelMapper modelMapper) {
        this.refundPercentRepository = refundPercentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RefundPercentDTO getActiveRefundPercent(int typeId) {
        RefundPercent refundPercentDTO = refundPercentRepository.getRefundPercentByTypeId(typeId);
        return mapToDTO(refundPercentDTO);
    }

    private RefundPercentDTO mapToDTO(RefundPercent refundPercent) {
        return modelMapper.map(refundPercent, RefundPercentDTO.class);
    }

    private RefundPercent mapToEnity(RefundPercentDTO refundPercentDTO) {
        return modelMapper.map(refundPercentDTO, RefundPercent.class);
    }



}
