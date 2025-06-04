package org.zerock.b01.service;

import org.zerock.b01.dto.DeliveryRequestDTO;
import org.zerock.b01.dto.InputDTO;
import org.zerock.b01.dto.ProgressInspectionDTO;

import java.util.List;

public interface DeliveryRequestService {

    void registerDeliveryRequest(DeliveryRequestDTO deliveryRequestDTO);
    void drAgree(DeliveryRequestDTO deliveryRequestDTO, List<String> drCodes);
    void drRemove(List<String> drCodes);
}
