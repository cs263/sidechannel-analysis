
public class Switches {
	enum Ops {
		Yes, No
	};

	public static void main(String[] args) {
		new Switches().doStuff();
	}

	public Switches() {
		doStuff();
	}

	public void doStuff() {
		int x = 7;
		switch(x) {
			case 14:
				System.out.println("1");
				break;
			case 22:
				System.out.println("2");
//			default:
//				System.out.println("default");
		}

		Ops o = Ops.Yes;
		switch(o) {
			case Yes:
				System.out.println("1");
			case No:
				System.out.println("2");
		}
	}
}