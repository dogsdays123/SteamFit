package org.zerock.b01.dto.formDTO;

import lombok.Getter;
import lombok.Setter;
import org.zerock.b01.dto.DeliveryProcurementPlanDTO;

import java.util.List;

@Getter
@Setter
public class DppFormDTO {
    private List<DeliveryProcurementPlanDTO> dpps;
}
