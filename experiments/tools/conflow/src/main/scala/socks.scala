
package socks {
	// requires socks c++ library from previous week
	
	import sys.process._

	class Sock(addr: String) {
		def <<(msg: Any) = 
			s"send /socks/${addr} ${msg}" !
	}

	package object Implicits {
		implicit def stringToSock(s: String) = new socks.Sock(s)
	}
}