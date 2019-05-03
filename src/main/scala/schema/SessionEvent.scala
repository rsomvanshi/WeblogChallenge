package schema

/**
  * Schema for each event in the session
  *
  * @author Rohan Somvanshi
  * @date 2019-05-03
  */
case class SessionEvent(
  client: String,
  url: String,
  isNewSession: Int,
  eventTimeInSeconds: Long,
  userSessionId: Long
)

