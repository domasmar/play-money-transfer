package unit.money.dao

import money.dao.StackedHolder
import org.mockito.Mockito.{times => mockitoTimes, verify => mockitoVerify}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

class StackedHolderTest extends FlatSpec with Matchers with MockitoSugar {

  trait StackedHolderTestContext {
    lazy val objProvider: () => Object = () => mock[Object]
    lazy val objStackedHolder = new StackedHolder(objProvider)
  }

  behavior of "ConnectionHolder"

  it should "create new object using objProvider when take is called " in new StackedHolderTestContext {
    override lazy val objProvider: () => Object = mock[() => Object]

    objStackedHolder.take()

    mockitoVerify(objProvider, mockitoTimes(1)).apply()
  }

  it should "create non null obj " in new StackedHolderTestContext {
    val obj = objStackedHolder.take()
    obj should not be null
  }


  it should "create new object when previous object was released" in new StackedHolderTestContext {
    val objBeforeRelease = objStackedHolder.take()
    objStackedHolder.release()
    val objAfterRelease = objStackedHolder.take()

    objBeforeRelease should not be objAfterRelease
  }

  behavior of "ConnectionHolder.isTaken()"

  it should "return true if object was taken" in new StackedHolderTestContext {
    objStackedHolder.take()
    objStackedHolder.isTaken() shouldBe true
  }

  it should "return false if object was taken and then returned" in new StackedHolderTestContext {
    objStackedHolder.take()
    objStackedHolder.release()
    objStackedHolder.isTaken() shouldBe false
  }


}
