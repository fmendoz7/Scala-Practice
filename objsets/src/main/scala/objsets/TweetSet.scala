package objsets

import TweetReader._

/**
 * A class to represent tweets.
 */
class Tweet(val user: String, val text: String, val retweets: Int) {
  override def toString: String =
    "User: " + user + "\n" +
    "Text: " + text + " [" + retweets + "]"
}

/**
 * This represents a set of objects of type `Tweet` in the form of a binary search
 * tree. Every branch in the tree has two children (two `TweetSet`s). There is an
 * invariant which always holds: for every branch `b`, all elements in the left
 * subtree are smaller than the tweet at `b`. The elements in the right subtree are
 * larger.
 *
 * Note that the above structure requires us to be able to compare two tweets (we
 * need to be able to say which of two tweets is larger, or if they are equal). In
 * this implementation, the equality / order of tweets is based on the tweet's text
 * (see `def incl`). Hence, a `TweetSet` could not contain two tweets with the same
 * text from different users.
 *
 *
 * The advantage of representing sets as binary search trees is that the elements
 * of the set can be found quickly. If you want to learn more you can take a look
 * at the Wikipedia page [1], but this is not necessary in order to solve this
 * assignment.
 *
 * [1] http://en.wikipedia.org/wiki/Binary_search_tree
 */
abstract class TweetSet extends TweetSetInterface {

  /**
   * This method takes a predicate and returns a subset of all the elements
   * in the original set for which the predicate is true.
   *
   * Question: Can we implement this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
    //filter and filterAcc method logic will be DEFAULTS for TweetSet

  def filter(p: Tweet => Boolean): TweetSet = this.filterAcc(p, new Empty)
    //I was WRONG. Apparently you can have 'DEFAULT' method logic
    //Parameter is a function taking in a tweet, returning a boolean
    //Examples of viable filter parameters:
      //1: # of retweets

  /**
   * This is a helper method for `filter` that propagates the accumulated tweets.
   */
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet
    //In the filter helpee method, you want to start with nothing for acc (EMPTY TweetSet)
    //p, the filtering logic, is left as p. Defining abstract method
      //Method logic should be up to the operator

  /**
   * Returns a new `TweetSet` that is the union of `TweetSet`s `this` and `that`.
   *
   * Question: Should we implement this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
  def union(that: TweetSet): TweetSet
    //Comparing THIS and THAT
    //Want to COMBINE both elements, both of type TweetSet
    //Want to overload union within Empty, have different implementation in NonEmpty
      //Iterate through recursive calls, utilizing this and that

  /**
   * Returns the tweet from this set which has the greatest retweet count.
   *
   * Calling `mostRetweeted` on an empty set should throw an exception of
   * type `java.util.NoSuchElementException`.
   *
   * Question: Should we implment this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
  def mostRetweeted: Tweet
    //Use inbuilt Scala .max method on retweet attribute
    //Have to overload for NonEmpty and Empty, obviously

  //Helper method to recursively iterate
  def mostRetweetedAcc(acc: Tweet): Tweet

  /**
   * Returns a list containing all tweets of this set, sorted by retweet count
   * in descending order. In other words, the head of the resulting list should
   * have the highest retweet count.
   *
   * Hint: the method `remove` on TweetSet will be very useful.
   * Question: Should we implement this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
  def descendingByRetweet: TweetList = ???
    //Output instance of class TweetList, tweets sorted high-low
    //Functional Programming Languages have immutable data structures
      //Hence, we are instantiating a NEW sorted TweetSet,
    //PROCESS
      //1. First, find highest tweet (mostRetweeted helper method)
      //2. Copy said element from original set
      //3. Put in new TweetList, recursively iterate by including element (.incl(elem))

  /**
   * The following methods are already implemented
   */

  /**
   * Returns a new `TweetSet` which contains all elements of this set, and the
   * the new element `tweet` in case it does not already exist in this set.
   *
   * If `this.contains(tweet)`, the current set is returned.
   */
  def incl(tweet: Tweet): TweetSet

  /**
   * Returns a new `TweetSet` which excludes `tweet`.
   */
  def remove(tweet: Tweet): TweetSet

  /**
   * Tests if `tweet` exists in this `TweetSet`.
   */
  def contains(tweet: Tweet): Boolean

  /**
   * This method takes a function and applies it to every element in the set.
   */
  def foreach(f: Tweet => Unit): Unit
}
//------------------------------------------------------------------------------------------
//EMPTY CLASS: Leaf Nodes in Binary Tree of Tweets
class Empty extends TweetSet {
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = ???

  /** The following methods are already implemented */

  def contains(tweet: Tweet): Boolean = false

  def incl(tweet: Tweet): TweetSet = new NonEmpty(tweet, new Empty, new Empty)

  def remove(tweet: Tweet): TweetSet = this

  def foreach(f: Tweet => Unit): Unit = ()

  def union(that: TweetSet): TweetSet = that

  def mostRetweeted: Tweet = throw new java.util.NoSuchElementException()

  def mostRetweetedAcc(acc: Tweet): Tweet = acc
}
//-----------------------------------------------------------------------------------------
//NONEMPTY CLASS: Root Nodes and All Others (except leaves) In Binary Tree of Tweets
class NonEmpty(elem: Tweet, left: TweetSet, right: TweetSet) extends TweetSet {

  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = ???

  /** The following methods are already implemented */

  //Recursively goes down binary tree from root to check if tweet exists
  def contains(x: Tweet): Boolean =
    if (x.text < elem.text) left.contains(x)
    else if (elem.text < x.text) right.contains(x)
    else true

  //Recursively goes down binary tree from root to position tweet properly
  def incl(x: Tweet): TweetSet = {
    if (x.text < elem.text) new NonEmpty(elem, left.incl(x), right)
    else if (elem.text < x.text) new NonEmpty(elem, left, right.incl(x))
    else this
  }

  def remove(tw: Tweet): TweetSet =
    if (tw.text < elem.text) new NonEmpty(elem, left.remove(tw), right)
    else if (elem.text < tw.text) new NonEmpty(elem, left, right.remove(tw))
    else left.union(right)
      //How does this snippet preserve all nodes above the one in question
      //Review how binary tree corrects when element is removed

  def foreach(f: Tweet => Unit): Unit = {
    f(elem)
    left.foreach(f)
    right.foreach(f)
  }

  def union(that: TweetSet): TweetSet = {
    left.union(right.union(that)).incl(elem)
      //First, union *this* left and *this* right
      //Then, union the *this* right with *THAT* entire tweet
      //MULTIPLE recursive calls
        //Increment mechanism is .incl(elem)
        // ..as it will INCLUDE ELEMENT of THAT tweetset to NEW BINARY TREE
        //Remember, we are NOT modifying the old binary tree
  }

  def mostRetweeted: Tweet = mostRetweetedAcc(elem)
    //Returns most retweeted tweet

  def mostRetweetedAcc(acc: Tweet): Tweet =
    left.mostRetweetedAcc(right.mostRetweetedAcc(if (elem.retweets > acc.retweets) elem else acc))
      //OPTION #1: elem
      //OPTION #2: acc
}
//-----------------------------------------------------------------------------------------
trait TweetList {
  def head: Tweet
  def tail: TweetList
  def isEmpty: Boolean
  def foreach(f: Tweet => Unit): Unit =
    if (!isEmpty) {
      f(head)
      tail.foreach(f)
    }
}
//-----------------------------------------------------------------------------------------
object Nil extends TweetList {
  def head = throw new java.util.NoSuchElementException("head of EmptyList")
  def tail = throw new java.util.NoSuchElementException("tail of EmptyList")
  def isEmpty = true
}
//-----------------------------------------------------------------------------------------
class Cons(val head: Tweet, val tail: TweetList) extends TweetList {
  def isEmpty = false
}
//-----------------------------------------------------------------------------------------
object GoogleVsApple {
  val google = List("android", "Android", "galaxy", "Galaxy", "nexus", "Nexus")
  val apple = List("ios", "iOS", "iphone", "iPhone", "ipad", "iPad")

  lazy val googleTweets: TweetSet = TweetReader.allTweets.filter(t => google.exists(token => t.text.contains(token)))
  lazy val appleTweets: TweetSet = TweetReader.allTweets.filter(t => apple.exists(token => t.text.contains(token)))

  /**
   * A list of all tweets mentioning a keyword from either apple or google,
   * sorted by the number of retweets.
   */
  lazy val trending: TweetList = (googleTweets union appleTweets).descendingByRetweet
}
//-----------------------------------------------------------------------------------------
object Main extends App {
  // Print the trending tweets
  GoogleVsApple.trending foreach println
}
