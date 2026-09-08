package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.payment.CreditLedgerDTO;
import lk.ijse.NexaSupply.entity.CreditLedger;
import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.CreditLedgerRepository;
import lk.ijse.NexaSupply.repository.UserRepository;
import lk.ijse.NexaSupply.service.CreditLedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CreditLedgerServiceImpl implements CreditLedgerService {

    private final CreditLedgerRepository creditLedgerRepository;
    private final UserRepository userRepository;

    @Override
    public void recordLedger(CreditLedgerDTO dto) {
        log.info("Execute recordLedger method");

        Optional<User> optionalUser = userRepository.findByUserCodeAndDataStatus(dto.getUserCode(), DataStatus.ACTIVE);
        if (optionalUser.isEmpty()) {
            log.error("User not found");
            throw new CustomException(404, "Customer Not Found with code " + dto.getUserCode());
        }
        User customer = optionalUser.get();

        CreditLedger creditLedger = new CreditLedger();
        creditLedger.setLedgerCode(generateLedgerCode());
        creditLedger.setReferenceCode(dto.getReferenceCode());
        creditLedger.setAmount(dto.getAmount());
        creditLedger.setBalanceAfter(dto.getBalanceAfter());
        creditLedger.setLedgerType(dto.getLedgerType());
        creditLedger.setDescription(dto.getDescription());
        creditLedger.setTransactionDate(LocalDateTime.now());
        creditLedger.setCustomer(customer);

        creditLedgerRepository.save(creditLedger);
    }

    @Override
    public List<CreditLedgerDTO> getLedgerByUserCode(String userCode) {
        log.info("Execute getLedgerByUserCode method");

        List<CreditLedger> ledgerList = creditLedgerRepository.findByCustomer_UserCode(userCode);
        List<CreditLedgerDTO> creditLedgerDTOList = new ArrayList<>();

        for (CreditLedger creditLedger : ledgerList) {
            CreditLedgerDTO dto = mapToDTO(creditLedger);
            creditLedgerDTOList.add(dto);
        }
        return creditLedgerDTOList;
    }

    @Override
    public List<CreditLedgerDTO> getAllLedger() {
        log.info("Execute getAllLedger method");

        List<CreditLedger> all = creditLedgerRepository.findAll();
        List<CreditLedgerDTO> creditLedgerDTOList = new ArrayList<>();

        for (CreditLedger creditLedger : all) {
            CreditLedgerDTO dto = mapToDTO(creditLedger);
            creditLedgerDTOList.add(dto);
        }
        return creditLedgerDTOList;
    }

    @Override
    public List<CreditLedgerDTO> getLedgersByReferenceCode(String referenceCode) {
        log.info("Execute getLedgersByReferenceCode method");

        List<CreditLedger> ledgerList = creditLedgerRepository.findByReferenceCode(referenceCode);
        List<CreditLedgerDTO> creditLedgerDTOList = new ArrayList<>();

        for (CreditLedger creditLedger : ledgerList) {
            CreditLedgerDTO dto = mapToDTO(creditLedger);
            creditLedgerDTOList.add(dto);
        }
        return creditLedgerDTOList;
    }

    private String generateLedgerCode() {
        int year = Year.now().getValue();
        long count = creditLedgerRepository.countAllLedgers() + 1;
        return String.format("LED-%d-%04d", year, count);
    }

    private CreditLedgerDTO mapToDTO(CreditLedger ledger) {

        CreditLedgerDTO dto = new CreditLedgerDTO();
        dto.setLedgerId(ledger.getLedgerId());
        dto.setLedgerCode(ledger.getLedgerCode());
        dto.setReferenceCode(ledger.getReferenceCode());
        dto.setAmount(ledger.getAmount());
        dto.setBalanceAfter(ledger.getBalanceAfter());
        dto.setLedgerType(ledger.getLedgerType());
        dto.setDescription(ledger.getDescription());
        dto.setTransactionDate(ledger.getTransactionDate());
        if (ledger.getCustomer() != null) {
            dto.setUserCode(ledger.getCustomer().getUserCode());
            dto.setCustomerName(ledger.getCustomer().getFullName());
        }
        return dto;
    }

}
