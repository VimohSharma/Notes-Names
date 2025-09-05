package com.example.Notes.controller;

import com.example.Notes.model.Note;
import com.example.Notes.repo.NoteRepo;
import com.example.Notes.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {
    private final NoteService service;
    private final NoteRepo noteRepo;
    public NoteController(NoteService service, NoteRepo noteRepo) {
        this.service = service;
        this.noteRepo = noteRepo;
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
    public ResponseEntity<?> toggleShare(@PathVariable Long id) {
        Note note = service.toggleShare(id);

        if (note.isPublic()) {
            String shareUrl = "https://notes-names-production.up.railway.app/api/notes/public/" + note.getId();
            return ResponseEntity.ok(Map.of(
                "message", "Note shared",
                "shareUrl", shareUrl
            ));
        } else {
            return ResponseEntity.ok(Map.of("message", "Note unshared"));
        }
    }
     @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicNoteById(@PathVariable Long id) {
        return noteRepo.findById(id)
                .filter(Note::isPublic)
                .map(note -> ResponseEntity.ok(Map.of(
                        "title", note.getTitle(),
                        "content", note.getContent(),
                        "updatedAt", note.getUpdatedAt()
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}



