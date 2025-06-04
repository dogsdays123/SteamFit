package org.zerock.b01.dto.formDTO;
import lombok.Getter;
import lombok.Setter;
import org.zerock.b01.dto.UserByDTO;

import java.util.List;

@Getter
@Setter
public class EmployeeApprovalFormDTO {
    private List<UserByDTO> users;
}
