package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.FollowUpCreateDTO;
import com.example.strayanimal.dto.FollowUpQueryDTO;
import com.example.strayanimal.entity.AdoptionApplication;
import com.example.strayanimal.entity.Animal;
import com.example.strayanimal.entity.FollowUpRecord;
import com.example.strayanimal.enums.AdoptionApplicationStatus;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.AnimalMapper;
import com.example.strayanimal.mapper.FollowUpRecordMapper;
import com.example.strayanimal.security.SecurityUser;
import com.example.strayanimal.security.UserContext;
import com.example.strayanimal.service.AdoptionService;
import com.example.strayanimal.service.FollowUpService;
import com.example.strayanimal.service.SysUserService;
import com.example.strayanimal.vo.AdoptionApplicationVO;
import com.example.strayanimal.vo.AnimalVO;
import com.example.strayanimal.vo.FollowUpRecordDetailVO;
import com.example.strayanimal.vo.FollowUpRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FollowUpServiceImpl extends ServiceImpl<FollowUpRecordMapper, FollowUpRecord> implements FollowUpService {

    private final AdoptionService adoptionService;
    private final AnimalMapper animalMapper;
    private final SysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FollowUpRecordVO create(FollowUpCreateDTO createDTO) {
        AdoptionApplication application = adoptionService.getById(createDTO.getApplicationId());
        if (application == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "领养申请不存在");
        }
        if (!AdoptionApplicationStatus.SUCCESS.getCode().equals(application.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "只有领养成功的申请可以新增回访记录");
        }
        FollowUpRecord record = new FollowUpRecord();
        record.setApplicationId(application.getId());
        record.setAnimalId(application.getAnimalId());
        record.setAdopterId(application.getApplicantId());
        record.setFollowTime(createDTO.getFollowTime() == null ? LocalDateTime.now() : createDTO.getFollowTime());
        record.setContent(createDTO.getContent());
        record.setAnimalCondition(createDTO.getAnimalCondition());
        record.setImageUrl(createDTO.getImageUrl());
        record.setRemark(createDTO.getRemark());
        save(record);
        return toVO(record);
    }

    @Override
    public PageResult<FollowUpRecordVO> pageFollowUps(FollowUpQueryDTO queryDTO) {
        Page<FollowUpRecord> page = page(new Page<>(pageNum(queryDTO), pageSize(queryDTO)),
                baseQuery(queryDTO).orderByDesc(FollowUpRecord::getFollowTime));
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public PageResult<FollowUpRecordVO> pageMine(FollowUpQueryDTO queryDTO) {
        Page<FollowUpRecord> page = page(new Page<>(pageNum(queryDTO), pageSize(queryDTO)),
                baseQuery(queryDTO)
                        .eq(FollowUpRecord::getAdopterId, UserContext.currentUserId())
                        .orderByDesc(FollowUpRecord::getFollowTime));
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public FollowUpRecordDetailVO detail(Long id) {
        FollowUpRecord record = getExisting(id);
        ensureCanView(record);
        FollowUpRecordDetailVO vo = new FollowUpRecordDetailVO();
        copy(record, vo);
        AdoptionApplication application = adoptionService.getById(record.getApplicationId());
        if (application != null) {
            AdoptionApplicationVO applicationVO = new AdoptionApplicationVO();
            applicationVO.setId(application.getId());
            applicationVO.setAnimalId(application.getAnimalId());
            applicationVO.setApplicantId(application.getApplicantId());
            applicationVO.setApplicantName(application.getApplicantName());
            applicationVO.setPhone(application.getPhone());
            applicationVO.setStatus(application.getStatus());
            applicationVO.setReviewRemark(application.getReviewRemark());
            applicationVO.setCreateTime(application.getCreateTime());
            applicationVO.setUpdateTime(application.getUpdateTime());
            vo.setApplication(applicationVO);
        }
        Animal animal = animalMapper.selectById(record.getAnimalId());
        if (animal != null) {
            vo.setAnimal(toAnimalVO(animal));
        }
        vo.setAdopter(sysUserService.getUserInfo(record.getAdopterId()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFollowUp(Long id) {
        getExisting(id);
        removeById(id);
    }

    private LambdaQueryWrapper<FollowUpRecord> baseQuery(FollowUpQueryDTO queryDTO) {
        return new LambdaQueryWrapper<FollowUpRecord>()
                .eq(queryDTO.getAnimalId() != null, FollowUpRecord::getAnimalId, queryDTO.getAnimalId())
                .eq(queryDTO.getAdopterId() != null, FollowUpRecord::getAdopterId, queryDTO.getAdopterId())
                .eq(queryDTO.getApplicationId() != null, FollowUpRecord::getApplicationId, queryDTO.getApplicationId())
                .and(StringUtils.isNotBlank(queryDTO.getKeyword()), w -> w
                        .like(FollowUpRecord::getContent, queryDTO.getKeyword())
                        .or()
                        .like(FollowUpRecord::getAnimalCondition, queryDTO.getKeyword()));
    }

    private FollowUpRecord getExisting(Long id) {
        FollowUpRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "回访记录不存在");
        }
        return record;
    }

    private void ensureCanView(FollowUpRecord record) {
        SecurityUser user = UserContext.currentUser();
        if (user.getRoles().contains("ADMIN") || user.getRoles().contains("SUPER_ADMIN")) {
            return;
        }
        if (record.getAdopterId().equals(user.getUserId())) {
            return;
        }
        throw new BusinessException(ErrorCode.FORBIDDEN, "不能查看他人的回访记录");
    }

    private long pageNum(FollowUpQueryDTO queryDTO) {
        return queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
    }

    private long pageSize(FollowUpQueryDTO queryDTO) {
        return queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);
    }

    private FollowUpRecordVO toVO(FollowUpRecord record) {
        FollowUpRecordVO vo = new FollowUpRecordVO();
        copy(record, vo);
        return vo;
    }

    private void copy(FollowUpRecord record, FollowUpRecordVO vo) {
        vo.setId(record.getId());
        vo.setApplicationId(record.getApplicationId());
        vo.setAnimalId(record.getAnimalId());
        Animal animal = animalMapper.selectById(record.getAnimalId());
        if (animal != null) {
            vo.setAnimalName(animal.getName());
        }
        vo.setAdopterId(record.getAdopterId());
        vo.setAdopterName(sysUserService.getUserInfo(record.getAdopterId()).getNickname());
        vo.setFollowTime(record.getFollowTime());
        vo.setContent(record.getContent());
        vo.setAnimalCondition(record.getAnimalCondition());
        vo.setImageUrl(record.getImageUrl());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());
        vo.setUpdateTime(record.getUpdateTime());
    }

    private AnimalVO toAnimalVO(Animal animal) {
        AnimalVO vo = new AnimalVO();
        vo.setId(animal.getId());
        vo.setAnimalNo(animal.getAnimalNo());
        vo.setName(animal.getName());
        vo.setType(animal.getType());
        vo.setGender(animal.getGender());
        vo.setAgeStage(animal.getAgeStage());
        vo.setArea(animal.getArea());
        vo.setStatus(animal.getStatus());
        vo.setCoverImage(animal.getCoverImage());
        return vo;
    }
}
