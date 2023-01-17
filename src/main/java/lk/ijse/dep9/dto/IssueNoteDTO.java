package lk.ijse.dep9.dto;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonbPropertyOrder({"id", "date", "memberId", "books"})
public class IssueNoteDTO implements Serializable {
    private Integer id;
    private LocalDate date;
    private String memberId;
    private ArrayList<String> books = new ArrayList<>();
}
