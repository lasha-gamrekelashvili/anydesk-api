import { useState } from 'react';
import styles from './CardList.module.css';

function AddTaskForm({ onTaskAdded, noCard }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(`${import.meta.env.VITE_API_URL}/api/tasks`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, description }),
      });
      if (!res.ok) {
        let msg = 'Failed to add task';
        try {
          const data = await res.json();
          if (data && (data.message || data.error)) msg = data.message || data.error;
        } catch {}
        throw new Error(msg);
      }
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
    <form onSubmit={handleSubmit} className={noCard ? '' : 'card'} style={noCard ? { width: '100%', maxWidth: 380, textAlign: 'left' } : { width: '100%', maxWidth: 380, marginBottom: 16, textAlign: 'left' }}>
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
      <button type="submit" className={styles.saveBtn} disabled={loading} style={{ width: '100%', marginTop: 6 }}>
        {loading ? 'Adding...' : 'Add Task'}
      </button>
      {error && <div className={styles.errorInline}>{error}</div>}
    </form>
  );
}

export default AddTaskForm; 