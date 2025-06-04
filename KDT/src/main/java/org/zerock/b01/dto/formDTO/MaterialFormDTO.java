package org.zerock.b01.dto.formDTO;

import lombok.Getter;
import lombok.Setter;
import org.zerock.b01.dto.MaterialDTO;

import java.util.List;

@Getter
@Setter
public class MaterialFormDTO {
    private List<MaterialDTO> materials;
}
