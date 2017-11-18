package com.jamesswafford.chess4j.board.squares

import com.jamesswafford.chess4j.board.squares._
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

class DirectionSpec extends FlatSpec with GivenWhenThen with Matchers {

  "e6 square" should "be north of e4" in {
    Given("the e6 square")
    val e6 = Square.valueOf(File.FILE_E,Rank.RANK_6)

    And("the e4 square")
    val e4 = Square.valueOf(File.FILE_E,Rank.RANK_4)

    Then("e6 should be north of e4")
    Direction.directionTo(e4.value)(e6.value) shouldEqual North.getInstance()
  }

  "e4 square" should "be south of e6" in {
    Given("the e4 square")
    val e4 = Square.valueOf(File.FILE_E,Rank.RANK_4)

    And("the e6 square")
    val e6 = Square.valueOf(File.FILE_E,Rank.RANK_6)

    Then("e4 should be south of e6")
    Direction.directionTo(e6.value)(e4.value) shouldEqual South.getInstance()
  }

  "e4 square" should "be west of h4" in {
    Given("the e4 square")
    val e4 = Square.valueOf(File.FILE_E,Rank.RANK_4)

    And("the h4 square")
    val h4 = Square.valueOf(File.FILE_H,Rank.RANK_4)

    Then("e4 should be west of h4")
    Direction.directionTo(h4.value)(e4.value) shouldEqual West.getInstance()
  }

  "e4 square" should "be east of a4" in {
    Given("the e4 square")
    val e4 = Square.valueOf(File.FILE_E,Rank.RANK_4)

    And("the a4 square")
    val a4 = Square.valueOf(File.FILE_A,Rank.RANK_4)

    Then("e4 should be east of h4")
    Direction.directionTo(a4.value)(e4.value) shouldEqual East.getInstance()
  }

  "e4 square" should "be southwest of h7" in {
    Given("the e4 square")
    val e4 = Square.valueOf(File.FILE_E,Rank.RANK_4)

    And("the h7 square")
    val h7 = Square.valueOf(File.FILE_H,Rank.RANK_7)

    Then("e4 should be southwest of h7")
    Direction.directionTo(h7.value)(e4.value) shouldEqual SouthWest.getInstance()
  }

  "e4 square" should "be southeast of c6" in {
    Given("the e4 square")
    val e4 = Square.valueOf(File.FILE_E,Rank.RANK_4)

    And("the c6 square")
    val c6 = Square.valueOf(File.FILE_C,Rank.RANK_6)

    Then("e4 should be southeast of c6")
    Direction.directionTo(c6.value)(e4.value) shouldEqual SouthEast.getInstance()
  }

  "e4 square" should "be northwest of h1" in {
    Given("the e4 square")
    val e4 = Square.valueOf(File.FILE_E,Rank.RANK_4)

    And("the h1 square")
    val h1 = Square.valueOf(File.FILE_H,Rank.RANK_1)

    Then("e4 should be northwest of h1")
    Direction.directionTo(h1.value)(e4.value) shouldEqual NorthWest.getInstance()
  }

  "e4 square" should "be northeast of b1" in {
    Given("the e4 square")
    val e4 = Square.valueOf(File.FILE_E,Rank.RANK_4)

    And("the b1 square")
    val b1 = Square.valueOf(File.FILE_B,Rank.RANK_1)

    Then("e4 should be northeast of b1")
    Direction.directionTo(b1.value)(e4.value) shouldEqual NorthEast.getInstance()
  }

  "e4 square" should "have no direction to a1" in {
    Given("the e4 square")
    val e4 = Square.valueOf(File.FILE_E,Rank.RANK_4)

    And("the a1 square")
    val a1 = Square.valueOf(File.FILE_A,Rank.RANK_1)

    Then("e4 should not have a direction to a1")
    Direction.directionTo(a1.value)(e4.value) should be(null)
  }

}
