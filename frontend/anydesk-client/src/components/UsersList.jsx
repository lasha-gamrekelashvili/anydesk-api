import { useState } from 'react';
import styles from './UsersList.module.css';

function UsersList({ users, tasks, onSelect, selectedUserId, onUserUpdated }) {
  const [editUserId, setEditUserId] = useState(null);
  const [editUsername, setEditUsername] = useState('');
  const [editEmail, setEditEmail] = useState('');
  const [editLoading, setEditLoading] = useState(false);
  const [editError, setEditError] = useState(null);

  const startEdit = (user) => {
    setEditUserId(user.id);
    setEditUsername(user.username);
    setEditEmail(user.email);
    setEditError(null);
  };

  const cancelEdit = () => {
    setEditUserId(null);
    setEditUsername('');
    setEditEmail('');
    setEditError(null);
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    setEditLoading(true);
    setEditError(null);
    try {
      const res = await fetch(`/api/users/${editUserId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: editUsername, email: editEmail }),
      });
      if (!res.ok) throw new Error('Failed to update user');
      setEditUserId(null);
      setEditUsername('');
      setEditEmail('');
      if (onUserUpdated) onUserUpdated();
    } catch (err) {
      setEditError(err.message);
    } finally {
      setEditLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.title}>Users</div>
      <ul>
        {users.map((user) => {
          const assignedTasks = (user.taskIds || [])
            .map(taskId => tasks.find(t => t.id === taskId)?.title)
            .filter(Boolean);
          const isSelected = selectedUserId === user.id;
          const isEditing = editUserId === user.id;
          return (
            <li
              key={user.id}
              onClick={() => !isEditing && onSelect && onSelect(user)}
              style={{
                cursor: isEditing ? 'default' : 'pointer',
                background: isSelected ? '#e0f7fa' : undefined,
                borderRadius: isSelected ? 6 : undefined,
                position: 'relative',
              }}
            >
              {isEditing ? (
                <form onSubmit={handleEditSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
                  <input
                    type="text"
                    value={editUsername}
                    onChange={e => setEditUsername(e.target.value)}
                    required
                    style={{ padding: 6 }}
                  />
                  <input
                    type="email"
                    value={editEmail}
                    onChange={e => setEditEmail(e.target.value)}
                    required
                    style={{ padding: 6 }}
                  />
                  <div style={{ display: 'flex', gap: 8, marginTop: 4 }}>
                    <button type="submit" disabled={editLoading} style={{ padding: '4px 12px' }}>
                      Save
                    </button>
                    <button type="button" onClick={cancelEdit} disabled={editLoading} style={{ padding: '4px 12px' }}>
                      Cancel
                    </button>
                  </div>
                  {editError && <div style={{ color: 'red', fontSize: 13 }}>{editError}</div>}
                </form>
              ) : (
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <span><strong>Username:</strong> {user.username}</span>
                    {isSelected && (
                      <button
                        style={{ marginLeft: 8, padding: '2px 10px', fontSize: 13 }}
                        onClick={e => { e.stopPropagation(); startEdit(user); }}
                      >Edit</button>
                    )}
                  </div>
                  <div><strong>Email:</strong> {user.email}</div>
                  <div style={{ fontSize: '0.95em', color: '#666', marginTop: 4 }}>
                    <strong>Tasks:</strong> {assignedTasks.length > 0 ? assignedTasks.join(', ') : 'None'}
                  </div>
                </div>
              )}
            </li>
          );
        })}
      </ul>
    </div>
  );
}

export default UsersList; 