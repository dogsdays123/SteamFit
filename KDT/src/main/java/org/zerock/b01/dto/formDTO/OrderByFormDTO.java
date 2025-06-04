package org.zerock.b01.dto.formDTO;

import lombok.Getter;
import lombok.Setter;
import org.zerock.b01.dto.OrderByDTO;

import java.util.List;

@Getter
@Setter
public class OrderByFormDTO {
    private List<OrderByDTO> orders;
}
