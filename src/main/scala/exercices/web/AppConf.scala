package exercices.web

import java.nio.file.Paths

import pureconfig.error.ConfigReaderFailures

import scala.util.{Failure, Success, Try}

case class AppConf(hello: HelloConf)

case class HelloConf(response: String)

object AppConf {
  def load(): Try[AppConf] =
    pureconfig
      .loadConfig[AppConf](Paths.get("src/main/resources/web/application.conf"))
      .fold(
        failures => Failure(new Exception(format(failures))),
        config => Success(config)
      )

  private def format(failures: ConfigReaderFailures): String =
    "Configuration Errors :\n" +
      failures.toList.map(f => s" - for file ${f.location.map(_.description).getOrElse("unknown")} error: ${f.description}").mkString("\n")
}
