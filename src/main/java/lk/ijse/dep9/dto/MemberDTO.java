package lk.ijse.dep9.dto;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonbPropertyOrder({"id", "name", "address", "contact"})
public class MemberDTO implements Serializable {
    private String id;
    private String name;
    private String address;
    private String contact;
}
