package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.strayanimal.entity.AdoptionApplication;
import com.example.strayanimal.entity.Animal;
import com.example.strayanimal.entity.RescueClue;
import com.example.strayanimal.entity.RescueOrder;
import com.example.strayanimal.entity.VolunteerTask;
import com.example.strayanimal.entity.Notification;
import com.example.strayanimal.enums.AdoptionApplicationStatus;
import com.example.strayanimal.enums.AnimalStatus;
import com.example.strayanimal.enums.RescueClueStatus;
import com.example.strayanimal.enums.RescueOrderStatus;
import com.example.strayanimal.enums.VolunteerTaskStatus;
import com.example.strayanimal.mapper.AdoptionApplicationMapper;
import com.example.strayanimal.mapper.AnimalMapper;
import com.example.strayanimal.mapper.RescueClueMapper;
import com.example.strayanimal.mapper.RescueOrderMapper;
import com.example.strayanimal.mapper.VolunteerTaskMapper;
import com.example.strayanimal.mapper.NotificationMapper;
import com.example.strayanimal.security.UserContext;
import com.example.strayanimal.service.StatisticsService;
import com.example.strayanimal.vo.StatisticsOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private static final String OVERVIEW_KEY_PREFIX = "statistics:overview:";

    private final AnimalMapper animalMapper;
    private final RescueClueMapper rescueClueMapper;
    private final RescueOrderMapper rescueOrderMapper;
    private final VolunteerTaskMapper volunteerTaskMapper;
    private final AdoptionApplicationMapper adoptionApplicationMapper;
    private final NotificationMapper notificationMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public StatisticsOverviewVO overview() {
        String cacheKey = OVERVIEW_KEY_PREFIX + UserContext.currentUserId();
        try {
            String cached = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, StatisticsOverviewVO.class);
            }
        } catch (Exception ignored) {
            // Redis 不可用时降级为直接查询数据库。
        }
        StatisticsOverviewVO overview = queryOverview();
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(overview), 5, TimeUnit.MINUTES);
        } catch (JsonProcessingException ignored) {
        } catch (Exception ignored) {
            // Redis 写入失败不影响接口返回。
        }
        return overview;
    }

    @Override
    public void evictOverviewCache() {
        try {
            stringRedisTemplate.delete(OVERVIEW_KEY_PREFIX + UserContext.currentUserId());
        } catch (Exception ignored) {
        }
    }

    private StatisticsOverviewVO queryOverview() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        StatisticsOverviewVO vo = new StatisticsOverviewVO();
        vo.setAnimalCount(animalMapper.selectCount(new LambdaQueryWrapper<Animal>()));
        vo.setAdoptableAnimalCount(animalMapper.selectCount(new LambdaQueryWrapper<Animal>()
                .eq(Animal::getStatus, AnimalStatus.ADOPTABLE.getCode())));
        vo.setAdoptedAnimalCount(animalMapper.selectCount(new LambdaQueryWrapper<Animal>()
                .eq(Animal::getStatus, AnimalStatus.ADOPTED.getCode())));
        vo.setRescueOrderCount(rescueOrderMapper.selectCount(new LambdaQueryWrapper<RescueOrder>()));
        vo.setPendingRescueOrderCount(rescueOrderMapper.selectCount(new LambdaQueryWrapper<RescueOrder>()
                .in(RescueOrder::getStatus,
                        RescueOrderStatus.WAIT_ASSIGN.getCode(),
                        RescueOrderStatus.ASSIGNED.getCode(),
                        RescueOrderStatus.PROCESSING.getCode())));
        vo.setPendingClueCount(rescueClueMapper.selectCount(new LambdaQueryWrapper<RescueClue>()
                .eq(RescueClue::getStatus, RescueClueStatus.PENDING.getCode())));
        vo.setWaitAssignOrderCount(rescueOrderMapper.selectCount(new LambdaQueryWrapper<RescueOrder>()
                .eq(RescueOrder::getStatus, RescueOrderStatus.WAIT_ASSIGN.getCode())));
        vo.setProcessingOrderCount(rescueOrderMapper.selectCount(new LambdaQueryWrapper<RescueOrder>()
                .eq(RescueOrder::getStatus, RescueOrderStatus.PROCESSING.getCode())));
        vo.setVolunteerTaskCount(volunteerTaskMapper.selectCount(new LambdaQueryWrapper<VolunteerTask>()));
        vo.setPendingReviewTaskCount(volunteerTaskMapper.selectCount(new LambdaQueryWrapper<VolunteerTask>()
                .eq(VolunteerTask::getStatus, VolunteerTaskStatus.FINISHED.getCode())));
        vo.setWaitClaimTaskCount(volunteerTaskMapper.selectCount(new LambdaQueryWrapper<VolunteerTask>()
                .eq(VolunteerTask::getStatus, VolunteerTaskStatus.WAIT_CLAIM.getCode())));
        vo.setAdoptionApplicationCount(adoptionApplicationMapper.selectCount(new LambdaQueryWrapper<AdoptionApplication>()));
        vo.setPendingAdoptionCount(adoptionApplicationMapper.selectCount(new LambdaQueryWrapper<AdoptionApplication>()
                .eq(AdoptionApplication::getStatus, AdoptionApplicationStatus.PENDING.getCode())));
        vo.setUnreadNotificationCount(notificationMapper.selectCount(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, UserContext.currentUserId())
                .eq(Notification::getIsRead, 0)));
        vo.setTodayNewClueCount(rescueClueMapper.selectCount(new LambdaQueryWrapper<RescueClue>()
                .ge(RescueClue::getCreateTime, startOfDay)));
        vo.setTodayNewOrderCount(rescueOrderMapper.selectCount(new LambdaQueryWrapper<RescueOrder>()
                .ge(RescueOrder::getCreateTime, startOfDay)));
        return vo;
    }
}
