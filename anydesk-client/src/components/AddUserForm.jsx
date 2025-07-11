import { useState } from 'react';

function AddUserForm({ onUserAdded }) {
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
      if (!res.ok) throw new Error('Failed to add user');
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
    <form onSubmit={handleSubmit} style={{ marginBottom: '1.5rem', maxWidth: 400 }}>
      <h3>Add User</h3>
      <div style={{ marginBottom: 8 }}>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={e => setUsername(e.target.value)}
          required
          style={{ width: '100%', padding: 8 }}
        />
      </div>
      <div style={{ marginBottom: 8 }}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          required
          style={{ width: '100%', padding: 8 }}
        />
      </div>
      <button type="submit" disabled={loading} style={{ padding: '8px 16px' }}>
        {loading ? 'Adding...' : 'Add User'}
      </button>
      {error && <div style={{ color: 'red', marginTop: 8 }}>{error}</div>}
    </form>
  );
}

export default AddUserForm; 