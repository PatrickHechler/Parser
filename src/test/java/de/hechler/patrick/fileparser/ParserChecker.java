package de.hechler.patrick.fileparser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import de.hechler.patrick.fileparser.Parser.ParserTemplate;
import de.hechler.patrick.zeugs.check.*;
import de.hechler.patrick.zeugs.check.anotations.*;

@CheckClass
// TODO more checks
public class ParserChecker extends Checker {
	
	ByteArrayInputStream  bais;
	Scanner               scanner;
	ByteArrayOutputStream baos;
	PrintStream           print;
	ParserTemplate        template;
	Parser                parser;
	
	@Start(onlyOnce = true)
	void beginAll() {
	}
	
	@End(onlyOnce = true)
	void endAll() {
	}
	
	@Start
	void start() {
		bais = null;
		scanner = null;
		baos = new ByteArrayOutputStream();
		print = new PrintStream(baos, false /* the checks will decide when it will flush */, StandardCharsets.UTF_8);
		template = Parser.EMPTY_TEMPLATE;
		parser = new Parser(template);
	}
	
	@End
	void end() {
		bais = null;
		scanner = null;
		baos = null;
		print = null;
		template = null;
		parser = null;
	}
	
	@Check
	void checkEmptyTemplate() {
		String str = "this is a nice text.\nIt will be unchanged!\n\n\t\t\n  \n".replaceAll("(\r\n)|\r|\n", "\\\r\\\n");
		bais = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
		scanner = new Scanner(bais, StandardCharsets.UTF_8);
		parser.parse(scanner, print);
		print.flush();
		assertEquals(str, new String(baos.toByteArray(), StandardCharsets.UTF_8));
	}
	
}
