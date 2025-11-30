package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.entity.Genre;
import nsu.library.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public List<Genre> getGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenre(Long id) {
        return genreRepository.findById(id).orElseThrow();
    }

    public Genre AddGenre(String genreName) {
        Genre genre = new Genre();
        genre.setGenreName(genreName);
        return genreRepository.save(genre);
    }

    public Genre UpdateGenre(Long id, String genreName) {
        Genre genre = genreRepository.findById(id).orElseThrow();
        genre.setGenreName(genreName);
        return genreRepository.save(genre);
    }

    public void DeleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
}
