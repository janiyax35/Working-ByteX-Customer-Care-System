package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.ActivityLog;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Autowired
    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public void logActivity(String actionType, String entityType, Long entityId, String description, User user) {
        ActivityLog log = new ActivityLog();
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setUser(user);
        log.setCreatedAt(LocalDateTime.now());
        // In a real app, you would get the IP address from the request
        log.setIpAddress("127.0.0.1");
        activityLogRepository.save(log);
    }

    @Override
    public List<ActivityLog> getAllLogs() {
        return activityLogRepository.findAll();
    }
}