package nsu.library.dto;

public record SearchQuery(
        String query,
        Integer from,
        Integer size,
        String mode,
        Integer k
) {
    public int fromOrDefault() { return from == null ? 0 : from; }
    public int sizeOrDefault() { return size == null ? 10 : size; }
    public String modeOrDefault() { return (mode == null || mode.isBlank()) ? "bm25" : mode.toLowerCase(); }
    public int kOrDefault() { return k == null ? 5 : k; }
}
