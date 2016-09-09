import akka.http.scaladsl.model.{StatusCode, MediaTypes, HttpEntity}
import dao.PostsDao
import models.{Post, User}
import org.scalatest.concurrent.ScalaFutures
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

class PostsApiSpec extends BaseServiceSpec with ScalaFutures{
  "Users posts api" should {
    "retrieve users posts list" in {
      Get("/v1/users/1/posts") ~> routes ~> check {
        responseAs[JsArray] should be(List(testPosts.head).toJson)
      }
    }

    "retrieve post by id" in {
      Get("/v1/users/1/posts/1") ~> routes ~> check {
        responseAs[JsObject] should be(testPosts.head.toJson)
      }
    }

    "create post properly" in {
      val newTitle = "newTitle"
      val newContent = "newContent"
      val requestEntity = HttpEntity(MediaTypes.`application/json`,
        JsObject(
          "title" -> JsString(newTitle),
          "userId" -> JsNumber(testUsers.head.id.get),
          "content" -> JsString(newContent)
        ).toString())
      Post("/v1/users/1/posts", requestEntity) ~> routes ~> check {
        response.status should be(StatusCode.int2StatusCode(200))
        Get("/v1/users/1/posts") ~> routes ~> check {
          responseAs[Seq[Post]] should have length 2
        }
      }
    }

    "update post by id" in {
      val newTitle = "UpdatedTitle"
      val requestEntity = HttpEntity(MediaTypes.`application/json`,
        JsObject(
          "title" -> JsString(newTitle),
          "userId" -> JsNumber(testUsers.head.id.get),
          "content" -> JsString(testPosts.head.content)
        ).toString())
      Put("/v1/users/1/posts/1", requestEntity) ~> routes ~> check {
        response.status should be(StatusCode.int2StatusCode(200))
        whenReady(PostsDao.findByUserIdAndId(1,1)) { result =>
          result.title should be(newTitle)
        }
      }
    }

    "delete post by id" in {
      Delete("/v1/users/1/posts/1") ~> routes ~> check {
        response.status should be(StatusCode.int2StatusCode(200))
        Get("/v1/users/1/posts") ~> routes ~> check {
          responseAs[Seq[Post]] should have length 1
        }
      }
    }
  }
}
