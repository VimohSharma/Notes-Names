import { useEffect, useState } from "react";
import api from "./api";
import "./index.css";

function App() {
  const [notes, setNotes] = useState([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [editingNote, setEditingNote] = useState(null);

  // Fetch all notes
  const fetchNotes = () => {
    api.get("/api/notes")
      .then((res) => setNotes(res.data))
      .catch((err) => console.error("Error fetching notes:", err));
  };

  useEffect(() => {
    fetchNotes();
  }, []);

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
      .then(() => fetchNotes())
      .catch((err) => console.error("Error deleting note:", err));
  };

  // 🔥 Toggle Share
  const handleToggleShare = (id) => {
    api.post(`/api/notes/${id}/share`)
      .then((res) => {
        const note = res.data;
        if (note.public) {
          const shareUrl = `${window.location.origin}/share/${note.id}`;
          navigator.clipboard.writeText(shareUrl);
          alert("Share link copied: " + shareUrl);
        } else {
          alert("Sharing disabled for this note.");
        }
        fetchNotes();
      })
      .catch((err) => console.error("Error toggling share:", err));
  };

  return (
    <div className="dashboard">
      {/* Sidebar */}
      <aside className="sidebar">
        <h2>Folders</h2>
        <div className="folder">Work</div>
        <div className="folder">Personal</div>
        <div className="folder">Ideas</div>
        <button className="add-folder">+ Add Folder</button>
      </aside>

      {/* Main Section */}
      <div className="main">
        {/* Top Bar */}
        <header className="topbar">
          <h1>Notes App</h1>
          <div className="auth-buttons">
            <button className="btn">Login</button>
            <button className="btn">Signup</button>
          </div>
        </header>

        {/* Form - Create / Edit */}
        <form
          onSubmit={editingNote ? handleUpdateNote : handleCreateNote}
          className="note-form"
        >
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
          <div>
            <button type="submit" className="btn primary">
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
                className="btn"
              >
                Cancel
              </button>
            )}
          </div>
        </form>

        {/* Notes Grid */}
        <section className="notes-grid">
          {notes.map((note) => (
            <div key={note.id} className="note-card">
              <h3>{note.title}</h3>
              <p>{note.content}</p>
              <div className="note-actions">
                <button className="btn small" onClick={() => startEdit(note)}>
                  Edit
                </button>
                <button
                  className="btn small danger"
                  onClick={() => handleDeleteNote(note.id)}
                >
                  Delete
                </button>
                <button
                  className="btn small"
                  onClick={() => handleToggleShare(note.id)}
                >
                  {note.public ? "Unshare" : "Share"}
                </button>
              </div>
            </div>
          ))}
        </section>
      </div>
    </div>
  );
}

export default App;
