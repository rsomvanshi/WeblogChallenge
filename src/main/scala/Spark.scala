import org.apache.spark.sql.SparkSession

/**
  * Reusable Spark session connection
  *
  * @author Rohan Somvanshi
  * @date 2019-05-03
  */
trait Spark {
  // Create spark session
  def createSparkSession(local: Boolean=false): SparkSession = {
    val builder = SparkSession.builder().appName("WebLogSessionizer")

    if(local) builder.master("local[*]").getOrCreate()
    else builder.getOrCreate()
  }
}
