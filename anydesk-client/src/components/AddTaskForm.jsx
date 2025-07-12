import { useState } from 'react';

function AddTaskForm({ onTaskAdded }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const res = await fetch('/api/tasks', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, description }),
      });
      if (!res.ok) throw new Error('Failed to add task');
      setTitle('');
      setDescription('');
      if (onTaskAdded) onTaskAdded();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="card" style={{ width: '100%', maxWidth: 380, marginBottom: 16, textAlign: 'left' }}>
      <div style={{ marginBottom: 14 }}>
        <input
          className="input"
          type="text"
          placeholder="Title"
          value={title}
          onChange={e => setTitle(e.target.value)}
          required
          style={{ width: '100%' }}
        />
      </div>
      <div style={{ marginBottom: 14 }}>
        <textarea
          className="input"
          placeholder="Description"
          value={description}
          onChange={e => setDescription(e.target.value)}
          required
          style={{ width: '100%', minHeight: 60 }}
        />
      </div>
      <button type="submit" disabled={loading} style={{ width: '100%', marginTop: 6 }}>
        {loading ? 'Adding...' : 'Add Task'}
      </button>
      {error && <div style={{ color: 'red', marginTop: 8 }}>{error}</div>}
    </form>
  );
}

export default AddTaskForm; 