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

  return (
    <>
      <div className="lists-row" style={{ alignItems: 'flex-start' }}>
        <div>
          <AddUserForm onUserAdded={() => setUsersRefreshKey(k => k + 1)} />
          <UsersList
            users={users}
            tasks={tasks}
            onSelect={handleUserSelect}
            selectedUserId={selectedUser?.id}
            onUserUpdated={() => setUsersRefreshKey(k => k + 1)}
          />
        </div>
        <div>
          <AddTaskForm onTaskAdded={() => setTasksRefreshKey(k => k + 1)} />
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
        <div style={{
          margin: '2rem auto',
          padding: '1.5rem',
          background: '#f7fafd',
          borderRadius: 10,
          boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
          minWidth: 260,
          maxWidth: 400,
          textAlign: 'center',
        }}>
          <div style={{ marginBottom: 8 }}>
            <strong>User:</strong> {selectedUser.username} <br />
            <strong>Task:</strong> {selectedTask.title}
          </div>
          {!isAssigned && (
            <button
              onClick={() => handleAssign(selectedUser, selectedTask)}
              disabled={actionLoading}
              style={{ marginRight: 12, padding: '8px 16px' }}
            >
              Assign
            </button>
          )}
          {isAssigned && (
            <button
              onClick={() => handleRemove(selectedUser, selectedTask)}
              disabled={actionLoading}
              style={{ padding: '8px 16px' }}
            >
              Remove
            </button>
          )}
        </div>
      )}
      {(actionError || actionSuccess) && (
        <div style={{ textAlign: 'center', marginTop: 16 }}>
          {actionError && <div style={{ color: 'red' }}>{actionError}</div>}
          {actionSuccess && <div style={{ color: 'green' }}>{actionSuccess}</div>}
        </div>
      )}
    </>
  )
}

export default App
