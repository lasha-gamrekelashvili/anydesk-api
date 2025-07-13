import React, { useState } from 'react';
import { FiEdit, FiTrash2, FiTag, FiFileText, FiUsers } from 'react-icons/fi';
import styles from './CardList.module.css';

function TasksList({ tasks, users, onSelect, selectedTaskId, onTaskUpdated, onAddTask }) {
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
      const res = await fetch(`${import.meta.env.VITE_API_URL}/api/tasks/${editTaskId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title: editTitle, description: editDescription }),
      });
      if (!res.ok) {
        let msg = 'Failed to update task';
        try {
          const data = await res.json();
          if (data && (data.message || data.error)) msg = data.message || data.error;
        } catch {}
        throw new Error(msg);
      }
      setEditTaskId(null);
      setEditTitle('');
      setEditDescription('');
      onTaskUpdated?.();
    } catch (err) {
      setEditError(err.message);
    } finally {
      setEditLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div style={{display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingBottom: 4, borderBottom: '2px solid #e5e7eb', marginBottom: 16}}>
        <div className={styles.title}>Tasks</div>
        <button className={styles.saveBtn} style={{marginLeft: 12}} onClick={e => { e.stopPropagation(); onAddTask && onAddTask(); }}>Add Task</button>
      </div>
      {tasks.length === 0 ? (
        <div className={styles.emptyMessage}>No tasks... maybe someone will add some</div>
      ) : (
        <ul>
          {tasks.map((task) => {
            const assignedUsers = (task.assignedUserIds || [])
              .map(id => users.find(u => u.id === id)?.username)
              .filter(Boolean);
            const isSelected = selectedTaskId === task.id;
            const isEditing  = editTaskId === task.id;

            return (
              <li
                key={task.id}
                className={`${styles.cardItem} ${isSelected ? styles.selected : ''}`}
                onClick={() => !isEditing && onSelect?.(task)}
              >
                {isEditing ? (
                  <form className={styles.editForm} onSubmit={handleEditSubmit}>
                    <input
                      className={styles.editInput}
                      type="text"
                      value={editTitle}
                      onChange={e => setEditTitle(e.target.value)}
                      required
                    />
                    <textarea
                      className={styles.editTextarea}
                      value={editDescription}
                      onChange={e => setEditDescription(e.target.value)}
                      required
                    />
                    <div className={styles.editActions}>
                      <button
                        type="submit"
                        className={styles.saveBtn}
                        disabled={editLoading}
                      >
                        Save
                      </button>
                      <button
                        type="button"
                        className={styles.cancelBtn}
                        onClick={cancelEdit}
                        disabled={editLoading}
                      >
                        Cancel
                      </button>
                    </div>
                    {editError && (
                      <div className={styles.errorInline}>{editError}</div>
                    )}
                  </form>
                ) : (
                  <>
                    <div className={styles.fieldRow}>
                      <strong><FiTag style={{marginRight: 4}} /> Title</strong>
                      <span>{task.title}</span>
                    </div>
                    <div className={styles.fieldRow}>
                      <strong><FiFileText style={{marginRight: 4}} /> Description</strong>
                      <span className={styles.descriptionText}>{task.description}</span>
                    </div>
                    <div className={styles.fieldRow}>
                      <strong><FiUsers style={{marginRight: 4}} /> Users</strong>
                      <span className={styles.descriptionText}>{assignedUsers.join(', ') || 'None'}</span>
                    </div>

                    {isSelected && (
                      <div className={styles.actions}>
                        <button className={styles.saveBtn} onClick={e => { e.stopPropagation(); startEdit(task); }}>
                          <FiEdit size={14} /> Edit
                        </button>
                        <button
                          className={styles.removeBtn}
                          onClick={async e => {
                            e.stopPropagation();
                            try {
                              const res = await fetch(`${import.meta.env.VITE_API_URL}/api/tasks/${task.id}`, { method: 'DELETE' });
                              if (!res.ok) throw new Error('Failed to remove task');
                              onTaskUpdated?.();
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

export default TasksList;
