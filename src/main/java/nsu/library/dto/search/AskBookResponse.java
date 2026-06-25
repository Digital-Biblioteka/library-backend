package nsu.library.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AskBookResponse {
    private String answer;
    private List<Map<String, Object>> sources;
}
