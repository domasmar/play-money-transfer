package money.dao

/**
  * Object storage witch return newly created object using objProvider
  * if there was no previous take() invocations. Each object should be released when they are no longer necessary
  *
  * Not Thread-safe
  */
class StackedHolder[T](objProvider: () => T) {
  var obj: T = _
  var size: Int = 0

  def take(): T = {
    if (size == 0) {
      obj = objProvider()
    }
    size += 1
    obj
  }

  def releaseAll(): Unit = {
    size = 0
    if (obj != null) {
      clearObj()
    }
  }

  def release(): Unit = {
    size -= 1
    if (!isTaken()) {
      clearObj()
    }
  }

  private def clearObj(): Unit = {
    if (obj != null) {
      obj = null.asInstanceOf[T]
    }
  }

  def isTaken(): Boolean = {
    size > 0
  }
}
