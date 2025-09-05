import { useEffect, useState } from "react";
import api from "./api";

function App() {
  const [notes, setNotes] = useState([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [editingNote, setEditingNote] = useState(null);

  // Fetch all notes
  useEffect(() => {
    fetchNotes();
  }, []);

  const fetchNotes = () => {
    api.get("/api/notes")
      .then((res) => setNotes(res.data))
      .catch((err) => console.error("Error fetching notes:", err));
  };

  // Create note
  const handleCreateNote = (e) => {
    e.preventDefault();
    const newNote = { title, content };

    api.post("/api/notes", newNote)
      .then(() => {
        setTitle("");
        setContent("");
        fetchNotes();
      })
      .catch((err) => console.error("Error creating note:", err));
  };

  // Start editing
  const startEdit = (note) => {
    setEditingNote(note);
    setTitle(note.title);
    setContent(note.content);
  };

  // Update note
  const handleUpdateNote = (e) => {
    e.preventDefault();
    if (!editingNote) return;

    api.put(`/api/notes/${editingNote.id}`, { title, content })
      .then(() => {
        setEditingNote(null);
        setTitle("");
        setContent("");
        fetchNotes();
      })
      .catch((err) => console.error("Error updating note:", err));
  };

  // Delete note
  const handleDeleteNote = (id) => {
    if (!window.confirm("Are you sure you want to delete this note?")) return;

    api.delete(`/api/notes/${id}`)
      .then(() => {
        fetchNotes();
      })
      .catch((err) => console.error("Error deleting note:", err));
  };

  // Toggle share
  const handleToggleShare = (id) => {
    api.post(`/api/notes/${id}/share`)
      .then((res) => {
        const note = res.data;
        if (note.public) {
          const shareUrl = `${window.location.origin}/share/${note.slug}`;
          navigator.clipboard.writeText(shareUrl);
          alert("Share link copied: " + shareUrl);
        } else {
          alert("Sharing disabled for this note.");
        }
        fetchNotes(); // refresh UI
      })
      .catch((err) => console.error("Error toggling share:", err));
  };

  return (
    <div style={{ padding: "20px" }}>
      <h1>Notes</h1>

      {/* Form - Create / Edit */}
      <form onSubmit={editingNote ? handleUpdateNote : handleCreateNote} style={{ marginBottom: "20px" }}>
        <input
          type="text"
          placeholder="Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <textarea
          placeholder="Content"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
        />
        <button type="submit">
          {editingNote ? "Update Note" : "Add Note"}
        </button>
        {editingNote && (
          <button
            type="button"
            onClick={() => {
              setEditingNote(null);
              setTitle("");
              setContent("");
            }}
          >
            Cancel
          </button>
        )}
      </form>

      {/* List of notes */}
      <ul>
        {notes.map((note) => (
          <li key={note.id}>
            <strong>{note.title}</strong>: {note.content}
            <button onClick={() => startEdit(note)} style={{ marginLeft: "10px" }}>
              Edit
            </button>
            <button onClick={() => handleDeleteNote(note.id)} style={{ marginLeft: "10px", color: "red" }}>
              Delete
            </button>
            <button
              onClick={() => handleToggleShare(note.id)}
              style={{ marginLeft: "10px", color: "blue" }}
            >
              {note.public ? "Unshare" : "Share"}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
