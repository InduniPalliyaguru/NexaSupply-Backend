package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.report.OrderInvoiceReportDTO;

public interface ReportService {

    byte[] generateOrderInvoicePdf(OrderInvoiceReportDTO reportDto) throws Exception;

}
