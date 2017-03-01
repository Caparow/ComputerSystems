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
    val timeForCheck = 1000
    val processorsList: List[Processor] = List(Processor(600),
                                               Processor(400),
                                               Processor(300),
                                               Processor(200),
                                               Processor(50))

    val fifo = FIFO(processorsList,
      probabilityOfNewTask,
      startDifficultOfTask,
      endDifficultOfTask,
      startingNumOfTasks,
      timeForCheck)
    val scheduler = Scheduler(processorsList,
      probabilityOfNewTask,
      startDifficultOfTask,
      endDifficultOfTask,
      startingNumOfTasks,
      timeForCheck)

    println("_"*10)
    fifo.testCOE
    println("_"*10)
    scheduler.testCOEonWorstProc
    println("_"*10)
    scheduler.testCOEonBestProc(20)
    println("_"*10)
    scheduler.testCOEonBestProc(60)
    println("_"*10)
  }
}


