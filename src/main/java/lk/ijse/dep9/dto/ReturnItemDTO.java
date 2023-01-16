package lk.ijse.dep9.dto;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonbPropertyOrder({"issueNoteId", "isbn"})
public class ReturnItemDTO implements Serializable {
    private Integer issueNoteId;
    private String isbn;
}
