import Processor.Processor
import Task._
import scala.collection.mutable.ListBuffer

/**
  * Created by Cypress on 28.02.2017.
  */
package FIFO {
  case class FIFO(processorsList: List[Processor],
                  probability: Int,
                  sRangeOfDif: Int,
                  eRangeOfDif: Int,
                  startingNumOfTasks: Int,
                  timeForCheck: Int) {

    val random = scala.util.Random
    var readyState: Boolean = false
    var taskList: ListBuffer[Task] = ListBuffer()
    var operationsDone: Int = 0
    var totalOpPerMS = 0
    var operationsReleased = 0
    var tasksReleased = 0
    processorsList.foreach(totalOpPerMS += _.operationsPerMS)
    processorsList.foreach(_.state = false)

    private def executeProcessor(processor: Processor): Unit = {
      import scala.util.control.Breaks._
      if (taskList.nonEmpty && !processor.getState) {
        breakable{
          for (task <- taskList){
            if (task.appropriateProcessors.contains(processorsList.indexOf(processor))){
              processor.setTask(task)
              taskList.remove(taskList.indexOf(task))
              tasksReleased += 1
              break()
            }
          }
        }
      }
      else {
        operationsDone += processor.operationsPerMS
        processor.executeTask()
      }
    }

    private def process: Double = {
      var time: Int = 0
      operationsDone = 0
      tasksReleased = 0
      taskList = createTaskList(startingNumOfTasks, sRangeOfDif, eRangeOfDif)
      while (time < timeForCheck){
        if(random.nextInt(100) < probability)
          taskList += createTask(sRangeOfDif, eRangeOfDif)
        processorsList.foreach(executeProcessor)
        time += 1
      }
      operationsReleased = operationsDone
      operationsDone.toDouble / time / totalOpPerMS
    }

    def testCOE: Double = {
      var tasks: Int = 0
      var op: Int = 0
      var COE: Double = 0
      for (_ <- 1 to 5){
        COE += process
        tasks += tasksReleased
        op += operationsReleased
      }
      println("COE of First-In-First-Out algorithm: " + (COE / 5).toString)
      println("Tasks released: " + (tasks/5).toString)
      println("Operations released: " + (op/5).toString)
      COE / 10
    }
  }
}

