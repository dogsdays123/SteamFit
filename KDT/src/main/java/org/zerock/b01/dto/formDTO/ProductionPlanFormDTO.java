package org.zerock.b01.dto.formDTO;

import lombok.Getter;
import lombok.Setter;
import org.zerock.b01.dto.ProductionPlanDTO;

import java.util.List;

@Getter
@Setter
public class ProductionPlanFormDTO {
    private List<ProductionPlanDTO> plans;
}
