import org.scalatest.{BeforeAndAfterAll, FunSuite}

/**
  * Test cases for the app
  *
  * @author Rohan Somvanshi
  * @date 2019-05-03
  */
class WebLogChallengeTest extends FunSuite with BeforeAndAfterAll with Spark with TestDataFactory {
  implicit val spark = createSparkSession(true)
  import spark.implicits._

  override def afterAll(): Unit = {
    spark.stop()
    super.afterAll()
  }

  test("Log parsing error dataset should have size 0") {
    assert(getRawWebLogs().map(LogParser.parseLine).filter(_.isLeft).map(_.left.get).toDF.count() === 0)
  }

  test("Total number of sessions detected should match the expectation") {

    val sessionEvents = LogAnalyzer.getSessionEvents(getWebLogs().toDS, 2100)

    assert(sessionEvents.select("client", "userSessionId").dropDuplicates().count() === 3)
  }

  test("Session durations should match the expectation") {

    val s = LogAnalyzer.getUserSessionDurations(getSessionEvents().toDS).orderBy("userSessionId")

    val sessionDurations = s.select("sessionDurationInSeconds").as[Long].collect()

    assert(sessionDurations.sameElements(Array(60,120,480)))
  }

  test("Average user session time should match the expectation") {

    val totalSessionTime = LogAnalyzer.getAverageSessionTime(getSessionDurations().toDS).select("avgSessionDurationInSeconds")

    assert(totalSessionTime.collect()(0)(0) === 220.0)
  }

  test("Total user session time should match the expectation") {

    val totalSessionTime = LogAnalyzer.getMostEngagedUsers(getSessionDurations().toDS).select("totalUserSessionTimesInSeconds")

    assert(totalSessionTime.collect()(0)(0) === 660)
  }

  test("Unique URL visits per session should match the expectation") {

    val uniqueURLHitsPerSession = LogAnalyzer.getUniqueURLHitsPerSession(getSessionEvents().toDS)
                                    .orderBy("userSessionId").select("uniqueURLHits").as[Long].collect()

    assert(uniqueURLHitsPerSession.sameElements(Array(2,2,1)))
  }
}
