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
    var time: Int = 0
    var taskList: ListBuffer[Task] = ListBuffer()
    var readyState: Boolean = false
    var operationsDone: Int = 0
    var totalOpPerMS = 0
    processorsList.foreach(totalOpPerMS += _.operationsPerMS)
    init()

    def getCOE: Double = operationsDone.toDouble / time / totalOpPerMS

    private def executeProcessor(processor: Processor): Unit = {
      if (taskList.nonEmpty &&
        !processor.getState &&
        taskList.head.appropriateProcessors.contains(processorsList.indexOf(processor))){

        processor.setTask(taskList.head)
        /**println("set task for " + processorsList.indexOf(proc).toString +
          " processor with time " +
          taskList.head.execTime.toString) */
        operationsDone += taskList.head.execTime

        if (taskList.size == 1)
          taskList = ListBuffer()
        else
          taskList = taskList.drop(1)
      }
      else processor.executeTask()
    }

    private def init(): Unit = {
      taskList = createTaskList(startingNumOfTasks, sRangeOfDif, eRangeOfDif)
      while (taskList.nonEmpty && !readyState){
        if(random.nextInt(100) < probability)
          taskList += createTask(sRangeOfDif, eRangeOfDif)
        processorsList.foreach(executeProcessor)
        time += 1
      }
    }
  }
}

