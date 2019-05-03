import org.apache.spark.sql.{Dataset, SparkSession}
import schema.WebLog

/**
  * Object used for parsing the log file and converting it to a structured dataset
  *
  * @author Rohan Somvanshi
  * @date 2019-05-03
  */
object LogParser {
  private val REGEX_PATTERN = """([^ ]*) ([^ ]*) ([^ ]*):([0-9]*) ([^ ]*)[:-]([0-9]*) ([-.0-9]*) ([-.0-9]*) ([-.0-9]*) (|[-0-9]*) (-|[-0-9]*) ([-0-9]*) ([-0-9]*) \"([^ ]*) h([^ ]*):([-0-9]*)([^ ]*) (- |[^ ]*)\" \"([^\"]*)\" ([A-Z0-9-]+) ([A-Za-z0-9.-]*)""".r

  private def parseRequest(request: String): Option[String] = {
    // Get API name and ignore query string
    val splitIndex = request.indexOf("?")
    if (splitIndex > -1) {
      return Some(request.slice(0, splitIndex))
    }
    return None
  }

  def parseLine(line: String): Either[String, WebLog] = {
    line match {
      // Match only required contents.
      case REGEX_PATTERN(timestamp,_,client,_,_,_,_,_,_,_,_,_,_,_,_,_,request,_,_,_,_) =>
        val url = parseRequest(request)
        Right(WebLog(timestamp, client, url.getOrElse(request)))
      case _ => Left(line)
    }
  }

  def parseLogs(inputFilePath: String)(implicit spark: SparkSession): Dataset[WebLog] = {
    import spark.implicits._
    spark.sparkContext.textFile(inputFilePath)
      .map(parseLine _)
      .filter(_.isRight).map(_.right.get)
      .toDS
  }
}
