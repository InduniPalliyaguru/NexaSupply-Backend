package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.restock.RestockRequestDTO;
import lk.ijse.NexaSupply.dto.restock.RestockResponseDTO;
import lk.ijse.NexaSupply.service.RestockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/restocks")
@RequiredArgsConstructor
public class RestockController {

    private final RestockService restockService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse addRestock(@Valid @RequestBody RestockRequestDTO dto) {
        RestockResponseDTO response = restockService.addRestock(dto);
        return new CommonResponse(201, response, "Restock added successfully!");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getAllRestockHistory() {
        List<RestockResponseDTO> response = restockService.getAllRestockHistory();
        return new CommonResponse(200, response, "Restock History retrieved successfully!");
    }

    @GetMapping(value = "/{restockCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getStockByCode(@PathVariable String restockCode) {
        RestockResponseDTO response = restockService.getRestockByCode(restockCode);
        return new CommonResponse(200, response, "Restock retrieved successfully!");
    }

}