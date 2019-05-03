package schema

/**
  * Schema for each ip session duration
  *
  * @author Rohan Somvanshi
  * @date 2019-05-03
  */
case class SessionDuration (
  client: String,
  userSessionId: Long,
  sessionDurationInSeconds: Long
)
