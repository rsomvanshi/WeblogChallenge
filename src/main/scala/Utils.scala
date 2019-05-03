import org.apache.spark.sql.{DataFrame, SaveMode}

/**
  * Placeholder for helper methods
  *
  * @author Rohan Somvanshi
  * @date 2019-05-03
  */
object Utils {
  def writeOutput(data: DataFrame, path: String): Unit = {
    data.write.mode(SaveMode.Overwrite).csv(path)
  }
}
