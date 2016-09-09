import akka.http.scaladsl.model.{StatusCode, MediaTypes, HttpEntity}
import dao.UsersDao
import models.User
import org.scalatest.concurrent.ScalaFutures
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

class UsersApiSpec extends BaseServiceSpec with ScalaFutures{
  "Users service" should {
    "retrieve users list" in {
      Get("/v1/users") ~> routes ~> check {
        responseAs[JsArray] should be(testUsers.toJson)
      }
    }

    "retrieve user by id" in {
      Get("/v1/users/1") ~> routes ~> check {
        responseAs[JsObject] should be(testUsers.head.toJson)
      }
    }

    "create user properly" in {
      val newUsername = "UpdatedUsername"
      val requestEntity = HttpEntity(MediaTypes.`application/json`,
        JsObject(
          "userName" -> JsString(newUsername),
          "password" -> JsString(testUsers.head.password),
          "age" -> JsNumber(testUsers.head.age),
          "gender" -> JsNumber(testUsers.head.gender)
        ).toString())
      Post("/v1/users", requestEntity) ~> routes ~> check {
        response.status should be(StatusCode.int2StatusCode(200))
        Get("/v1/users") ~> routes ~> check {
          responseAs[Seq[User]] should have length 4
        }
      }
    }

    "update user by id" in {
      val newUsername = "UpdatedUsername"
      val requestEntity = HttpEntity(MediaTypes.`application/json`,
        JsObject(
          "userName" -> JsString(newUsername),
          "password" -> JsString(testUsers.head.password),
          "age" -> JsNumber(testUsers.head.age),
          "gender" -> JsNumber(testUsers.head.gender)
        ).toString())
      Put("/v1/users/1", requestEntity) ~> routes ~> check {
        response.status should be(StatusCode.int2StatusCode(200))
        whenReady(UsersDao.findById(1)) { result =>
          result.userName should be(newUsername)
        }
      }
    }

    "delete user by id" in {
      Delete("/v1/users/1") ~> routes ~> check {
        response.status should be(StatusCode.int2StatusCode(200))
        Get("/v1/users") ~> routes ~> check {
          responseAs[Seq[User]] should have length 3
        }
      }
    }
  }
}
