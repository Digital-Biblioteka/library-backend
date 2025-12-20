package nsu.library.dto.reader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class TocItemDTO {
    String htmlHref;
    String title;
    List<TocItemDTO> children;
}
