import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions._
import schema.{SessionDuration, SessionEvent, WebLog}

/**
  * Provides methods to analyze logs
  *
  * @author Rohan Somvanshi
  * @date 2019-05-03
  */
object LogAnalyzer {

  // Inspired by a great blog: https://mode.com/blog/finding-user-sessions-sql
  // A way to sessionize logs without joins
  def getSessionEvents(elbLogs: Dataset[WebLog], sessionWindowTimeInSec: Int)(implicit spark: SparkSession): Dataset[SessionEvent] = {
    import spark.implicits._
    val userEventsOverTime = elbLogs
      .withColumn("eventTime", to_timestamp($"timestamp"))
      .select("client", "url", "eventTime")
      .withColumn("lastEventTime",
        lag("eventTime", 1)
          .over(Window.partitionBy("client").orderBy("eventTime")))

    val userSessionTagged = userEventsOverTime
      .select($"client",
        unix_timestamp($"eventTime").as("eventTimeInSeconds"),
        $"url",
        unix_timestamp($"lastEventTime").as("lastEventTimeInSeconds"))
      .withColumn("isNewSession",
        when($"eventTimeInSeconds".minus($"lastEventTimeInSeconds") >  sessionWindowTimeInSec || $"lastEventTimeInSeconds".isNull, 1)
          .otherwise(0))

    userSessionTagged
      .select("client", "url", "isNewSession", "eventTimeInSeconds")
      .withColumn("userSessionId",
        sum("isNewSession").over(Window.partitionBy("client").orderBy("eventTimeInSeconds")))
      .as[SessionEvent]
  }

  def getAverageSessionTime(sessionDurations: Dataset[SessionDuration]): DataFrame = {
    sessionDurations
      .select(avg("sessionDurationInSeconds").as("avgSessionDurationInSeconds"))
  }

  def getUniqueURLHitsPerSession(sessionEvents: Dataset[SessionEvent]): DataFrame = {
    sessionEvents
      .groupBy("client", "userSessionId")
      .agg(countDistinct("url").as("uniqueURLHits"))
  }

  def getMostEngagedUsers(sessionDurations: Dataset[SessionDuration]): DataFrame = {
    sessionDurations
      .groupBy("client")
      .agg(sum("sessionDurationInSeconds").as("totalUserSessionTimesInSeconds"))
      .orderBy(desc("totalUserSessionTimesInSeconds"))
      .limit(10)
  }

  // Filter user sessions with length 0. Helps in getting real session time average
  // Length 0 means either log is truncated or these are unwanted favicon requests
  def getUserSessionDurations (sessionEvents: Dataset[SessionEvent])(implicit spark: SparkSession): Dataset[SessionDuration] = {
    import spark.implicits._
    sessionEvents
      .groupBy("client", "userSessionId")
      .agg((max("eventTimeInSeconds") - min("eventTimeInSeconds")).as("sessionDurationInSeconds"))
      .filter($"sessionDurationInSeconds" =!= 0)
      .as[SessionDuration]
  }
}
