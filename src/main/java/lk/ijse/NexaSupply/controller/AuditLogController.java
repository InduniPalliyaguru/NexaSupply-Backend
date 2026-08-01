package lk.ijse.NexaSupply.controller;

import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.AuditLogDTO;
import lk.ijse.NexaSupply.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public CommonResponse getAllAuditLogs() {
        List<AuditLogDTO> logs = auditLogService.getAllAuditLogs();
        return new CommonResponse(200, logs, "Audit logs fetched successfully!");
    }

}
