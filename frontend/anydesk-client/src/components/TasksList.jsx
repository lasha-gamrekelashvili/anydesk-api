import { useEffect, useState } from 'react';
import styles from './TasksList.module.css';

function TasksList() {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch('/api/tasks')
      .then((res) => {
        if (!res.ok) throw new Error('Failed to fetch tasks');
        return res.json();
      })
      .then((data) => {
        setTasks(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div className={styles.container}>Loading tasks...</div>;
  if (error) return <div className={styles.container}>Error: {error}</div>;

  return (
    <div className={styles.container}>
      <div className={styles.title}>Tasks</div>
      <ul>
        {tasks.map((task) => (
          <li key={task.id}>
            <div><strong>ID:</strong> {task.id}</div>
            <div><strong>Title:</strong> {task.title}</div>
            <div><strong>Description:</strong> {task.description}</div>
            <div><strong>Assigned User IDs:</strong> {task.assignedUserIds && task.assignedUserIds.length > 0 ? task.assignedUserIds.join(', ') : 'None'}</div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TasksList; 