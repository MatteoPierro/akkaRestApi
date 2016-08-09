package utils

import slick.driver.JdbcProfile

trait DatabaseConfig extends Config{
  import slick.backend.DatabaseConfig

  val slickConfig = DatabaseConfig.forConfig[JdbcProfile]("slick")
  val driver = slickConfig.driver

  def db = slickConfig.db

  implicit val session = db.createSession()
}