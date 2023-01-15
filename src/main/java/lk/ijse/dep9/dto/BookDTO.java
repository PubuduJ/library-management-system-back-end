package lk.ijse.dep9.dto;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonbPropertyOrder({"isbn", "title", "author", "copies"})
public class BookDTO implements Serializable {
    private String isbn;
    private String title;
    private String author;
    private Integer copies;
}
