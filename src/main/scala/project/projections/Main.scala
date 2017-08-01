package project.projections

import project.projections.dao.LocalClient

object Main {
  def main(args: Array[String]): Unit = {
    val res = LocalClient.getEvents("src/main/resources/projections/sample.json").get
    println(res)
  }
}
