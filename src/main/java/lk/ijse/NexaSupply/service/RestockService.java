package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.restock.RestockRequestDTO;
import lk.ijse.NexaSupply.dto.restock.RestockResponseDTO;

import java.util.List;

public interface RestockService {

    RestockResponseDTO addRestock(RestockRequestDTO dto);

    List<RestockResponseDTO> getAllRestockHistory();

    RestockResponseDTO getRestockByCode(String restockCode);

}
