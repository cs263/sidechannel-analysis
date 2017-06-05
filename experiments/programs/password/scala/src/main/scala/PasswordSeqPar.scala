
import tests.password._

package tests.password {
	object PasswordSeqPar extends PasswordChecker {
    	val d = new Dictionary()

		def checkPassword(s: String, password: String): Boolean =
			s.zip(password).par.takeWhile(Function.tupled(_ == _)).map(_._1).size == password.size

		def main(args: Array[String]): Unit = {
	        d.run(this)
		}
	}
}
