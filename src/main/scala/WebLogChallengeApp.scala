/**
  * Main app to solve the challenge. Entry point to Spark App
  *
  * @author Rohan Somvanshi
  * @date 2019-05-03
  */
object WebLogChallengeApp extends App with Spark {
  if(args.size < 1) {
    println("**********Missing argument***********")
    System.exit(-1)
  }

  val path = args(0)

  // Session window time to identify single session
  // 35 minutes is selected after which number of sessions appear to be constant
  val SESSION_WINDOW_TIME_IN_SECONDS = 2100

  // Passed on to the analyze methods
  implicit val spark = createSparkSession()

  try {
    val elbLogs = LogParser.parseLogs(path)

    // Cache dataset after parsing the logs
    // Reduces time for the next stages as they do not have to reparse the logs
    val sessionEvents = LogAnalyzer.getSessionEvents(elbLogs, SESSION_WINDOW_TIME_IN_SECONDS).cache()

    // Challenge 1: Sessionize the web log by IP
    // Outputs unique (IP, Session)
    Utils.writeOutput(sessionEvents.select("client", "userSessionId").dropDuplicates(), "output/sessions")

    // Cache after calculating duration per session as it is being reused for the next stages
    val sessionDurations = LogAnalyzer.getUserSessionDurations(sessionEvents).cache()

    // Challenge 2: Determine the average session time
    Utils.writeOutput(LogAnalyzer.getAverageSessionTime(sessionDurations), "output/average-session-time")

    // Challenge 3: Determine unique URL visits per session
    Utils.writeOutput(LogAnalyzer.getUniqueURLHitsPerSession(sessionEvents), "output/unique_url-hits_per-session")

    // Challenge 4: Find the most engaged users
    // Shows top 10 users to save writing time
    Utils.writeOutput(LogAnalyzer.getMostEngagedUsers(sessionDurations), "output/most-engaged-users")
  } finally {
    // Explicit stop
    spark.stop()
  }
}
