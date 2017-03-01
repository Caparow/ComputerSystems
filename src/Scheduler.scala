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
                      startingNumOfTasks: Int) {

    private val random = scala.util.Random
    private var taskList: ListBuffer[Task] = ListBuffer()
    private var totalOpPerMS = 0
    private var maxProc = 0

    for (proc <- processorsList)
      if (maxProc < proc.operationsPerMS)
        maxProc = proc.operationsPerMS

    processorsList.foreach(totalOpPerMS += _.operationsPerMS)
    processorsList.foreach(_.state = false)

    private def getProcForTask(task: Task, max: Int): Int = {
      var minTimeExec: Double = 10000000
      var procNeeded: Int = -1
      var coef = max/maxProc
      for (proc <- processorsList) {
        if (task.appropriateProcessors.contains(processorsList.indexOf(proc))){
          if (task.execTime/proc.operationsPerMS < coef)
            procNeeded = processorsList.indexOf(proc)
          }
      }
      procNeeded
    }

    private def getProcForTasks(tasks: ListBuffer[Task]): ListBuffer[(Int, Task)] = {
      var max = 0

      for (task <- taskList)
        if (max < task.execTime)
          max = task.execTime

      var newTasksMap: ListBuffer[(Int, Task)] = ListBuffer()
      for (task <- tasks){
        var proc = getProcForTask(task, max)
        if (proc != -1)
          newTasksMap += Tuple2(getProcForTask(task, max), task)
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
      val taskProc = getProcForTasks(taskList)

      if (0 <= schedulerProc && schedulerProc < processorsList.length)
        processorsList(schedulerProc).state = true

      if(getProcessorsState){
        for (task <-taskProc){
          if (!processorsList(task._1).state){
            processorsList(task._1).setTask(task._2)
            taskProc.remove(taskProc.indexOf(task))
            taskList.remove(taskList.indexOf(task._2))
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
      if (mode == 0){
        var time: Int = 0
        var worstProc: Int = getWorstProc
        var operationsDone = 0
        taskList = createTaskList(startingNumOfTasks, sRangeOfDif, eRangeOfDif)
        while (time < 1000){
          if(random.nextInt(100) < probability)
            taskList += createTask(sRangeOfDif, eRangeOfDif)
          operationsDone += execOneCycle(worstProc)
          time += 1
        }
        (operationsDone.toDouble / time) / (totalOpPerMS - processorsList(worstProc).operationsPerMS)
      } else {
        var time: Int = 0
        var bestProc: Int = getBestProc
        var operationsDone = 0
        taskList = createTaskList(startingNumOfTasks, sRangeOfDif, eRangeOfDif)
        while (time < 1000){
          if(random.nextInt(100) < probability)
            taskList += createTask(sRangeOfDif, eRangeOfDif)

          if (time % workTime < schedulerTime) operationsDone += execOneCycle(-1)
          else operationsDone += execOneCycle(-1)

          time += 1
        }
        (operationsDone.toDouble / time) / (totalOpPerMS -
          processorsList(bestProc).operationsPerMS*(schedulerTime.toDouble / (workTime+schedulerTime)))
      }
    }

    def testCOEonWorstProc: Double = {
      var COE: Double = 0
      for (_ <- 1 to 5)
        COE += process(0)
      COE / 10
    }

    def testCOEonBestProc: Double = {
      var COE: Double = 0
      for (_ <- 1 to 5)
        COE += process(1, 20, 4)
      COE / 10
    }
  }
}
