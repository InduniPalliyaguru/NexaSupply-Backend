package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.OrderInvoiceReportDTO;

public interface ReportService {

    byte[] generateOrderInvoicePdf(OrderInvoiceReportDTO reportDto) throws Exception;

}
