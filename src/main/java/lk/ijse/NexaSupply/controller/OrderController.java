package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.report.OrderInvoiceReportDTO;
import lk.ijse.NexaSupply.dto.order.OrderRequestDTO;
import lk.ijse.NexaSupply.dto.order.OrderResponseDTO;
import lk.ijse.NexaSupply.enumeration.OrderStatus;
import lk.ijse.NexaSupply.service.OrderService;
import lk.ijse.NexaSupply.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ReportService reportService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_RETAILER')")
    public CommonResponse placeOrder(@Valid @RequestBody OrderRequestDTO requestDTO) {
        OrderResponseDTO response = orderService.placeOrder(requestDTO);
        return new CommonResponse(201, response, "Order Placed Successfully!");
    }

    @PutMapping(value = "/{orderCode}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse updateOrderStatus(@PathVariable String orderCode, @RequestParam OrderStatus status) {
        OrderResponseDTO response = orderService.updateOrderStatus(orderCode, status);
        return new CommonResponse(200, response, "Order Status Updated Successfully!");
    }

    @GetMapping(value = "myOrders", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_RETAILER')")
    public CommonResponse getMyOrders() {
        List<OrderResponseDTO> orders = orderService.getMyOrders();
        return new CommonResponse(200, orders, "My Orders fetched Successfully!");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return new CommonResponse(200, orders, "All orders fetched successfully!");
    }

    @GetMapping(value = "/{orderCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getOrderByCode(@PathVariable String orderCode) {
        OrderResponseDTO order = orderService.getOrderByCode(orderCode);
        return new CommonResponse(200, order, "Order fetched successfully!");
    }

    @GetMapping(value = "/{orderCode}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RETAILER')")
    public ResponseEntity<byte[]> downloadOrderPdf(@PathVariable String orderCode) throws Exception {

        OrderInvoiceReportDTO reportData = orderService.getOrderInvoiceReportData(orderCode);
        byte[] pdfBytes = reportService.generateOrderInvoicePdf(reportData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Invoice_" + orderCode + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}
