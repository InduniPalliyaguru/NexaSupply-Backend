package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.AuditLogDTO;
import lk.ijse.NexaSupply.entity.AuditLog;
import lk.ijse.NexaSupply.repository.AuditLogRepository;
import lk.ijse.NexaSupply.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void logAction(String userEmail, String action) {
        log.info("Execute Log Action");

        AuditLog auditLog = new AuditLog();
        auditLog.setUserEmail(userEmail);
        auditLog.setAction(action);
        auditLog.setActionDate(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLogDTO> getAllAuditLogs() {
        log.info("Execute Log All AuditLogs");

        List<AuditLog> all = auditLogRepository.findAllByOrderByActionDateDesc();
        List<AuditLogDTO> auditLogDTOS = new ArrayList<>();

        for (AuditLog auditLog : all) {

            AuditLogDTO auditLogDTO = new AuditLogDTO();
            auditLogDTO.setUserEmail(auditLog.getUserEmail());
            auditLogDTO.setAction(auditLog.getAction());
            auditLogDTO.setActionDate(auditLog.getActionDate());

            auditLogDTOS.add(auditLogDTO);
        }
        return auditLogDTOS;
    }
}
