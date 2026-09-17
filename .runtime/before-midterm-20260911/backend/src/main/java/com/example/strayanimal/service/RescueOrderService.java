package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.RescueOrderAssignDTO;
import com.example.strayanimal.dto.RescueOrderCancelDTO;
import com.example.strayanimal.dto.RescueOrderCloseDTO;
import com.example.strayanimal.dto.RescueOrderCreateDTO;
import com.example.strayanimal.dto.RescueOrderFinishDTO;
import com.example.strayanimal.dto.RescueOrderQueryDTO;
import com.example.strayanimal.entity.RescueOrder;
import com.example.strayanimal.vo.RescueOrderDetailVO;
import com.example.strayanimal.vo.RescueOrderLogVO;
import com.example.strayanimal.vo.RescueOrderVO;

import java.util.List;

public interface RescueOrderService extends IService<RescueOrder> {

    RescueOrderVO create(RescueOrderCreateDTO createDTO);

    RescueOrder createFromClue(Long clueId, String title, String description, String location, String emergencyLevel, Long creatorId);

    PageResult<RescueOrderVO> pageOrders(RescueOrderQueryDTO queryDTO);

    RescueOrderDetailVO detail(Long id);

    RescueOrderVO assign(Long id, RescueOrderAssignDTO assignDTO);

    RescueOrderVO start(Long id);

    RescueOrderVO finish(Long id, RescueOrderFinishDTO finishDTO);

    RescueOrderVO close(Long id, RescueOrderCloseDTO closeDTO);

    RescueOrderVO cancel(Long id, RescueOrderCancelDTO cancelDTO);

    List<RescueOrderLogVO> logs(Long id);
    void deleteOrder(Long id);
}
