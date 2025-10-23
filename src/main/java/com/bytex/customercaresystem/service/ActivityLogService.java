package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.ActivityLog;
import com.bytex.customercaresystem.model.User;

import java.util.List;

public interface ActivityLogService {
    void logActivity(String actionType, String entityType, Long entityId, String description, User user);
    List<ActivityLog> getAllLogs();
}