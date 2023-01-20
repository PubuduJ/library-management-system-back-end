package lk.ijse.dep9.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueNote implements SuperEntity {
    private int id;
    private Date date;
    private String memberId;

    /* Since IssueNote id is auto incremented, put this parameterized constructor */
    public IssueNote(Date date, String memberId) {
        this.date = date;
        this.memberId = memberId;
    }
}
