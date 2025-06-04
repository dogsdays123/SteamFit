package org.zerock.b01.dto.formDTO;

import lombok.Getter;
import lombok.Setter;
import org.zerock.b01.dto.allDTO.OrderByPdfDTO;

import java.util.List;

@Getter
@Setter
public class OrderByPdfFormDTO {
    private List<OrderByPdfDTO> pdfs;
}
