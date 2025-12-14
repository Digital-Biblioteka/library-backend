package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.siegmann.epublib.domain.Resource;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class TocItemDTO {
    Resource resource;
    String title;
    List<TocItemDTO> children;
}
