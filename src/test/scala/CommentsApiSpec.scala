import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCode}
import dao.CommentsDao
import models.Comment
import org.scalatest.concurrent.ScalaFutures
import spray.json._

class CommentsApiSpec extends BaseServiceSpec with ScalaFutures{
  "Comments api" should {
    "retrieve comments list" in {
      Get("/v1/users/1/posts/1/comments") ~> routes ~> check {
        responseAs[JsArray] should be(List(testComments.head).toJson)
      }
    }

    "retrieve comment by id" in {
      Get("/v1/users/1/posts/1/comments/1") ~> routes ~> check {
        responseAs[JsObject] should be(testComments.head.toJson)
      }
    }

    "create comment properly" in {
      val newContent = "newContent"
      val requestEntity = HttpEntity(MediaTypes.`application/json`,
        JsObject(
          "postId" -> JsNumber(testPosts.head.id.get),
          "userId" -> JsNumber(testUsers.head.id.get),
          "content" -> JsString(newContent)
        ).toString())
      Post("/v1/comments", requestEntity) ~> routes ~> check {
        response.status should be(StatusCode.int2StatusCode(200))
        Get("/v1/users/1/posts/1/comments") ~> routes ~> check {
          responseAs[Seq[Comment]] should have length 2
        }
      }
    }

    "update comment by id" in {
      val newContent = "UpdatedContent"
      val requestEntity = HttpEntity(MediaTypes.`application/json`,
        JsObject(
          "postId" -> JsNumber(testPosts.head.id.get),
          "userId" -> JsNumber(testUsers.head.id.get),
          "content" -> JsString(newContent)
        ).toString())
      Put("/v1/users/1/posts/1/comments/1", requestEntity) ~> routes ~> check {
        response.status should be(StatusCode.int2StatusCode(200))
        whenReady(CommentsDao.findById(1,1, 1)) { result =>
          result.content should be(newContent)
        }
      }
    }

    "delete comment by id" in {
      Delete("/v1/comments/1") ~> routes ~> check {
        response.status should be(StatusCode.int2StatusCode(200))
        Get("/v1/users/1/posts/1/comments") ~> routes ~> check {
          responseAs[Seq[Comment]] should have length 1
        }
      }
    }
  }
}
