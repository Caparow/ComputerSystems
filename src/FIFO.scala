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
                  startingNumOfTasks: Int) {

    val random = scala.util.Random

    var readyState: Boolean = false
    var taskList: ListBuffer[Task] = ListBuffer()
    var operationsDone: Int = 0
    var totalOpPerMS = 0
    processorsList.foreach(totalOpPerMS += _.operationsPerMS)
    processorsList.foreach(_.state = false)

    private def executeProcessor(processor: Processor): Unit = {
      if (taskList.nonEmpty && !processor.getState) {
        if (taskList.head.appropriateProcessors.contains(processorsList.indexOf(processor))){
          processor.setTask(taskList.head)
          /**println("set task for " + processorsList.indexOf(proc).toString +
          " processor with time " +
          taskList.head.execTime.toString) */

          taskList = taskList.drop(1)
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
      taskList = createTaskList(startingNumOfTasks, sRangeOfDif, eRangeOfDif)
      while (time < 10000){
        if(random.nextInt(100) < probability)
          taskList += createTask(sRangeOfDif, eRangeOfDif)
        processorsList.foreach(executeProcessor)
        time += 1
      }
      operationsDone.toDouble / time / totalOpPerMS
    }

    def testCOE: Double = {
      var COE: Double = 0
      for (_ <- 1 to 10)
        COE += process
      COE / 10
    }
  }
}

