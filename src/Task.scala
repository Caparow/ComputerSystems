/**
  * Created by cypress on 27.02.17.
  */

import scala.collection.mutable.ListBuffer

object Task{
  case class Task(execTime: Int, appropriateProcessors: List[Int])

  def createTask(s: Int, e: Int): Task = {
    val random = scala.util.Random
    var pr: ListBuffer[Int] = ListBuffer()
    var n = random.nextInt(4)
    for (i <- 1 to n){
      pr += random.nextInt(4)
    }
    Task(s+random.nextInt(e), pr.toList)
  }

  def createTaskList(n: Int, sRangeOfDif: Int, eRangeOfDif: Int): ListBuffer[Task] ={
    var taskList: ListBuffer[Task] = ListBuffer()
    for (i <- 0 until n)
      taskList += createTask(sRangeOfDif, eRangeOfDif)
    taskList
  }
}

