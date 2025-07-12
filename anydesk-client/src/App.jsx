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
  const [showAddUser, setShowAddUser] = useState(false);
  const [showAddTask, setShowAddTask] = useState(false);

  useEffect(() => {
    fetch('/api/users')
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch users')
        return res.json()
      })
      .then(fetchedUsers => {
        setUsers(fetchedUsers);
        setSelectedUser(prev => prev && !fetchedUsers.some(u => u.id === prev.id) ? null : prev);
      })
      .catch(() => setUsers([]))
  }, [usersRefreshKey])

  useEffect(() => {
    fetch('/api/tasks')
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch tasks')
        return res.json()
      })
      .then(fetchedTasks => {
        setTasks(fetchedTasks);
        // Clear selectedTask if it no longer exists
        setSelectedTask(prev => prev && !fetchedTasks.some(t => t.id === prev.id) ? null : prev);
      })
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
        <div className="users-list-scrollable">
          <UsersList
            users={users}
            tasks={tasks}
            onSelect={handleUserSelect}
            selectedUserId={selectedUser?.id}
            onUserUpdated={() => setUsersRefreshKey(k => k + 1)}
            onAddUser={() => setShowAddUser(true)}
          />
        </div>
        <div>
          <TasksList
            tasks={tasks}
            users={users}
            onSelect={handleTaskSelect}
            selectedTaskId={selectedTask?.id}
            onTaskUpdated={() => setTasksRefreshKey(k => k + 1)}
            onAddTask={() => setShowAddTask(true)}
          />
        </div>
      </div>
      {showAddUser && (
        <div className="modal-overlay" style={{position: 'fixed', top:0, left:0, width:'100vw', height:'100vh', background:'rgba(0,0,0,0.18)', zIndex: 200, display:'flex', alignItems:'center', justifyContent:'center', backdropFilter:'blur(2px)'}}>
          <div style={{background:'#fff', borderRadius:18, boxShadow:'0 8px 40px rgba(0,0,0,0.18)', border:'1.5px solid #e5e7eb', padding:'36px 32px 32px 32px', minWidth:340, maxWidth:400, position:'relative', display:'flex', flexDirection:'column', alignItems:'center'}}>
            <button onClick={()=>setShowAddUser(false)} style={{position:'absolute', top:6, right:8, width:28, height:28, padding:0, background:'none', border:'none', fontSize:22, cursor:'pointer', color:'#888', display:'flex', alignItems:'center', justifyContent:'center', borderRadius:'50%', lineHeight:1}}>&times;</button>
            <div style={{height: 18}} />
            <AddUserForm onUserAdded={() => { setUsersRefreshKey(k => k + 1); setShowAddUser(false); }} noCard />
          </div>
        </div>
      )}
      {showAddTask && (
        <div className="modal-overlay" style={{position: 'fixed', top:0, left:0, width:'100vw', height:'100vh', background:'rgba(0,0,0,0.18)', zIndex: 200, display:'flex', alignItems:'center', justifyContent:'center', backdropFilter:'blur(2px)'}}>
          <div style={{background:'#fff', borderRadius:18, boxShadow:'0 8px 40px rgba(0,0,0,0.18)', border:'1.5px solid #e5e7eb', padding:'36px 32px 32px 32px', minWidth:340, maxWidth:400, position:'relative', display:'flex', flexDirection:'column', alignItems:'center'}}>
            <button onClick={()=>setShowAddTask(false)} style={{position:'absolute', top:6, right:8, width:28, height:28, padding:0, background:'none', border:'none', fontSize:22, cursor:'pointer', color:'#888', display:'flex', alignItems:'center', justifyContent:'center', borderRadius:'50%', lineHeight:1}}>&times;</button>
            <div style={{height: 18}} />
            <AddTaskForm onTaskAdded={() => { setTasksRefreshKey(k => k + 1); setShowAddTask(false); }} noCard />
          </div>
        </div>
      )}
      {selectedUser && selectedTask && (
        <button
          className={`assign-panel${(selectedUser && selectedTask) ? ' visible' : ''}${isAssigned ? ' unassign' : ''}`}
          type="button"
          onClick={() => {
            if (isAssigned) {
              handleRemove(selectedUser, selectedTask);
            } else {
              handleAssign(selectedUser, selectedTask);
            }
          }}
          disabled={actionLoading}
        >
          <div className="assign-title">
            <span className="assign-action">{isAssigned ? 'Unassign' : 'Assign'}</span>
            <span className="assign-entity">
              {selectedTask.title} {isAssigned ? 'from' : 'to'} {selectedUser.username}
            </span>
          </div>
        </button>
      )}
    </>
  )
}

export default App
