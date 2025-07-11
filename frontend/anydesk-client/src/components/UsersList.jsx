import { useEffect, useState } from 'react';
import styles from './UsersList.module.css';

function UsersList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch('/api/users')
      .then((res) => {
        if (!res.ok) throw new Error('Failed to fetch users');
        return res.json();
      })
      .then((data) => {
        setUsers(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div className={styles.container}>Loading users...</div>;
  if (error) return <div className={styles.container}>Error: {error}</div>;

  return (
    <div className={styles.container}>
      <div className={styles.title}>Users</div>
      <ul>
        {users.map((user) => (
          <li key={user.id}>
            <div><strong>ID:</strong> {user.id}</div>
            <div><strong>Username:</strong> {user.username}</div>
            <div><strong>Email:</strong> {user.email}</div>
            <div><strong>Task IDs:</strong> {user.taskIds && user.taskIds.length > 0 ? user.taskIds.join(', ') : 'None'}</div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default UsersList; 