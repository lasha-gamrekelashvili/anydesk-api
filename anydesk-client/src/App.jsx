import { useState, useEffect } from 'react'
import UsersList from './components/UsersList'
import TasksList from './components/TasksList'
import AddUserForm from './components/AddUserForm'
import AddTaskForm from './components/AddTaskForm'
import './App.css'

function App() {
  const [users, setUsers] = useState([])
  const [tasks, setTasks] = useState([])
  const [usersRefreshKey, setUsersRefreshKey] = useState(0)
  const [tasksRefreshKey, setTasksRefreshKey] = useState(0)
  const [selectedUser, setSelectedUser] = useState(null)
  const [selectedTask, setSelectedTask] = useState(null)
  const [actionLoading, setActionLoading] = useState(false)
  const [actionError, setActionError] = useState(null)
  const [actionSuccess, setActionSuccess] = useState(null)

  useEffect(() => {
    fetch('/api/users')
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch users')
        return res.json()
      })
      .then(setUsers)
      .catch(() => setUsers([]))
  }, [usersRefreshKey])

  useEffect(() => {
    fetch('/api/tasks')
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch tasks')
        return res.json()
      })
      .then(setTasks)
      .catch(() => setTasks([]))
  }, [tasksRefreshKey])

  const handleAssign = async (user, task) => {
    if (!user || !task) return
    setActionLoading(true)
    setActionError(null)
    setActionSuccess(null)
    try {
      const res = await fetch(`/api/users/${user.id}/assign-task/${task.id}`, { method: 'PATCH' })
      if (!res.ok) throw new Error('Failed to assign task to user')
      setActionSuccess('Task assigned to user!')
      setUsersRefreshKey(k => k + 1)
      setTasksRefreshKey(k => k + 1)
      setSelectedUser(prev => prev && prev.id === user.id
        ? { ...prev, taskIds: [...(prev.taskIds || []), task.id] }
        : prev)
    } catch (err) {
      setActionError(err.message)
    } finally {
      setActionLoading(false)
    }
  }

  const handleRemove = async (user, task) => {
    if (!user || !task) return
    setActionLoading(true)
    setActionError(null)
    setActionSuccess(null)
    try {
      const res = await fetch(`/api/users/${user.id}/remove-task/${task.id}`, { method: 'PATCH' })
      if (!res.ok) throw new Error('Failed to remove task from user')
      setActionSuccess('Task removed from user!')
      setUsersRefreshKey(k => k + 1)
      setTasksRefreshKey(k => k + 1)
      setSelectedUser(prev => prev && prev.id === user.id
        ? { ...prev, taskIds: (prev.taskIds || []).filter(id => id !== task.id) }
        : prev)
    } catch (err) {
      setActionError(err.message)
    } finally {
      setActionLoading(false)
    }
  }

  const isAssigned = selectedUser && selectedTask && Array.isArray(selectedUser.taskIds)
    ? selectedUser.taskIds.includes(selectedTask.id)
    : false;

  const handleUserSelect = (user) => {
    setSelectedUser(selectedUser && selectedUser.id === user.id ? null : user)
  }
  const handleTaskSelect = (task) => {
    setSelectedTask(selectedTask && selectedTask.id === task.id ? null : task)
  }

  useEffect(() => {
    if (actionError || actionSuccess) {
      const timer = setTimeout(() => {
        setActionError(null);
        setActionSuccess(null);
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [actionError, actionSuccess]);

  return (
    <>
      <div className="dashboard-grid">
        <div>
          <AddUserForm onUserAdded={() => setUsersRefreshKey(k => k + 1)} />
        </div>
        <div>
          <AddTaskForm onTaskAdded={() => setTasksRefreshKey(k => k + 1)} />
        </div>
        <div className="users-list-scrollable">
          <UsersList
            users={users}
            tasks={tasks}
            onSelect={handleUserSelect}
            selectedUserId={selectedUser?.id}
            onUserUpdated={() => setUsersRefreshKey(k => k + 1)}
          />
        </div>
        <div>
          <TasksList
            tasks={tasks}
            users={users}
            onSelect={handleTaskSelect}
            selectedTaskId={selectedTask?.id}
            onTaskUpdated={() => setTasksRefreshKey(k => k + 1)}
          />
        </div>
      </div>
      {selectedUser && selectedTask && (
        <div style={{ position: 'fixed', right: 24, top: 24, zIndex: 102, minWidth: 180, display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
          {(actionError || actionSuccess) && (
            <div style={{
              marginBottom: 8,
              textAlign: 'center',
              pointerEvents: 'none',
            }}>
              {actionError && <div style={{ color: 'red', background: '#fff', borderRadius: 5, padding: '6px 12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>{actionError}</div>}
              {actionSuccess && <div style={{ color: 'green', background: '#fff', borderRadius: 5, padding: '6px 12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>{actionSuccess}</div>}
            </div>
          )}
          <div className="modal-panel">
            <div style={{ marginBottom: 8, fontSize: '0.98rem' }}>
              <strong>User:</strong> {selectedUser.username} <br />
              <strong>Task:</strong> {selectedTask.title}
            </div>
            {!isAssigned && (
              <button
                onClick={() => handleAssign(selectedUser, selectedTask)}
                disabled={actionLoading}
                style={{ marginRight: 8 }}
              >
                Assign
              </button>
            )}
            {isAssigned && (
              <button
                onClick={() => handleRemove(selectedUser, selectedTask)}
                disabled={actionLoading}
              >
                Remove
              </button>
            )}
          </div>
        </div>
      )}
    </>
  )
}

export default App
