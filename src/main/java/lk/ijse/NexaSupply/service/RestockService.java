package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.RestockRequestDTO;
import lk.ijse.NexaSupply.dto.RestockResponseDTO;

import java.util.List;

public interface RestockService {

    RestockResponseDTO addRestock(RestockRequestDTO dto);

    List<RestockResponseDTO> getAllRestockHistory();

    RestockResponseDTO getRestockByCode(String restockCode);

}
