package exercices.web.controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import exercices.web.HelloConf

class MainController(conf: HelloConf) extends Controller {
  get("/hello") { _: Request =>
    response.ok(conf.response).toFuture
  }
}
