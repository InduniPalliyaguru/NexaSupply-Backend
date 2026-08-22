package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.payment.CreditLedgerDTO;

import java.util.List;

public interface CreditLedgerService {

    void recordLedger(CreditLedgerDTO dto);

    List<CreditLedgerDTO> getLedgerByUserCode(String userCode);

    List<CreditLedgerDTO> getAllLedger();

    List<CreditLedgerDTO> getLedgersByReferenceCode(String referenceCode);

}
