/**
  * Created by Cypress on 01.03.2017.
  */

import Processor.Processor
import Task._
import scala.collection.mutable.ListBuffer

package Scheduler {
  case class Scheduler(processorsList: List[Processor],
                       probability: Int,
                       sRangeOfDif: Int,
                       eRangeOfDif: Int,
                       startingNumOfTasks: Int,
                       timeForCheck: Int) {

    private val random = scala.util.Random
    private var taskList: ListBuffer[Task] = ListBuffer()
    private var totalOpPerMS = 0
    private var tasksReleased = 0
    private var operationsReleased = 0
    private var taskProc = ListBuffer[(Int, Task)]()

    processorsList.foreach(totalOpPerMS += _.operationsPerMS)
    processorsList.foreach(_.state = false)

    private def getProcForTask(task: Task): Int = {
      var minTimeExec: Double = 10000000
      var procNeeded: Int = -1
      for (proc <- processorsList) {
        if (task.appropriateProcessors.contains(processorsList.indexOf(proc))){
          if (minTimeExec > task.execTime.toDouble / proc.operationsPerMS) {
            minTimeExec = task.execTime.toDouble / proc.operationsPerMS
            procNeeded = processorsList.indexOf(proc)
          }
        }
      }
      procNeeded
    }

    private def getProcForTasks(tasks: ListBuffer[Task]): ListBuffer[(Int, Task)] = {
      var newTasksMap: ListBuffer[(Int, Task)] = ListBuffer()
      for (task <- tasks){
        var t = getProcForTask(task)
        if (t != -1)
          newTasksMap += Tuple2(t, task)
      }
      newTasksMap
    }

    private def getBestProc: Int = {
      var maxOp = 0
      var bestProc = 0
      for (proc <- processorsList)
        if (maxOp < proc.operationsPerMS) {
          maxOp = proc.operationsPerMS
          bestProc = processorsList.indexOf(proc)
        }
      bestProc
    }

    private def getWorstProc: Int = {
      var minOp = 10000000
      var worstProc = 0
      for (proc <- processorsList)
        if (minOp > proc.operationsPerMS) {
          minOp = proc.operationsPerMS
          worstProc = processorsList.indexOf(proc)
        }
      worstProc
    }

    private def getProcessorsState: Boolean = {
      var state = false
      for (proc <- processorsList)
        if (!proc.state)
          state = true
      state
    }

    private def execOneCycle(schedulerProc: Int): Int = {
      var opDone: Int = 0

      if (0 <= schedulerProc && schedulerProc <= processorsList.length){
        taskProc = getProcForTasks(taskList)
        processorsList(schedulerProc).state = true
      }

      if(getProcessorsState){
        for (task <-taskProc if taskProc.nonEmpty){
          if (!processorsList(task._1).state){
            processorsList(task._1).setTask(task._2)
            taskProc.remove(taskProc.indexOf(task))
            taskList.remove(taskList.indexOf(task._2))
            tasksReleased += 1
          }
        }
      }

      for (proc <- processorsList if processorsList.indexOf(proc) != schedulerProc){
        if (proc.state){
          proc.executeTask()
          opDone += proc.operationsPerMS
        }
      }

      opDone
    }

    private def process(mode: Int, workTime: Int = 20, schedulerTime: Int = 4): Double = {
      var time: Int = 0
      var operationsDone = 0
      tasksReleased = 0
      taskList = createTaskList(startingNumOfTasks, sRangeOfDif, eRangeOfDif)

      if (mode == 0){
        var worstProc: Int = getWorstProc

        while (time < timeForCheck){
          if(random.nextInt(100) < probability)
            taskList += createTask(sRangeOfDif, eRangeOfDif)
          operationsDone += execOneCycle(worstProc)
          time += 1
        }
        operationsReleased = operationsDone
        (operationsDone.toDouble / time) / (totalOpPerMS - processorsList(worstProc).operationsPerMS)
      } else {
        var bestProc: Int = getBestProc

        while (time < timeForCheck){
          if(random.nextInt(100) < probability)
            taskList += createTask(sRangeOfDif, eRangeOfDif)

          if (time % workTime < schedulerTime) operationsDone += execOneCycle(bestProc)
          else operationsDone += execOneCycle(-1)

          time += 1
        }

        operationsReleased = operationsDone
        (operationsDone.toDouble / time) / (totalOpPerMS -
          processorsList(bestProc).operationsPerMS*(schedulerTime.toDouble / (workTime+schedulerTime)))
      }
    }

    def testCOEonWorstProc: Double = {
      var COE: Double = 0
      var tasks: Int = 0
      var op: Int = 0
      for (_ <- 1 to 5){
        COE += process(0)
        tasks += tasksReleased
        op += operationsReleased
      }
      println("COE of the Scheduler on the worst processor: " + (COE / 5).toString)
      println("Tasks released: " + (tasks/5).toString)
      println("Operations released: " + (op/5).toString)
      COE / 10
    }

    def testCOEonBestProc(workTime: Int): Double = {
      var COE: Double = 0
      var tasks: Int = 0
      var op: Int = 0
      for (_ <- 1 to 5) {
        COE += process(1, workTime, 4)
        tasks += tasksReleased
        op += operationsReleased
      }
      println("COE of the Scheduler on the best processor ("+ workTime.toString+", 4): " + (COE / 5).toString)
      println("Tasks released: " + (tasks/5).toString)
      println("Operations released: " + (op/5).toString)
      COE / 10
    }
  }
}
