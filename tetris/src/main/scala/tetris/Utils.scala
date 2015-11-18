package tetris

import scala.collection.JavaConversions._

import org.apache.spark.{SparkConf, SparkContext}
import scala.io.Source
import java.io.{FileInputStream, File, FileOutputStream, BufferedOutputStream}

import org.apache.commons.io.IOUtils
import org.apache.spark.{SparkConf, SparkContext}
import tetris.tetrominoes.Tetromino

import scala.collection.mutable.Map
import scala.pickling._
import scala.pickling.binary._
import scala.pickling.Defaults._
import scala.pickling.static._
import scala.pickling.Defaults.{pickleOps, unpickleOps}
import scala.pickling.Defaults.{stringPickler, intPickler, refUnpickler, nullPickler}


import scala.collection.mutable.HashMap

/**
 * Created by papacharlie on 10/31/15.
 */
object Utils {

  def executeInSpark[T](fun: SparkContext => T): T = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("tetris")
    val sc = new SparkContext(conf)
    val res = fun(sc)
    sc.stop()
    res
  }

  def clearScreen() = print("\u001b[H\u001b[2J")

  def waitToPrint() = Thread.sleep(250)

  def printStacks(stacks: Iterable[Stack]) = {
    // Assumes foreach runs in order (not actually guaranteed)
    stacks.foreach { stack =>
      clearScreen()
      println(stack)
      waitToPrint()
    }
  }

  implicit class Pipe[T](val t: T) extends AnyVal {
    def |->(fun: T => Unit): T = {
      fun(t)
      t
    }

    def |>[U](fun: T => U): U = fun(t)
  }

  val rankArrayFilename = "ranks/rank_array.arr"
  val rankMapFilename = "maps/rank_map.map"

  def saveArray(filename: String, arr: Array[Int], iteration: Option[Int]=None): Unit = {
    def save(filename: String) = {
      val file = new FileOutputStream(filename)
      IOUtils.write(arr.head.toString, file)
      arr.tail.foreach{ i =>
        IOUtils.write(s",$i", file)
      }
      file.close()
    }
    iteration match {
      case Some(p) => save(s"$filename.$iteration")
      case _ => save(filename)
    }
  }

  def loadArray(filename: String, iteration: Option[Int] = None): Option[Array[Int]] = {
    def load(filename:String) = {
      if (new File(filename).exists()){
        val (seq,_) = IOUtils.toString(new FileInputStream(filename)).foldLeft((IndexedSeq():IndexedSeq[Int],"")){
          case ((seq, s), ',') => (seq :+ Integer.parseInt(s), "")
          case ((seq, s), c) => (seq, s + c)
        }
        Some(seq.toArray)
      } else None
    }
    iteration match {
      case Some(i) => load(s"$filename.$i")
      case _ => load(filename)
    }
  }

  def iterationExists(filename: String, iteration: Int) = new File(s"$filename.$iteration").exists()

  def partExists(filename: String, part: Int) = new File(s"$filename.$part").exists()

  def savePartedHashMap(filename: String, map: HashMap[Int, Seq[Int]], part: Int): Unit = {
    val file = new FileOutputStream(s"$filename.$part")
    map.foreach { case (c, seq) =>
      IOUtils.write(s"$c,${seq.mkString(",")}\n", file)
    }
    file.close()
  }

  def loadHashMap(filename: String, part: Option[Int]): Option[HashMap[Int, Seq[Int]]] = {
    def load(filename: String): Option[HashMap[Int, Seq[Int]]] ={
      val map: HashMap[Int, Seq[Int]] = new HashMap()
      if (new File(s"$filename").exists()) {
        val lines = IOUtils.readLines(new FileInputStream(filename))
        lines.map { line =>
          val nums = line.split(",")
          map += Integer.parseInt(nums.head) -> nums.tail.map(Integer.parseInt)
        }
      }
      if (map.isEmpty) None else Some(map)
    }
    part match {
      case Some(p) => load(s"$filename.$p")
      case _ => load(filename)
    }
  }

  def loadPartedHashMap(filename: String, parts: Int): Option[HashMap[Int, Seq[Int]]] = {
    val map: HashMap[Int, Seq[Int]] = new HashMap()
    def loadMaps(part: Int): Unit = {
      if (new File(s"$filename.$part").exists()) {
        val lines = IOUtils.readLines(new FileInputStream(s"$filename.$part"))
        lines.map { line =>
          val nums = line.split(",")
          map += Integer.parseInt(nums.head) -> nums.tail.map(Integer.parseInt)
        }
        loadMaps(part + 1)
      }
    }
    loadMaps(0)
    if (map.isEmpty) None else Some(map)
  }
}
