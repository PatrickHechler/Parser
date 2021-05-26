package de.hechler.patrick;

import de.hechler.patrick.fileparser.MAINChecker;
import de.hechler.patrick.fileparser.ParserChecker;
import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.Checker.BigCheckResult;

public class Fileparser {
	
	public static void main(String[] args) {
		BigCheckResult res = Checker.checkAll(true, ParserChecker.class, MAINChecker.class);
		res.print();
		res.forAllUnexpected((c, m, t) -> {
			System.err.println(c + "  ->  " + m + ":");
			t.printStackTrace();
		});
	}
	
}
