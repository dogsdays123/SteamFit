package org.zerock.b01.dto.formDTO;

import lombok.Getter;
import lombok.Setter;
import org.zerock.b01.dto.BomDTO;

import java.util.List;

@Getter
@Setter
public class BomFormDTO {
    private List<BomDTO> boms;
}
