package com.example.Notes.service;

import com.example.Notes.exception.ResourceNotFoundException;
import com.example.Notes.model.Note;
import com.example.Notes.repo.NoteRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {
    private final NoteRepo repo;

    public NoteService(NoteRepo repo) {
        this.repo = repo;
    }

    public Note create(Note note) {
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
        note.setPublic(!note.isPublic());
        note.setUpdatedAt(Instant.now());
        return repo.save(note);
    }
}

