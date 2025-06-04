package org.zerock.b01.service;

import org.zerock.b01.dto.InputDTO;
import org.zerock.b01.dto.OutPutDTO;

import java.util.List;

public interface OutputService {

    void registerOutput(OutPutDTO outPutDTO);
    List<OutPutDTO> getOutputs();
    void removeOutput(List<String> opIds);
    void confirmOutput(List<String> opIds);
}
