/**
  * Created by cypress on 27.02.17.
  */

import Processor.Processor
import FIFO.FIFO

object main {
  def main(ars: Array[String]): Unit ={
    val probabilityOfNewTask = 20
    val startDifficultOfTask = 500
    val endDifficultOfTask = 10000
    val startingNumOfTasks = 10
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

    println(fifo.time)
    println(fifo.getCOE)
  }
}


