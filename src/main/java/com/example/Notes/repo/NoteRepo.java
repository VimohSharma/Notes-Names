package com.example.Notes.repo;

import com.example.Notes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoteRepo extends JpaRepository<Note,Long> {
    
}

