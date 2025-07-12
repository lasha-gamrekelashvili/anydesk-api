import { useState } from 'react';
import cardStyles from './CardList.module.css';

function AddUserForm({ onUserAdded, noCard }) {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const res = await fetch('/api/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, email }),
      });
      if (!res.ok) {
        let msg = 'Failed to add user';
        try {
          const data = await res.json();
          if (data && (data.message || data.error)) msg = data.message || data.error;
        } catch {}
        throw new Error(msg);
      }
      setUsername('');
      setEmail('');
      if (onUserAdded) onUserAdded();
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
          placeholder="Username"
          value={username}
          onChange={e => setUsername(e.target.value)}
          required
          style={{ width: '100%' }}
        />
      </div>
      <div style={{ marginBottom: 14 }}>
        <input
          className="input"
          type="email"
          placeholder="Email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          required
          style={{ width: '100%' }}
        />
      </div>
      <button type="submit" className={cardStyles.saveBtn} disabled={loading} style={{ width: '100%', marginTop: 6 }}>
        {loading ? 'Adding...' : 'Add User'}
      </button>
      {error && <div className={cardStyles.errorInline}>{error}</div>}
    </form>
  );
}

export default AddUserForm; 