/**
  * Created by cypress on 27.02.17.
  */

import Processor.Processor
import FIFO.FIFO
import Scheduler.Scheduler

object main {
  def main(ars: Array[String]): Unit ={
    val probabilityOfNewTask = 60
    val startDifficultOfTask = 500
    val endDifficultOfTask = 10000
    val startingNumOfTasks = 20
    val processorsList: List[Processor] = List(Processor(600),
                                               Processor(400),
                                               Processor(300),
                                               Processor(200),
                                               Processor(50))

    val fifo = FIFO(processorsList,
      probabilityOfNewTask,
      startDifficultOfTask,
      endDifficultOfTask,
      startingNumOfTasks)
    val scheduler = Scheduler(processorsList,
      probabilityOfNewTask,
      startDifficultOfTask,
      endDifficultOfTask,
      startingNumOfTasks)
    println("COE of First-In-First-Out algorithm: " + fifo.testCOE.toString)
    println("COE of the Scheduler on the worst processor: " + scheduler.testCOEonWorstProc.toString)
    println("COE of the Scheduler on the best processor (20, 4): " + scheduler.testCOEonBestProc.toString)
  }
}


