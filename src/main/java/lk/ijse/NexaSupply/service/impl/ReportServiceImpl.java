package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.OrderInvoiceReportDTO;
import lk.ijse.NexaSupply.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Override
    public byte[] generateOrderInvoicePdf(OrderInvoiceReportDTO reportDto) throws Exception {
        log.info("Executing generateOrderInvoicePdf");

        InputStream inputStream = new ClassPathResource("reports/order_invoice.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

        String logoPath = new ClassPathResource("static/images/logo.png").getURL().toString();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("logoPath", logoPath);
        parameters.put("orderCode", reportDto.getOrderCode());
        parameters.put("orderDate", reportDto.getOrderDate());
        parameters.put("orderStatus", reportDto.getOrderStatus());
        parameters.put("customerName", reportDto.getCustomerName());
        parameters.put("shopName", reportDto.getShopName());
        parameters.put("customerEmail", reportDto.getCustomerEmail());
        parameters.put("customerPhone", reportDto.getCustomerPhone());
        parameters.put("payCode", reportDto.getPayCode());
        parameters.put("paymentStatus", reportDto.getPaymentStatus());
        parameters.put("grandTotal", reportDto.getGrandTotal());
        parameters.put("paidAmount", reportDto.getPaidAmount());
        parameters.put("balanceAmount", reportDto.getBalanceAmount());

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportDto.getItems());

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);

    }

}
