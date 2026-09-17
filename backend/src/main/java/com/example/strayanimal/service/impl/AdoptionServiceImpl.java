package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.AdoptionApplyDTO;
import com.example.strayanimal.dto.AdoptionCancelDTO;
import com.example.strayanimal.dto.AdoptionQueryDTO;
import com.example.strayanimal.dto.AdoptionReviewDTO;
import com.example.strayanimal.entity.AdoptionApplication;
import com.example.strayanimal.entity.Animal;
import com.example.strayanimal.enums.AdoptionApplicationStatus;
import com.example.strayanimal.enums.AdoptionReviewOperationType;
import com.example.strayanimal.enums.AnimalStatus;
import com.example.strayanimal.enums.NotificationType;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.AdoptionApplicationMapper;
import com.example.strayanimal.mapper.AnimalMapper;
import com.example.strayanimal.security.SecurityUser;
import com.example.strayanimal.security.UserContext;
import com.example.strayanimal.service.AdoptionReviewLogService;
import com.example.strayanimal.service.AdoptionService;
import com.example.strayanimal.service.NotificationService;
import com.example.strayanimal.service.SysUserService;
import com.example.strayanimal.vo.AdoptionApplicationDetailVO;
import com.example.strayanimal.vo.AdoptionApplicationVO;
import com.example.strayanimal.vo.AdoptionReviewLogVO;
import com.example.strayanimal.vo.AnimalVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdoptionServiceImpl extends ServiceImpl<AdoptionApplicationMapper, AdoptionApplication>
        implements AdoptionService {

    private static final Set<String> ACTIVE_STATUSES = Set.of(
            AdoptionApplicationStatus.PENDING.getCode(),
            AdoptionApplicationStatus.FIRST_APPROVED.getCode(),
            AdoptionApplicationStatus.INTERVIEWING.getCode(),
            AdoptionApplicationStatus.TRIAL.getCode()
    );

    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            AdoptionApplicationStatus.PENDING.getCode(), Set.of(
                    AdoptionApplicationStatus.FIRST_APPROVED.getCode(),
                    AdoptionApplicationStatus.REJECTED.getCode(),
                    AdoptionApplicationStatus.CANCELED.getCode()
            ),
            AdoptionApplicationStatus.FIRST_APPROVED.getCode(), Set.of(
                    AdoptionApplicationStatus.INTERVIEWING.getCode(),
                    AdoptionApplicationStatus.REJECTED.getCode(),
                    AdoptionApplicationStatus.CANCELED.getCode()
            ),
            AdoptionApplicationStatus.INTERVIEWING.getCode(), Set.of(
                    AdoptionApplicationStatus.TRIAL.getCode(),
                    AdoptionApplicationStatus.REJECTED.getCode()
            ),
            AdoptionApplicationStatus.TRIAL.getCode(), Set.of(
                    AdoptionApplicationStatus.SUCCESS.getCode(),
                    AdoptionApplicationStatus.TRIAL_FAILED.getCode()
            )
    );

    private final AnimalMapper animalMapper;
    private final AdoptionReviewLogService adoptionReviewLogService;
    private final SysUserService sysUserService;
    private final NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionApplicationVO apply(AdoptionApplyDTO applyDTO) {
        Animal animal = getExistingAnimal(applyDTO.getAnimalId());
        if (AnimalStatus.ADOPTED.getCode().equals(animal.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "动物已领养，不能再次申请");
        }
        if (!AnimalStatus.ADOPTABLE.getCode().equals(animal.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "动物不是可领养状态，不能申请");
        }
        Long applicantId = UserContext.currentUserId();
        long activeCount = count(new LambdaQueryWrapper<AdoptionApplication>()
                .eq(AdoptionApplication::getAnimalId, applyDTO.getAnimalId())
                .eq(AdoptionApplication::getApplicantId, applicantId)
                .in(AdoptionApplication::getStatus, ACTIVE_STATUSES));
        if (activeCount > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "不能重复申请同一只动物");
        }

        AdoptionApplication application = new AdoptionApplication();
        application.setAnimalId(applyDTO.getAnimalId());
        application.setApplicantId(applicantId);
        application.setApplicantName(applyDTO.getApplicantName());
        application.setPhone(applyDTO.getPhone());
        application.setAddress(applyDTO.getAddress());
        application.setReason(applyDTO.getReason());
        application.setExperience(applyDTO.getExperience());
        application.setStatus(AdoptionApplicationStatus.PENDING.getCode());
        save(application);

        animal.setStatus(AnimalStatus.APPLYING.getCode());
        animalMapper.updateById(animal);

        adoptionReviewLogService.record(application.getId(), applicantId, null,
                application.getStatus(), AdoptionReviewOperationType.SUBMIT.getCode(), "提交领养申请");
        return toVO(application);
    }

    @Override
    public PageResult<AdoptionApplicationVO> pageApplications(AdoptionQueryDTO queryDTO) {
        validateQuery(queryDTO);
        Page<AdoptionApplication> page = page(new Page<>(pageNum(queryDTO), pageSize(queryDTO)),
                baseQuery(queryDTO).orderByDesc(AdoptionApplication::getCreateTime));
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public PageResult<AdoptionApplicationVO> pageMine(AdoptionQueryDTO queryDTO) {
        validateQuery(queryDTO);
        Page<AdoptionApplication> page = page(new Page<>(pageNum(queryDTO), pageSize(queryDTO)),
                baseQuery(queryDTO)
                        .eq(AdoptionApplication::getApplicantId, UserContext.currentUserId())
                        .orderByDesc(AdoptionApplication::getCreateTime));
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public AdoptionApplicationDetailVO detail(Long id) {
        AdoptionApplication application = getExisting(id);
        ensureCanView(application);
        AdoptionApplicationDetailVO vo = toDetailVO(application);
        vo.setAnimal(toAnimalVO(getExistingAnimal(application.getAnimalId())));
        vo.setApplicant(sysUserService.getUserInfo(application.getApplicantId()));
        vo.setLogs(adoptionReviewLogService.listByApplicationId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionApplicationVO firstApprove(Long id, AdoptionReviewDTO reviewDTO) {
        AdoptionApplicationVO vo = changeStatus(id, AdoptionApplicationStatus.FIRST_APPROVED.getCode(),
                AdoptionReviewOperationType.FIRST_APPROVE.getCode(), remark(reviewDTO));
        notificationService.createNotification(vo.getApplicantId(), "领养申请初审通过",
                "你的领养申请已初审通过，请等待后续面谈安排。", NotificationType.ADOPTION.getCode());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionApplicationVO interview(Long id, AdoptionReviewDTO reviewDTO) {
        return changeStatus(id, AdoptionApplicationStatus.INTERVIEWING.getCode(),
                AdoptionReviewOperationType.INTERVIEW.getCode(), remark(reviewDTO));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionApplicationVO trial(Long id, AdoptionReviewDTO reviewDTO) {
        AdoptionApplicationVO vo = changeStatus(id, AdoptionApplicationStatus.TRIAL.getCode(),
                AdoptionReviewOperationType.TRIAL.getCode(), remark(reviewDTO));
        Animal animal = getExistingAnimal(vo.getAnimalId());
        if (!AnimalStatus.APPLYING.getCode().equals(animal.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "动物当前状态不能进入试养");
        }
        animal.setStatus(AnimalStatus.TRIAL.getCode());
        animalMapper.updateById(animal);
        notificationService.createNotification(vo.getApplicantId(), "领养申请进入试养",
                "你的领养申请已进入试养阶段。", NotificationType.ADOPTION.getCode());
        return toVO(getById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionApplicationVO success(Long id, AdoptionReviewDTO reviewDTO) {
        AdoptionApplication application = getExisting(id);
        validateTransition(application.getStatus(), AdoptionApplicationStatus.SUCCESS.getCode());
        Animal animal = getExistingAnimal(application.getAnimalId());
        if (!AnimalStatus.TRIAL.getCode().equals(animal.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "动物状态不是试养中，不能确认领养成功");
        }
        String oldStatus = application.getStatus();
        application.setStatus(AdoptionApplicationStatus.SUCCESS.getCode());
        application.setReviewRemark(remark(reviewDTO));
        updateById(application);

        animal.setStatus(AnimalStatus.ADOPTED.getCode());
        animalMapper.updateById(animal);

        List<AdoptionApplication> otherActive = list(new LambdaQueryWrapper<AdoptionApplication>()
                .eq(AdoptionApplication::getAnimalId, application.getAnimalId())
                .ne(AdoptionApplication::getId, application.getId())
                .in(AdoptionApplication::getStatus, ACTIVE_STATUSES));
        for (AdoptionApplication other : otherActive) {
            other.setStatus(AdoptionApplicationStatus.REJECTED.getCode());
            other.setReviewRemark("动物已完成领养，其他申请自动关闭");
            other.setUpdateTime(LocalDateTime.now());
            updateById(other);
        }

        adoptionReviewLogService.record(application.getId(), UserContext.currentUserId(), oldStatus,
                application.getStatus(), AdoptionReviewOperationType.SUCCESS.getCode(), remark(reviewDTO));
        notificationService.createNotification(application.getApplicantId(), "领养申请成功",
                "恭喜你，领养申请已成功。", NotificationType.ADOPTION.getCode());
        return toVO(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionApplicationVO reject(Long id, AdoptionReviewDTO reviewDTO) {
        String remark = remark(reviewDTO);
        if (StringUtils.isBlank(remark)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "拒绝申请时审核备注不能为空");
        }
        AdoptionApplicationVO vo = changeStatus(id, AdoptionApplicationStatus.REJECTED.getCode(),
                AdoptionReviewOperationType.REJECT.getCode(), remark);
        notificationService.createNotification(vo.getApplicantId(), "领养申请未通过",
                "你的领养申请未通过，原因见审核备注。", NotificationType.ADOPTION.getCode());
        restoreAnimalIfNoActiveApplication(vo.getAnimalId(), id);
        return toVO(getById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionApplicationVO trialFailed(Long id, AdoptionReviewDTO reviewDTO) {
        String remark = remark(reviewDTO);
        if (StringUtils.isBlank(remark)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "试养失败备注不能为空");
        }
        AdoptionApplicationVO vo = changeStatus(id, AdoptionApplicationStatus.TRIAL_FAILED.getCode(),
                AdoptionReviewOperationType.TRIAL_FAILED.getCode(), remark);
        Animal animal = getExistingAnimal(vo.getAnimalId());
        if (!AnimalStatus.TRIAL.getCode().equals(animal.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "动物状态不是试养中，不能标记试养失败");
        }
        animal.setStatus(AnimalStatus.ADOPTABLE.getCode());
        animalMapper.updateById(animal);
        return toVO(getById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdoptionApplicationVO cancel(Long id, AdoptionCancelDTO cancelDTO) {
        AdoptionApplication application = getExisting(id);
        ensureCanCancel(application);
        AdoptionApplicationVO vo = changeStatus(id, AdoptionApplicationStatus.CANCELED.getCode(),
                AdoptionReviewOperationType.CANCEL.getCode(), cancelDTO == null ? null : cancelDTO.getReason());
        restoreAnimalIfNoActiveApplication(vo.getAnimalId(), id);
        return toVO(getById(id));
    }

    @Override
    public List<AdoptionReviewLogVO> logs(Long id) {
        AdoptionApplication application = getExisting(id);
        ensureCanView(application);
        return adoptionReviewLogService.listByApplicationId(id);
    }

    private AdoptionApplicationVO changeStatus(Long id, String newStatus, String operationType, String remark) {
        AdoptionApplication application = getExisting(id);
        String oldStatus = application.getStatus();
        validateTransition(oldStatus, newStatus);
        application.setStatus(newStatus);
        application.setReviewRemark(remark);
        updateById(application);
        adoptionReviewLogService.record(application.getId(), UserContext.currentUserId(), oldStatus, newStatus, operationType, remark);
        return toVO(application);
    }

    private void validateTransition(String oldStatus, String newStatus) {
        if (!ALLOWED_TRANSITIONS.getOrDefault(oldStatus, Set.of()).contains(newStatus)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "领养申请状态流转非法：" + oldStatus + " -> " + newStatus);
        }
    }

    private void restoreAnimalIfNoActiveApplication(Long animalId, Long currentApplicationId) {
        Animal animal = getExistingAnimal(animalId);
        if (!AnimalStatus.APPLYING.getCode().equals(animal.getStatus())) {
            return;
        }
        long activeCount = count(new LambdaQueryWrapper<AdoptionApplication>()
                .eq(AdoptionApplication::getAnimalId, animalId)
                .ne(AdoptionApplication::getId, currentApplicationId)
                .in(AdoptionApplication::getStatus, ACTIVE_STATUSES));
        if (activeCount == 0) {
            animal.setStatus(AnimalStatus.ADOPTABLE.getCode());
            animalMapper.updateById(animal);
        }
    }

    private LambdaQueryWrapper<AdoptionApplication> baseQuery(AdoptionQueryDTO queryDTO) {
        return new LambdaQueryWrapper<AdoptionApplication>()
                .eq(StringUtils.isNotBlank(queryDTO.getStatus()), AdoptionApplication::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getAnimalId() != null, AdoptionApplication::getAnimalId, queryDTO.getAnimalId())
                .eq(queryDTO.getApplicantId() != null, AdoptionApplication::getApplicantId, queryDTO.getApplicantId())
                .and(StringUtils.isNotBlank(queryDTO.getKeyword()), w -> w
                        .like(AdoptionApplication::getApplicantName, queryDTO.getKeyword())
                        .or()
                        .like(AdoptionApplication::getPhone, queryDTO.getKeyword())
                        .or()
                        .like(AdoptionApplication::getReason, queryDTO.getKeyword()));
    }

    private void validateQuery(AdoptionQueryDTO queryDTO) {
        if (StringUtils.isNotBlank(queryDTO.getStatus()) && !AdoptionApplicationStatus.contains(queryDTO.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "领养申请状态不合法");
        }
    }

    private AdoptionApplication getExisting(Long id) {
        AdoptionApplication application = getById(id);
        if (application == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "领养申请不存在");
        }
        return application;
    }

    private Animal getExistingAnimal(Long animalId) {
        Animal animal = animalMapper.selectById(animalId);
        if (animal == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "动物档案不存在");
        }
        return animal;
    }

    private void ensureCanView(AdoptionApplication application) {
        SecurityUser user = UserContext.currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (application.getApplicantId().equals(user.getUserId())) {
            return;
        }
        throw new BusinessException(ErrorCode.FORBIDDEN, "当前用户不能查看他人的领养申请");
    }

    private void ensureCanCancel(AdoptionApplication application) {
        SecurityUser user = UserContext.currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (!application.getApplicantId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "当前用户不是申请人，不能取消");
        }
    }

    private boolean isAdmin(SecurityUser user) {
        return user.getRoles().contains("ADMIN") || user.getRoles().contains("SUPER_ADMIN");
    }

    private String remark(AdoptionReviewDTO reviewDTO) {
        return reviewDTO == null ? null : reviewDTO.getRemark();
    }

    private long pageNum(AdoptionQueryDTO queryDTO) {
        return queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
    }

    private long pageSize(AdoptionQueryDTO queryDTO) {
        return queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);
    }

    private AdoptionApplicationDetailVO toDetailVO(AdoptionApplication application) {
        AdoptionApplicationDetailVO vo = new AdoptionApplicationDetailVO();
        copy(application, vo);
        return vo;
    }

    private AdoptionApplicationVO toVO(AdoptionApplication application) {
        AdoptionApplicationVO vo = new AdoptionApplicationVO();
        copy(application, vo);
        return vo;
    }

    private void copy(AdoptionApplication application, AdoptionApplicationVO vo) {
        vo.setId(application.getId());
        vo.setAnimalId(application.getAnimalId());
        Animal animal = animalMapper.selectById(application.getAnimalId());
        if (animal != null) {
            vo.setAnimalName(animal.getName());
            vo.setAnimalNo(animal.getAnimalNo());
            vo.setAnimalStatus(animal.getStatus());
        }
        vo.setApplicantId(application.getApplicantId());
        vo.setApplicantName(application.getApplicantName());
        vo.setPhone(application.getPhone());
        vo.setAddress(application.getAddress());
        vo.setReason(application.getReason());
        vo.setExperience(application.getExperience());
        vo.setStatus(application.getStatus());
        vo.setReviewRemark(application.getReviewRemark());
        vo.setCreateTime(application.getCreateTime());
        vo.setUpdateTime(application.getUpdateTime());
    }

    private AnimalVO toAnimalVO(Animal animal) {
        AnimalVO vo = new AnimalVO();
        vo.setId(animal.getId());
        vo.setAnimalNo(animal.getAnimalNo());
        vo.setName(animal.getName());
        vo.setType(animal.getType());
        vo.setGender(animal.getGender());
        vo.setAgeStage(animal.getAgeStage());
        vo.setColor(animal.getColor());
        vo.setHealthStatus(animal.getHealthStatus());
        vo.setSterilizationStatus(animal.getSterilizationStatus());
        vo.setVaccineStatus(animal.getVaccineStatus());
        vo.setArea(animal.getArea());
        vo.setStatus(animal.getStatus());
        vo.setDescription(animal.getDescription());
        vo.setCoverImage(animal.getCoverImage());
        vo.setCreateTime(animal.getCreateTime());
        vo.setUpdateTime(animal.getUpdateTime());
        return vo;
    }
}
