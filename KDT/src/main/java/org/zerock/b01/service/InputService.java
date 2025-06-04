package org.zerock.b01.service;

import org.zerock.b01.domain.DeliveryRequest;
import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.DeliveryRequestDTO;
import org.zerock.b01.dto.InputDTO;

import java.util.List;

public interface InputService {

    List<DeliveryRequestDTO> getDeliveryRequest();
    void registerInput(InputDTO inputDTO);
    List<InputDTO> getInputs();
    void removeInput(List<String> ipIds);
}
