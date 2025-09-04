package com.example.Notes.service;

import com.example.Notes.exception.ResourceNotFoundException;
import com.example.Notes.model.Note;
import com.example.Notes.repo.NoteRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NoteService {
    private final NoteRepo repo;

    public NoteService(NoteRepo repo) {
        this.repo = repo;
    }

    public Note create(Note note) {
        if (note.getSlug() == null || note.getSlug().isBlank()) {
            note.setSlug(generateUniqueSlug());
        }
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        return repo.save(note);
    }

    public List<Note> listAll() {
        return repo.findAll();
    }

    public Note getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Note not found: " + id));
    }

    public Note update(Long id, Note partial) {
        Note existing = getById(id);
        if (partial.getTitle() != null) existing.setTitle(partial.getTitle());
        if (partial.getContent() != null) existing.setContent(partial.getContent());
        existing.setUpdatedAt(Instant.now());
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Note not found: " + id);
        repo.deleteById(id);
    }

    public Note toggleShare(Long id) {
        Note note = getById(id);
        if (!note.isPublic()) {
            note.setPublic(true);
            if (note.getSlug() == null || note.getSlug().isBlank()) {
                note.setSlug(generateUniqueSlug());
            }
        } else {
            note.setPublic(false);
            note.setSlug(null);
        }
        note.setUpdatedAt(Instant.now());
        return repo.save(note);
    }

    public Optional<Note> findBySlug(String slug) {
        return repo.findBySlug(slug);
    }

    private String generateUniqueSlug() {
        String s = randomSlug();
        while (repo.findBySlug(s).isPresent()) {
            s = randomSlug();
        }
        return s;
    }

    private String randomSlug() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
