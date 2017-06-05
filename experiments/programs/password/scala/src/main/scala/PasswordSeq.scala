
import tests.password._

object PasswordSeq extends PasswordChecker {
	def checkPassword(s: String, password: String): Boolean =
		s.zip(password).takeWhile(Function.tupled(_ == _)).map(_._1).size == password.size
}