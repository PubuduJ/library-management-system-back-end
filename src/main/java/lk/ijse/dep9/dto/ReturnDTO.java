package lk.ijse.dep9.dto;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonbPropertyOrder({"memberId", "returnItems"})
public class ReturnDTO implements Serializable {
    private String memberId;
    private List<ReturnItemDTO> returnItems = new ArrayList<>();
}
