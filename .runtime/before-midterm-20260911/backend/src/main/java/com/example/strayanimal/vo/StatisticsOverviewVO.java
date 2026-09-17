package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsOverviewVO {
    private Long animalCount;
    private Long adoptableAnimalCount;
    private Long adoptedAnimalCount;
    private Long rescueOrderCount;
    private Long pendingRescueOrderCount;
    private Long pendingClueCount;
    private Long waitAssignOrderCount;
    private Long processingOrderCount;
    private Long volunteerTaskCount;
    private Long pendingReviewTaskCount;
    private Long waitClaimTaskCount;
    private Long adoptionApplicationCount;
    private Long pendingAdoptionCount;
    private Long unreadNotificationCount;
    private Long todayNewClueCount;
    private Long todayNewOrderCount;
}
