/**
  * Created by cypress on 27.02.17.
  */

import Processor.Processor
import FIFO.FIFO
import Scheduler.Scheduler

object Main extends App {
  println("\t ENTER SOME VALUES:")
  val probabilityOfNewTask = readLine("Enter the probability of new task: ").toInt
  val startDifficultOfTask = readLine("Enter min difficult of the task: ").toInt
  val endDifficultOfTask = readLine("Enter max difficult of the task: ").toInt
  val startingNumOfTasks = readLine("Enter starting number of tasks: ").toInt
  val timeForCheck = readLine("Enter time for experement(ms): ").toInt
  println("\t ENTER OPS FOR PROCESSORS:")
  val processorsList: List[Processor] = List(Processor(readLine("OPS for 1st: ").toInt),
    Processor(readLine("OPS for 2st: ").toInt),
    Processor(readLine("OPS for 3st: ").toInt),
    Processor(readLine("OPS for 4st: ").toInt),
    Processor(readLine("OPS for 5st: ").toInt))

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

  println("\n\tCOE FOR YOUR VALUES:\n")
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


