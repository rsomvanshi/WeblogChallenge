package schema

/**
  * Schema for each log line
  *
  * @author Rohan Somvanshi
  * @date 2019-05-03
  */
case class WebLog(
  timestamp: String,
  client: String,
  url: String
)
