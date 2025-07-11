import UsersList from './components/UsersList'
import TasksList from './components/TasksList'
import './App.css'

function App() {

  return (
    <>
      <div className="lists-row">
        <UsersList />
        <TasksList />
      </div>
    </>
  )
}

export default App
