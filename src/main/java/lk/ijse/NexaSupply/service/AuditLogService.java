package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.system.AuditLogDTO;

import java.util.List;

public interface AuditLogService {

    void logAction(String userEmail, String action);

    List<AuditLogDTO> getAllAuditLogs();

}
