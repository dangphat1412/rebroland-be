package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.entity.Withdraw;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.repository.WithdrawRepository;
import vn.edu.fpt.rebroland.service.NotificationService;
import vn.edu.fpt.rebroland.service.WithdrawService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WithdrawServiceImpl implements WithdrawService {
    private WithdrawRepository withdrawRepository;

    private ModelMapper modelMapper;
    private NotificationService notificationService;
    private UserRepository userRepository;

    public WithdrawServiceImpl(WithdrawRepository withdrawRepository, ModelMapper modelMapper,
                               NotificationService notificationService, UserRepository userRepository) {
        this.withdrawRepository = withdrawRepository;
        this.modelMapper = modelMapper;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @Override
    public WithdrawDTO createWithdraw(WithdrawDTO withdrawDTO) {
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        withdrawDTO.setStartDate(date);
        withdrawDTO.setStatus(1);

        Withdraw withdraw = mapToEntity(withdrawDTO);
        Withdraw newWithDraw = withdrawRepository.save(withdraw);
        return mapToDTO(newWithDraw);
    }

    @Override
    public WithdrawResponse getAllDirectWithdraw(int pageNumber, int pageSize, String keyword, String option) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int sortOption = Integer.parseInt(option);
        Page<Withdraw> pageWithdraw = null;
        switch (sortOption){
            case 0:
                pageWithdraw = withdrawRepository.getAllDirectWithdraw(keyword, pageable);
                break;
            case 1:
                pageWithdraw = withdrawRepository.getAllDirectWithdrawNotProcess(keyword, pageable);
                break;
            case 2:
                pageWithdraw = withdrawRepository.getAllDirectWithdrawProcessed(keyword, pageable);
                break;

        }
        List<Withdraw> listWithdraw = pageWithdraw.getContent();
        List<WithdrawDTO> listDto = listWithdraw.stream().map(withdraw -> mapToDTO(withdraw)).collect(Collectors.toList());

        WithdrawResponse searchResponse = new WithdrawResponse();
        searchResponse.setLists(listDto);
        searchResponse.setPageNo(pageNumber + 1);
        searchResponse.setTotalPages(pageWithdraw.getTotalPages());
        searchResponse.setTotalResult(pageWithdraw.getTotalElements());
        return searchResponse;
    }

    @Override
    public WithdrawResponse getAllTransferWithdraw(int pageNumber, int pageSize, String keyword, String option) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        Page<Withdraw> pageWithdraw = withdrawRepository.getAllTransferWithdraw(pageable);
        int sortOption = Integer.parseInt(option);
        Page<Withdraw> pageWithdraw = null;
        switch (sortOption){
            case 0:
                pageWithdraw = withdrawRepository.getAllTransferWithdraw(keyword, pageable);
                break;
            case 1:
                pageWithdraw = withdrawRepository.getAllTransferWithdrawNotProcess(keyword, pageable);
                break;
            case 2:
                pageWithdraw = withdrawRepository.getAllTransferWithdrawProcessed(keyword, pageable);
                break;

        }

        List<Withdraw> listWithdraw = pageWithdraw.getContent();
        List<WithdrawDTO> listDto = listWithdraw.stream().map(withdraw -> mapToDTO(withdraw)).collect(Collectors.toList());

        WithdrawResponse searchResponse = new WithdrawResponse();
        searchResponse.setLists(listDto);
        searchResponse.setPageNo(pageNumber + 1);
        searchResponse.setTotalPages(pageWithdraw.getTotalPages());
        searchResponse.setTotalResult(pageWithdraw.getTotalElements());
        return searchResponse;
    }

    @Autowired
    SimpMessagingTemplate template;

    @Override
    public boolean acceptWithdraw(int withdrawId) {
        Withdraw withdraw = withdrawRepository.getWithdrawById(withdrawId);
        if(withdraw != null && withdraw.getStatus() == 1){
            withdraw.setStatus(2);
            withdrawRepository.save(withdraw);
            User user = withdraw.getUser();

            TextMessageDTO messageDTO = new TextMessageDTO();
            messageDTO.setMessage("Yêu cầu rút tiền của bạn được chấp nhận!");
            template.convertAndSend("/topic/message/" + user.getId(), messageDTO);

            saveNotificationAndUpdateUser("Yêu cầu rút tiền của bạn được chấp nhận !", user);

            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean rejectWithdraw(int withdrawId, String comment) {
        Withdraw withdraw = withdrawRepository.getWithdrawById(withdrawId);
        if(withdraw != null && withdraw.getStatus() == 1){
            withdraw.setStatus(3);
            withdraw.setComment(comment);
            withdrawRepository.save(withdraw);
            User user = withdraw.getUser();

            TextMessageDTO messageDTO = new TextMessageDTO();
            messageDTO.setMessage("Yêu cầu rút tiền của bạn bị từ chối!");
            template.convertAndSend("/topic/message/" + user.getId(), messageDTO);

            saveNotificationAndUpdateUser("Yêu cầu rút tiền của bạn bị từ chối!", user);

            return true;
        }else{
            return false;
        }
    }

    private void saveNotificationAndUpdateUser(String message, User user){
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserId(user.getId());
        notificationDTO.setContent(message);
        notificationDTO.setPhone(user.getPhone());
        notificationDTO.setType("Withdraw");
        notificationService.createContactNotification(notificationDTO);

        int numberUnread = user.getUnreadNotification();
        numberUnread++;
        user.setUnreadNotification(numberUnread);
        userRepository.save(user);
    }

    private WithdrawDTO mapToDTO(Withdraw withdraw) {
        return modelMapper.map(withdraw, WithdrawDTO.class);
    }

    private Withdraw mapToEntity(WithdrawDTO withdrawDTO) {
        return modelMapper.map(withdrawDTO, Withdraw.class);
    }


}