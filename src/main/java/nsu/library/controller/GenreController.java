package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.GenreDTO;
import nsu.library.entity.Genre;
import nsu.library.service.books.GenreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("genres")
    public List<Genre> getAllGenres() {
        return genreService.getGenres();
    }

    @GetMapping("{id}")
    public Genre getGenre(@PathVariable Long id) {
        return genreService.getGenre(id);
    }

    @PutMapping("admin/genres/{id}")
    public Genre updateGenre(@PathVariable Long id, @RequestBody GenreDTO dto) {
        return genreService.UpdateGenre(id, dto.getGenreName());
    }

    @PostMapping("admin/genres")
    public Genre addGenre(@RequestBody GenreDTO dto) {
        return genreService.AddGenre(dto.getGenreName());
    }

    @DeleteMapping("admin/genres{id}")
    public void deleteGenre(@PathVariable Long id) {
        genreService.DeleteGenre(id);
    }
}
