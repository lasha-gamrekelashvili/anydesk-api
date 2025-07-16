import { useState } from 'react';
import cardStyles from './CardList.module.css';
import { FiEdit, FiTrash2, FiUser, FiMail, FiList } from 'react-icons/fi';

function UsersList({ users, tasks, onSelect, selectedUserId, onUserUpdated, onAddUser }) {
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
      const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users/${editUserId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: editUsername, email: editEmail }),
      });
      if (!res.ok) {
        let msg = 'Failed to update user';
        try {
          const data = await res.json();
          if (data && (data.message || data.error)) msg = data.message || data.error;
        } catch {}
        throw new Error(msg);
      }
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

  const sortedUsers = [...users].sort((a, b) => a.username.localeCompare(b.username));

  return (
    <div className={cardStyles.container}>
      <div style={{display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingBottom: 4, borderBottom: '2px solid #e5e7eb', marginBottom: 16}}>
        <div className={cardStyles.title}>Users</div>
        <button className={cardStyles.saveBtn} style={{marginLeft: 12}} onClick={e => { e.stopPropagation(); onAddUser && onAddUser(); }}>Add User</button>
      </div>
      {sortedUsers.length === 0 ? (
        <div className={cardStyles.emptyMessage}>No users... maybe someone will add some</div>
      ) : (
        <ul>
          {sortedUsers.map((user) => {
            const assignedTasks = (user.taskIds || [])
              .map(taskId => tasks.find(t => t.id === taskId)?.title)
              .filter(Boolean);
            const isSelected = selectedUserId === user.id;
            const isEditing = editUserId === user.id;
            return (
              <li
                key={user.id}
                className={`${cardStyles.cardItem} ${isSelected ? cardStyles.selected : ''}`}
                onClick={() => !isEditing && onSelect && onSelect(user)}
              >
                {isEditing ? (
                  <form className={cardStyles.editForm} onSubmit={handleEditSubmit}>
                    <input
                      className={cardStyles.editInput}
                      type="text"
                      value={editUsername}
                      onChange={e => setEditUsername(e.target.value)}
                      required
                    />
                    <input
                      className={cardStyles.editInput}
                      type="email"
                      value={editEmail}
                      onChange={e => setEditEmail(e.target.value)}
                      required
                    />
                    <div className={cardStyles.editActions}>
                      <button
                        type="submit"
                        className={cardStyles.saveBtn}
                        disabled={editLoading}
                      >
                        Save
                      </button>
                      <button
                        type="button"
                        className={cardStyles.cancelBtn}
                        onClick={cancelEdit}
                        disabled={editLoading}
                      >
                        Cancel
                      </button>
                    </div>
                    {editError && (
                      <div className={cardStyles.errorInline}>{editError}</div>
                    )}
                  </form>
                ) : (
                  <>
                    <div className={cardStyles.fieldRow}>
                      <strong><FiUser style={{marginRight: 4}} /> Username</strong>
                      <span>{user.username}</span>
                    </div>
                    <div className={cardStyles.fieldRow}>
                      <strong><FiMail style={{marginRight: 4}} /> Email</strong>
                      <span>{user.email}</span>
                    </div>
                    <div className={cardStyles.fieldRow}>
                      <strong><FiList style={{marginRight: 4}} /> Tasks</strong>
                      <span className={cardStyles.descriptionText}>{assignedTasks.length > 0 ? assignedTasks.join(', ') : 'None'}</span>
                    </div>
                    {isSelected && (
                      <div className={cardStyles.actions}>
                        <button className={cardStyles.saveBtn} onClick={e => { e.stopPropagation(); startEdit(user); }}>
                          <FiEdit size={14} /> Edit
                        </button>
                        <button
                          className={cardStyles.removeBtn}
                          onClick={async e => {
                            e.stopPropagation();
                            try {
                              const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users/${user.id}`, { method: 'DELETE' });
                              if (!res.ok) throw new Error('Failed to remove user');
                              if (onUserUpdated) onUserUpdated();
                            } catch (err) {
                              alert(err.message);
                            }
                          }}
                        >
                          <FiTrash2 size={14} /> Remove
                        </button>
                      </div>
                    )}
                  </>
                )}
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}

export default UsersList; 