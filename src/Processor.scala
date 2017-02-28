import Task._

object Processor {
  case class Processor(operationsPerMS: Int,
                         var state : Boolean = false,
                         var timeLeft : Int = 0) {

    def setState(st: Boolean): Unit = this.state = st

    def getState: Boolean = this.state

    def setTask(task: Task): Unit = {
      this.timeLeft = task.execTime
      this.state = true
    }

    def executeTask(): Unit = {
      this.timeLeft -= this.operationsPerMS
      if (this.timeLeft <= 0){
        this.timeLeft = 0
        this.state = false
      }
    }
  }
}
