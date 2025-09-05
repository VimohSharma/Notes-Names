package com.example.Notes.controller;

import com.example.Notes.model.Note;
import com.example.Notes.repo.NoteRepo;
import com.example.Notes.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {
    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Note> create(@RequestBody Note payload) {
        Note created = service.create(payload);
        return ResponseEntity.created(URI.create("/api/notes/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Note> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> update(@PathVariable Long id, @RequestBody Note payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(java.util.Collections.singletonMap("ok", true));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Note> toggleShare(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleShare(id));
    }

    @GetMapping("/share/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable String slug) {
        return service.findBySlug(slug)
                .map(n -> ResponseEntity.ok(java.util.Map.of(
                        "title", n.getTitle(),
                        "content", n.getContent(),
                        "updatedAt", n.getUpdatedAt()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/notes/public/{id}")
    public ResponseEntity<Note> getPublicNoteById(@PathVariable Long id) {
        return noteRepository.findById(id)
            .filter(Note::isPublic) // only returning if it's shared
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
}
}

