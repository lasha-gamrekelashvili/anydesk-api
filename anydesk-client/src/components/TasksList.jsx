import { useState } from 'react';
import styles from './TasksList.module.css';

function TasksList({ tasks, users, onSelect, selectedTaskId, onTaskUpdated }) {
  const [editTaskId, setEditTaskId] = useState(null);
  const [editTitle, setEditTitle] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [editLoading, setEditLoading] = useState(false);
  const [editError, setEditError] = useState(null);

  const startEdit = (task) => {
    setEditTaskId(task.id);
    setEditTitle(task.title);
    setEditDescription(task.description);
    setEditError(null);
  };

  const cancelEdit = () => {
    setEditTaskId(null);
    setEditTitle('');
    setEditDescription('');
    setEditError(null);
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    setEditLoading(true);
    setEditError(null);
    try {
      const res = await fetch(`/api/tasks/${editTaskId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title: editTitle, description: editDescription }),
      });
      if (!res.ok) throw new Error('Failed to update task');
      setEditTaskId(null);
      setEditTitle('');
      setEditDescription('');
      if (onTaskUpdated) onTaskUpdated();
    } catch (err) {
      setEditError(err.message);
    } finally {
      setEditLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.title}>Tasks</div>
      <ul>
        {tasks.map((task) => {
          const assignedUsers = (task.assignedUserIds || [])
            .map(userId => users.find(u => u.id === userId)?.username)
            .filter(Boolean);
          const isSelected = selectedTaskId === task.id;
          const isEditing = editTaskId === task.id;
          return (
            <li
              key={task.id}
              onClick={() => !isEditing && onSelect && onSelect(task)}
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
                    value={editTitle}
                    onChange={e => setEditTitle(e.target.value)}
                    required
                    style={{ padding: 6 }}
                  />
                  <textarea
                    value={editDescription}
                    onChange={e => setEditDescription(e.target.value)}
                    required
                    style={{ padding: 6, minHeight: 48 }}
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
                    <span><strong>Title:</strong> {task.title}</span>
                    {isSelected && (
                      <button
                        style={{ marginLeft: 8, padding: '2px 10px', fontSize: 13 }}
                        onClick={e => { e.stopPropagation(); startEdit(task); }}
                      >Edit</button>
                    )}
                  </div>
                  <div><strong>Description:</strong> {task.description}</div>
                  <div style={{ fontSize: '0.95em', color: '#666', marginTop: 4 }}>
                    <strong>Users:</strong> {assignedUsers.length > 0 ? assignedUsers.join(', ') : 'None'}
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

export default TasksList; 