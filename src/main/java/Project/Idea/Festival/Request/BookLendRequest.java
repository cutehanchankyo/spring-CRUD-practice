package Project.Idea.Festival.Request;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class BookLendRequest {
    private List<Long> bookIds;
    private Long memberId;
}
