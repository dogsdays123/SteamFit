package org.zerock.b01.service;

import org.zerock.b01.dto.ReturnByDTO;

import java.util.List;

public interface ReturnService {

    void returnInput(ReturnByDTO returnByDTO);

    void reDelivery(ReturnByDTO returnByDTO);

    void removeReturn(List<Long> rIds);
}
