package vn.edu.fpt.rebroland.service.impl;


import vn.edu.fpt.rebroland.entity.RefundPercent;
import vn.edu.fpt.rebroland.payload.PercentDTO;
import vn.edu.fpt.rebroland.payload.RefundPercentDTO;
import vn.edu.fpt.rebroland.repository.RefundPercentRepository;
import vn.edu.fpt.rebroland.service.RefundPercentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private RefundPercentDTO createRefundPercentByType(int type, RefundPercent refundPercent, int percent){
        try{
            RefundPercentDTO dtoWithoutInfo = new RefundPercentDTO();
            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);
            dtoWithoutInfo.setStartDate(date);
            dtoWithoutInfo.setTypeId(type);
            if(refundPercent == null){
                dtoWithoutInfo.setPercent(percent);
                dtoWithoutInfo.setStatus(true);
                RefundPercent r = mapToEnity(dtoWithoutInfo);
                RefundPercent refund = refundPercentRepository.save(r);
                return mapToDTO(refund);
            }else{
                if(refundPercent.getPercent() == percent){
                    return mapToDTO(refundPercent);
                }
                refundPercent.setStatus(false);
                refundPercentRepository.save(refundPercent);
                dtoWithoutInfo.setPercent(percent);
                dtoWithoutInfo.setStatus(true);
                RefundPercent r = mapToEnity(dtoWithoutInfo);
                RefundPercent refund1 = refundPercentRepository.save(r);
                return mapToDTO(refund1);
            }

        }catch (Exception e){
            return null;
        }
    }

    @Override
    public Map<String, Object> createRefundPercent(PercentDTO percentDTO) {
        RefundPercent percentWithoutInfo = refundPercentRepository.getRefundPercentByTypeId(1);
        RefundPercent percentWithInfo = refundPercentRepository.getRefundPercentByTypeId(2);

        RefundPercentDTO withoutInfo = createRefundPercentByType(1, percentWithoutInfo, percentDTO.getRefundWithoutInfo());
        RefundPercentDTO withInfo = createRefundPercentByType(2, percentWithInfo, percentDTO.getRefundWithInfo());

        Map<String, Object> map = new HashMap<>();
        map.put("currentRefundWithoutInfo", withoutInfo);
        map.put("currentRefundWithInfo", withInfo);
        return map;
    }

    @Override
    public Map<String, Object> getListRefundPercent() {
        RefundPercent refundPercentType1 = refundPercentRepository.getCurrentRefundPercent(1);
        RefundPercent refundPercentType2 = refundPercentRepository.getCurrentRefundPercent(2);
        List<Integer> listPercentType1 = refundPercentRepository.getListCurrentRefundPercent(1);
        List<Integer> listPercentType2 = refundPercentRepository.getListCurrentRefundPercent(2);
        Map<String, Object> map = new HashMap<>();
        map.put("currentRefundWithoutInfo", refundPercentType1);
        map.put("listRefundWithoutInfo", listPercentType1);
        map.put("currentRefundWithInfo", refundPercentType2);
        map.put("listRefundWithInfo", listPercentType2);
        return map;
    }

    private RefundPercentDTO mapToDTO(RefundPercent refundPercent) {
        return modelMapper.map(refundPercent, RefundPercentDTO.class);
    }

    private RefundPercent mapToEnity(RefundPercentDTO refundPercentDTO) {
        return modelMapper.map(refundPercentDTO, RefundPercent.class);
    }



}
