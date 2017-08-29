package exercices.s2Collections

import java.text.SimpleDateFormat

import org.scalatest.{FunSpec, Matchers}

class ProjectionsSpec extends FunSpec with Matchers {
  describe("Projections") {
    import Projections._

    val sampleEvents = loadData("sample.json").get
    val fullEvents = loadData("full.json").get
    val monthFormat = new SimpleDateFormat("yyyy-MM")
    val dayFormat = new SimpleDateFormat("yyyy-MM-dd")

    describe("full events") {
      describe("loadData") {
        it("should read event data and return it") {
          sampleEvents.length shouldBe 30
          fullEvents.length shouldBe 432642
        }
      }
      describe("numberOfEvents") {
        it("should count the number of events") {
          numberOfEvents(sampleEvents) shouldBe 30
          numberOfEvents(fullEvents) shouldBe 432642
        }
      }
      describe("registredPlayers") {
        it("should count the number of registered players") {
          registredPlayers(sampleEvents) shouldBe 2
          registredPlayers(fullEvents) shouldBe 948
        }
      }
      describe("registredPlayersPerMonth") {
        it("should count the number of registered players by month") {
          registredPlayersPerMonth(sampleEvents) shouldBe Map("2016-05" -> 2)
          registredPlayersPerMonth(fullEvents) shouldBe Map(
            "2015-03" -> 18,
            "2015-04" -> 19,
            "2015-05" -> 8,
            "2015-06" -> 30,
            "2015-07" -> 26,
            "2015-08" -> 13,
            "2015-09" -> 20,
            "2015-10" -> 14,
            "2015-11" -> 49,
            "2015-12" -> 57,
            "2016-01" -> 49,
            "2016-02" -> 34,
            "2016-03" -> 42,
            "2016-04" -> 105,
            "2016-05" -> 67,
            "2016-06" -> 104,
            "2016-07" -> 54,
            "2016-08" -> 58,
            "2016-09" -> 49,
            "2016-10" -> 49,
            "2016-11" -> 62,
            "2016-12" -> 21
          )
        }
      }
      describe("popularQuizs") {
        it("should count the number of time a quiz game was opened and return the top 10") {
          popularQuizs(sampleEvents) shouldBe Seq()
          popularQuizs(fullEvents) shouldBe Seq(
            ("2125", "A0128", 102),
            ("2089", "BF424", 101),
            ("1976", "DD055", 92),
            ("2600", "32B99", 86),
            ("2528", "6BE93", 82),
            ("2666", "1102A", 77),
            ("3217", "52EDC", 64),
            ("2827", "09B69", 59),
            ("3334", "33131", 55),
            ("3354", "D2CD3", 54)
          )
        }
      }
      describe("inactivePlayers") {
        it("should count the number of joined games and return top 10 of inactive players for the month") {
          inactivePlayers(sampleEvents, monthFormat.parse("2016-04")) shouldBe Seq()
          inactivePlayers(fullEvents, monthFormat.parse("2016-04")) shouldBe Seq(
            ("21", "3C59D C048", 4),
            ("733", "6C297 93A1", 4),
            ("699", "AFD48 3671", 4),
            ("867", "EDE7E 2B6D", 4),
            ("261", "B1A59 B315", 5),
            ("283", "0F49C 89D1", 6),
            ("313", "158F3 069A", 6),
            ("463", "428FC A9BC", 6),
            ("95", "812B4 BA28", 6),
            ("325", "89F0F D5C9", 6)
          )
        }
      }
      describe("activePlayers") {
        it("should count the number of joined games in the last 7 days and return top 10 of active players") {
          activePlayers(sampleEvents, monthFormat.parse("2016-04")) shouldBe Seq()
          activePlayers(fullEvents, dayFormat.parse("2016-04-20")) shouldBe Seq(
            ("1895", "059FD CD96", 18),
            ("1863", "09FB0 5DD4", 18),
            ("1891", "13168 E6A2", 18),
            ("1879", "44A2E 0804", 18),
            ("1865", "69D1F C78D", 18),
            ("1875", "7E9E3 46DC", 18),
            ("1867", "A19AC D7D2", 18),
            ("1881", "B4568 DF26", 18),
            ("1877", "F31B2 0466", 18),
            ("879", "D516B 1367", 14)
          )
        }
      }
    }
  }
}
