package week04

import week04._

class nth {

}

import week04._

object nth {
  def nth[T](n: Int, xs: List[T]): T =
    if(n == 0) xs.head
    else nth(n-1, xs.tail)

  val list = new Cons(1, new Cons(2, new Cons(3, new Nil)))

  nth(2, list)
  nth(-1, list)
}