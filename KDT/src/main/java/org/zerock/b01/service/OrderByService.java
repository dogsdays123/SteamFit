package org.zerock.b01.service;

import org.zerock.b01.dto.OrderByDTO;

import java.util.List;
import java.util.Map;

public interface OrderByService {
    void orderByRegister(OrderByDTO orderByDTO, String uId);
    Map<String, Double> getMonthlyOrderSummary();
    void setOrderReady(OrderByDTO orderByDTO,  List<String> oCodes);
}
