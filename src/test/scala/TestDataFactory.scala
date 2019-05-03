import schema.{SessionDuration, SessionEvent, WebLog}

/**
  * Factory for providing data to test suite
  *
  * @author Rohan Somvanshi
  * @date 2019-05-03
  */
trait TestDataFactory {

  def getWebLogs(): Seq[WebLog] = {
    Seq(
      WebLog("2015-07-22T02:41:38.401430Z","127.0.0.1", "/api/abc"),
      WebLog("2015-07-22T02:42:38.401430Z","127.0.0.1", "/api/xyz"),
      WebLog("2015-07-22T03:41:38.401430Z","127.0.0.1", "/api/pqr"),
      WebLog("2015-07-22T03:43:38.401430Z","127.0.0.1", "/api/abc"),
      WebLog("2015-07-22T04:21:38.401430Z","127.0.0.1", "/api/xyz"),
      WebLog("2015-07-22T04:29:38.401430Z","127.0.0.1", "/api/xyz")
    )
  }

  def getSessionEvents(): Seq[SessionEvent] = {
    Seq(
      SessionEvent("127.0.0.1", "/api/abc", 1, 1437532898, 1),
      SessionEvent("127.0.0.1", "/api/xyz", 0, 1437532958, 1),
      SessionEvent("127.0.0.1", "/api/pqr", 1, 1437536498, 2),
      SessionEvent("127.0.0.1", "/api/abc", 0, 1437536618, 2),
      SessionEvent("127.0.0.1", "/api/xyz", 1, 1437538898, 3),
      SessionEvent("127.0.0.1", "/api/xyz", 0, 1437539378, 3)
    )
  }

  def getSessionDurations(): Seq[SessionDuration] = {
    Seq(
      SessionDuration("127.0.0.1", 1, 60),
      SessionDuration("127.0.0.1", 2, 120),
      SessionDuration("127.0.0.1", 3, 480)
    )
  }

  def getRawWebLogs(): Seq[String] = {
    Seq(
      """2015-07-22T09:00:28.019143Z marketpalce-shop 123.242.248.130:54635 10.0.6.158:80 0.000022 0.026109 0.00002 200 200 0 699 "GET https://paytm.com:443/shop/authresponse?code=f2405b05-e2ee-4b0d-8f6a-9fed0fcfe2e0&state=null HTTP/1.1" "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36" ECDHE-RSA-AES128-GCM-SHA256 TLSv1.2""",
      """2015-07-22T09:00:28.425643Z marketpalce-shop 14.139.217.165:59056 10.0.6.158:80 0.000029 0.000198 0.000015 301 301 0 178 "GET https://www.paytm.com:443/ HTTP/1.1" "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36" ECDHE-RSA-AES128-GCM-SHA256 TLSv1.2""",
      """2015-07-22T09:00:28.425643Z marketpalce-shop 14.139.217.165:59056 10.0.6.158:80 0.000029 0.000198 0.000015 301 301 0 178 "GET http://www.paytm.com:80/ HTTP/1.1" "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36" ECDHE-RSA-AES128-GCM-SHA256 TLSv1.2"""
    )
  }

}
