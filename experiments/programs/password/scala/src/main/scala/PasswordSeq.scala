
import tests.password._

package tests.password {
	object PasswordSeq extends PasswordChecker {
    	val d = new Dictionary()

		def checkPassword(s: String, password: String): Boolean =
			s.zip(password).takeWhile(Function.tupled(_ == _)).map(_._1).size == password.size

		def main(args: Array[String]): Unit = {
	        d.run(this)
		}
	}
}
