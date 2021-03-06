package org.adridadou.ethereum.propeller.converters.e2e

import java.io.File
import java.math.BigInteger
import java.util.concurrent.CompletableFuture

import org.adridadou.ethereum.propeller.keystore.AccountProvider
import org.adridadou.ethereum.propeller.values._
import org.junit.Assert.assertEquals
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import org.scalatest.check.Checkers
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by davidroon on 26.03.17.
  * This code is released under Apache 2 license
  */
class AccountTest extends FlatSpec with Matchers with Checkers with SolidityConversionHelper {

  "The account type" should "be converted into an address and then encoded and the address should be decoded properly" in {
    val contract = contractObject[AccountContract]
    val contractFuture = contractObject[AccountContractFuture]
    check(forAll(arbitrary[BigInt])(checkEncode(contract, _)))
    check(forAll(arbitrary[BigInt])(checkEncode(contractFuture, _)))
  }

  it should "read a keystore file and decode it properly" in {
    val account = AccountProvider.fromKeystore(new File("src/test/resources/keystore.json")).decode("testpassword")
    assertEquals(new BigInteger("55254095649631781209224057814590225966912998986153936485890744796566334537373"), account.getBigIntPrivateKey)
  }

  private def checkEncode(contractObject: AccountContract, seed: BigInt) = {
    val account = new EthAccount(seed.bigInteger)
    contractObject.addressFunc(account) shouldEqual account.getAddress
    true
  }

  private def checkEncode(contractObject: AccountContractFuture, seed: BigInt) = {
    val account = new EthAccount(seed.bigInteger)
    contractObject.addressFunc(account).get() shouldEqual account.getAddress
    true
  }

}

trait AccountContract {
  def addressFunc(account: EthAccount): EthAddress
}

trait AccountContractFuture {
  def addressFunc(account: EthAccount): CompletableFuture[EthAddress]
}
