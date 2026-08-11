package lk.ijse.NexaSupply.controller;

import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.CreditLedgerDTO;
import lk.ijse.NexaSupply.service.CreditLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/credit-ledgers")
@RequiredArgsConstructor
public class CreditLedgerController {

    private final CreditLedgerService creditLedgerService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getAllLedger() {
        List<CreditLedgerDTO> ledgers = creditLedgerService.getAllLedger();
        return new CommonResponse(200, ledgers, "Credit ledger list retrieved successfully!");
    }

    @GetMapping(value = "/user/{userCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getLedgerByUserCode(@PathVariable String userCode) {
        List<CreditLedgerDTO> ledgers = creditLedgerService.getLedgerByUserCode(userCode);
        return new CommonResponse(200, ledgers, "Credit ledger list for user retrieved successfully!");
    }

    @GetMapping(value = "/reference/{referenceCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getLedgerByReferenceCode(@PathVariable String referenceCode) {
        List<CreditLedgerDTO> ledgers = creditLedgerService.getLedgersByReferenceCode(referenceCode);
        return new CommonResponse(200, ledgers, "Credit ledger list for reference code retrieved successfully!");
    }

}
