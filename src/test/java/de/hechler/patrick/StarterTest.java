package de.hechler.patrick;

import de.hechler.patrick.fileparser.ParserChecker;
import de.hechler.patrick.fileparser.serial.SerializerObjectsChecker;
import de.hechler.patrick.fileparser.serial.SerializerPrimArrayChecker;
import de.hechler.patrick.fileparser.serial.SerializerPrimitivesChecker;
import de.hechler.patrick.zeugs.check.BigCheckResult;
import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.exceptions.CheckerNotEqualsExeption;

public class StarterTest {
	
	public void testname() throws Exception {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		BigCheckResult res = Checker.checkAll(true, ParserChecker.class, SerializerObjectsChecker.class, SerializerPrimitivesChecker.class, SerializerPrimArrayChecker.class);
		res.print();
		res.forAllUnexpected((c, m, t) -> {
			System.err.println(c + "  ->  " + m + ":");
			if (t instanceof CheckerNotEqualsExeption) {
				CheckerNotEqualsExeption cnee = (CheckerNotEqualsExeption) t;
				System.err.println("a.hash=" + cnee.a.hashCode());
				System.err.println("b.hash=" + cnee.b.hashCode());
				if (cnee.a.getClass() == String.class && cnee.b.getClass() == String.class) {
					char[] a = ((String) cnee.a).toCharArray(), b = ((String) cnee.b).toCharArray();
					if (a.length != b.length) System.err.println("diffrent lenght: a=" + a.length + " b=" + b.length);
					for (int i = 0; i < a.length && i < b.length; i ++ ) {
						if (a[i] != b[i]) {
							String as = a[i] == '\r' ? "[CR]" : (a[i] == '\n' ? "[LF]" : (a[i] == '\t' ? "[TAB]" : a[i] == ' ' ? "[ ]" : (a[i] + ""))),
									bs = b[i] == '\r' ? "[CR]" : (b[i] == '\n' ? "[LF]" : (b[i] == '\t' ? "[TAB]" : b[i] == ' ' ? "[ ]" : (b[i] + "")));
							System.err.println("a[" + i + "]=" + as + " b[" + i + "]=" + bs);
						}
					}
				}
			}
			t.printStackTrace();
		});
	}
	
}
