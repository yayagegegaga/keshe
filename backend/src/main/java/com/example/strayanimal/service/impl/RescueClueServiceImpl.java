package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.RescueClueCreateDTO;
import com.example.strayanimal.dto.RescueClueQueryDTO;
import com.example.strayanimal.dto.RescueClueReviewDTO;
import com.example.strayanimal.entity.RescueClue;
import com.example.strayanimal.enums.AnimalType;
import com.example.strayanimal.enums.EmergencyLevel;
import com.example.strayanimal.enums.RescueClueStatus;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.RescueClueMapper;
import com.example.strayanimal.security.SecurityUser;
import com.example.strayanimal.security.UserContext;
import com.example.strayanimal.service.RescueClueService;
import com.example.strayanimal.service.RescueOrderService;
import com.example.strayanimal.service.StatisticsService;
import com.example.strayanimal.vo.RescueClueDetailVO;
import com.example.strayanimal.vo.RescueClueVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RescueClueServiceImpl extends ServiceImpl<RescueClueMapper, RescueClue> implements RescueClueService {

    private final RescueOrderService rescueOrderService;

    private final StatisticsService statisticsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueClueVO create(RescueClueCreateDTO createDTO) {
        validateAnimalType(createDTO.getAnimalType());
        validateEmergencyLevel(createDTO.getEmergencyLevel());
        RescueClue clue = new RescueClue();
        clue.setSubmitterId(UserContext.currentUserId());
        clue.setAnimalType(createDTO.getAnimalType());
        clue.setLocation(createDTO.getLocation());
        clue.setDescription(createDTO.getDescription());
        clue.setEmergencyLevel(createDTO.getEmergencyLevel());
        clue.setImageUrl(createDTO.getImageUrl());
        clue.setContact(createDTO.getContact());
        clue.setStatus(RescueClueStatus.PENDING.getCode());
        save(clue);
        // 普通用户提交新线索 → 管理员首页"待审核线索"数 +1，立即失效所有管理员统计缓存
        statisticsService.evictAdminOverviewCache();
        return toVO(clue);
    }

    @Override
    public PageResult<RescueClueVO> pageAll(RescueClueQueryDTO queryDTO) {
        validateQuery(queryDTO);
        return pageByQuery(queryDTO, queryDTO.getSubmitterId());
    }

    @Override
    public PageResult<RescueClueVO> pageMine(RescueClueQueryDTO queryDTO) {
        validateQuery(queryDTO);
        return pageByQuery(queryDTO, UserContext.currentUserId());
    }

    @Override
    public RescueClueDetailVO detail(Long id) {
        RescueClue clue = getExisting(id);
        SecurityUser current = UserContext.currentUser();
        if (!isAdmin(current) && !clue.getSubmitterId().equals(current.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不能查看他人的救助线索");
        }
        RescueClueDetailVO vo = new RescueClueDetailVO();
        copy(clue, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueClueVO review(Long id, RescueClueReviewDTO reviewDTO) {
        RescueClue clue = getExisting(id);
        if (!RescueClueStatus.PENDING.getCode().equals(clue.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "只有待审核线索可以审核");
        }
        clue.setReviewerId(UserContext.currentUserId());
        clue.setReviewRemark(reviewDTO.getReviewRemark());
        if (Boolean.FALSE.equals(reviewDTO.getApproved())) {
            clue.setStatus(RescueClueStatus.INVALID.getCode());
            updateById(clue);
            statisticsService.evictAdminOverviewCache();
            return toVO(clue);
        }

        clue.setStatus(RescueClueStatus.CONVERTED.getCode());
        updateById(clue);
        boolean createOrder = reviewDTO.getCreateOrder() == null || reviewDTO.getCreateOrder();
        if (createOrder) {
            String title = StringUtils.isBlank(reviewDTO.getOrderTitle())
                    ? "救助工单-" + clue.getLocation()
                    : reviewDTO.getOrderTitle();
            String description = StringUtils.isBlank(reviewDTO.getOrderDescription())
                    ? clue.getDescription()
                    : reviewDTO.getOrderDescription();
            rescueOrderService.createFromClue(clue.getId(), title, description,
                    clue.getLocation(), clue.getEmergencyLevel(), UserContext.currentUserId());
        }
        statisticsService.evictAdminOverviewCache();
        return toVO(clue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueClueVO cancel(Long id) {
        RescueClue clue = getExisting(id);
        SecurityUser current = UserContext.currentUser();
        if (!isAdmin(current) && !clue.getSubmitterId().equals(current.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "非提交人不能取消线索");
        }
        if (!RescueClueStatus.PENDING.getCode().equals(clue.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "只有待审核线索可以取消");
        }
        clue.setStatus(RescueClueStatus.CANCELED.getCode());
        updateById(clue);
        return toVO(clue);
    }

    private PageResult<RescueClueVO> pageByQuery(RescueClueQueryDTO queryDTO, Long fixedSubmitterId) {
        long pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
        long pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);
        LambdaQueryWrapper<RescueClue> wrapper = new LambdaQueryWrapper<RescueClue>()
                .eq(StringUtils.isNotBlank(queryDTO.getStatus()), RescueClue::getStatus, queryDTO.getStatus())
                .eq(StringUtils.isNotBlank(queryDTO.getAnimalType()), RescueClue::getAnimalType, queryDTO.getAnimalType())
                .eq(StringUtils.isNotBlank(queryDTO.getEmergencyLevel()), RescueClue::getEmergencyLevel, queryDTO.getEmergencyLevel())
                .eq(fixedSubmitterId != null, RescueClue::getSubmitterId, fixedSubmitterId)
                .and(StringUtils.isNotBlank(queryDTO.getKeyword()), w -> w
                        .like(RescueClue::getLocation, queryDTO.getKeyword())
                        .or()
                        .like(RescueClue::getDescription, queryDTO.getKeyword()))
                .orderByDesc(RescueClue::getCreateTime);
        Page<RescueClue> page = page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    private RescueClue getExisting(Long id) {
        RescueClue clue = getById(id);
        if (clue == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "救助线索不存在");
        }
        return clue;
    }

    private void validateQuery(RescueClueQueryDTO queryDTO) {
        if (StringUtils.isNotBlank(queryDTO.getStatus()) && !RescueClueStatus.contains(queryDTO.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "线索状态不合法");
        }
        if (StringUtils.isNotBlank(queryDTO.getAnimalType())) {
            validateAnimalType(queryDTO.getAnimalType());
        }
        if (StringUtils.isNotBlank(queryDTO.getEmergencyLevel())) {
            validateEmergencyLevel(queryDTO.getEmergencyLevel());
        }
    }

    private void validateAnimalType(String animalType) {
        if (!AnimalType.contains(animalType)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "动物类型不合法");
        }
    }

    private void validateEmergencyLevel(String emergencyLevel) {
        if (!EmergencyLevel.contains(emergencyLevel)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "紧急程度不合法");
        }
    }

    private boolean isAdmin(SecurityUser user) {
        return user.getRoles().contains("ADMIN") || user.getRoles().contains("SUPER_ADMIN");
    }

    private RescueClueVO toVO(RescueClue clue) {
        RescueClueVO vo = new RescueClueVO();
        copy(clue, vo);
        return vo;
    }

    private void copy(RescueClue clue, RescueClueVO vo) {
        vo.setId(clue.getId());
        vo.setSubmitterId(clue.getSubmitterId());
        vo.setAnimalType(clue.getAnimalType());
        vo.setLocation(clue.getLocation());
        vo.setDescription(clue.getDescription());
        vo.setEmergencyLevel(clue.getEmergencyLevel());
        vo.setImageUrl(clue.getImageUrl());
        vo.setContact(clue.getContact());
        vo.setStatus(clue.getStatus());
        vo.setReviewerId(clue.getReviewerId());
        vo.setReviewRemark(clue.getReviewRemark());
        vo.setCreateTime(clue.getCreateTime());
        vo.setUpdateTime(clue.getUpdateTime());
    }
}
