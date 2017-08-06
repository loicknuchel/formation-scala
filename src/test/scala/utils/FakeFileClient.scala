package utils

import java.io.FileNotFoundException

import utils.FileClient.Path

import scala.util.{Failure, Success, Try}

class FakeFileClient extends FileClient {
  var fs: Map[String, Try[String]] = Map()

  def init(): Unit = {
    fs = Map()
  }

  override def read(path: Path): Try[String] = {
    fs.getOrElse(path, Failure(new FileNotFoundException(path)))
  }

  override def write(path: Path, content: String): Try[Unit] = Try {
    fs = fs ++ Map(path -> Success(content))
  }

  override def delete(path: Path): Try[Boolean] = Try {
    fs = fs - path
    true
  }

  override def listFiles(folder: Path): Try[Seq[String]] = {
    Try(fs.keys.toSeq)
  }

  override def createFolder(path: Path): Try[Boolean] = Try {
    true
  }

  override def deleteFolder(path: Path): Try[Boolean] = Try {
    fs = fs.filterNot(_._1.startsWith(path))
    true
  }
}
