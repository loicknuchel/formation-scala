package utils

import java.io.{BufferedWriter, File, FileWriter}

import utils.FileClient.Path

import scala.io.Source
import scala.util.{Failure, Try}

object FileClient {
  type Path = String
}

trait FileClient {
  def read(path: Path): Try[String]

  def write(path: Path, content: String): Try[Unit]

  def delete(path: Path): Try[Boolean]

  def listFiles(folder: Path): Try[Seq[String]]

  def createFolder(path: Path): Try[Boolean]

  def deleteFolder(path: Path): Try[Boolean]
}

class RealFileClient() extends FileClient {
  def read(path: Path): Try[String] = Try {
    Source.fromFile(path).mkString
  }

  def write(path: Path, content: String): Try[Unit] = Try {
    val file = new File(path)
    Option(file.getParentFile).map(_.mkdirs()) // do not fail if getParentFile returns 'null'
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(content)
    bw.close()
  }

  def delete(path: Path): Try[Boolean] = {
    val file = new File(path)
    if (file.isFile)
      Try(file.delete())
    else
      Failure(new Exception(s"$path is not a file"))
  }

  def listFiles(folder: Path): Try[Seq[String]] = Try {
    new File(folder).listFiles.toSeq.map(_.getPath)
  }

  def createFolder(path: Path): Try[Boolean] = {
    val folder = new File(path)
    Try(folder.mkdirs())
  }

  def deleteFolder(path: Path): Try[Boolean] = {
    val folder = new File(path)
    if (folder.isDirectory) {
      Try {
        val (files, folders) = folder.listFiles().toSeq.partition(_.isFile)
        files.map(_.getPath).map(delete)
        folders.map(_.getPath).map(deleteFolder)
        folder.delete()
      }
    } else {
      Failure(new Exception(s"$path is not a directory"))
    }
  }
}
