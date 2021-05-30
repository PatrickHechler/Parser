package de.hechler.patrick.fileparser;

import java.io.PrintStream;
import java.util.*;

public class Parser {
	
	private static final String lsregex = "(\r\n)|\r|\n";
	
	public static final ParserTemplate EMPTY_TEMPLATE                       = new ParserTemplate(new String[0], new String[0], "", false, false, "", false, false, true, "", "", "", false, Collections.emptyList(),
			false, false, System.lineSeparator(), false);
	public static final ParserTemplate MAIN_TEMPLATE                        = new ParserTemplate(new String[] {"int main() {", "\tasm volatile (" }, new String[] {"\t);", "\treturn 0;", "}" }, "\t\t\"", false,
			false, "\\n\"", false, false, false, ";", "//", "", true, Collections.emptyList(), false, false, System.lineSeparator(), false);
	public static final ParserTemplate ARDUINO_TEMPLATE                     = new ParserTemplate(new String[] {"void setup() {", "\tasm volatile (" }, new String[] {"\t);", "}", "", "void loop() {}" }, "\t\t\"",
			false, false, "\\n\"", false, false, false, ";", "//", "", true, Collections.emptyList(), false, false, System.lineSeparator(), false);
	public static final ParserTemplate ARDUINO_TEMPLATE_WITH_EMPTY_ASM_LOOP = new ParserTemplate(new String[] {"void setup() {", "\tasm volatile (" },
			new String[] {"\t);", "}", "", "void loop() {", "\tasm volatile (", "\t\t\"\\n\"", "\t);", "}" }, "\t\t\"", false, false, "\\n\"", false, false, false, ";", "//", "", true, Collections.emptyList(),
			false, false, System.lineSeparator(), false);
	public static final ParserTemplate ARDUINO_TEMPLATE_WITH_LOOP_REPLACE   = new ParserTemplate(new String[] {"void setup() {", "\tasm volatile (" }, new String[] {"\t);", "}" }, "\t\t\"", false, false, "\\n\"",
			false, false, false, ";", "//", "", true,
			Arrays.asList(new Replace("^(\\s*)([lL][oO][oO][pP])\\s*\\:\\s*$", "$1\t\t\"$2\\:\\\\n\"\n\t);\n}\n\nvoid loop() {\n\tasm volatile(\n$1\t\t\"$2\\:\\\\n\""),
					new Replace("^(\\s*)([lL][oO][oO][pP])\\s*\\:(\\s*)([^;]+)$", "$1\t\t\"$2\\:\\\\n\\\"\n\t);\n}\n\nvoid loop() {\n\tasm volatile(\n$1\t\t\\\"$2\\:\\\\n\\\"\n$3\t\t\"$4\\\\n\\\""),
					new Replace("^(\\s*)([lL][oO][oO][pP])\\s*\\:(\\s*);(.*)$", "$1\t\t\"$2\\:\\\\n\\\"\n\t);\n}\n\nvoid loop() {\n\tasm volatile(\n$1\t\t\"$2\\:\\\\n\\\"$3//$4"),
					new Replace("^(\\s*)([lL][oO][oO][pP])\\s*\\:(\\s*)([^;]+);(.*)$", "$1\t\t\"$2\\:\\\\n\\\"\n\t);\n}\n\nvoid loop() {\n\tasm volatile(\n$1\t\t\"$2\\:\\\\n\\\"\n$3\t\t\"$4\\\\n\\\"//$5")),
			false, false, System.lineSeparator(), false);
	
	private ParserTemplate properties;
	
	public Parser(ParserTemplate properties) {
		this.properties = properties == null ? EMPTY_TEMPLATE : properties;
	}
	
	public ParserTemplate getProperties() {
		return properties;
	}
	
	public void setProperties(ParserTemplate properties) {
		this.properties = Objects.requireNonNull(properties, "no null properties permittet");
	}
	
	private void println(PrintStream output) {
		if (properties.explicitLineSep) {
			output.print(properties.lineSeparator);
		}
	}
	
	private void println(PrintStream output, String str) {
		str = str.replaceAll(lsregex, properties.lineSeparator);
		output.print(str);
		if (properties.explicitLineSep) {
			output.print(properties.lineSeparator);
		}
	}
	
	private void print(PrintStream output, String str) {
		str = str.replaceAll(lsregex, properties.lineSeparator);
		output.print(str);
	}
	
	public void parse(Scanner input, PrintStream output) {
		for (int i = 0; i < properties.headLines.length; i ++ ) {
			if (properties.lineStartAlsoOnHeadLines) print(output, properties.lineStart);
			print(output, properties.headLines[i]);
			if (properties.lineEndAlsoOnHeadLines) print(output, properties.lineEnd);
			println(output);
		}
		while (input.hasNextLine()) {
			String line = input.nextLine();
			if (properties.explicitLineSep) {
				line += properties.lineSeparator;
			}
			String comment = "";
			if ( !properties.supressReplaces) {
				boolean replaced = false;
				for (Replace replacer : properties.replaces) {
					String repl = line.replaceAll(replacer.regex, replacer.replacement);
					if (repl != line) replaced = true;
					line = repl;
				}
				if (replaced && !properties.continueAfterReplace) {
					println(output, line);
					continue;
				}
			}
			if (properties.startAfterWhite) {
				String whitespace;
				int dif = 0;
				{
					char[] chars;
					for (chars = line.toCharArray(); dif < chars.length; dif ++ ) {
						if (chars[dif] > ' ') break;// ' ' is the higest whitespace all below is a command (like CR, LF, TAB, ...)
					}
					whitespace = line.substring(0, dif);
					line = line.substring(dif, chars.length);
				}
				print(output, whitespace);
				print(output, properties.lineStart);
			} else print(output, properties.lineStart);
			int ci = line.indexOf(properties.unparsedCommentSymbol);
			if (ci > -1) {
				comment = line.substring(ci + properties.unparsedCommentSymbol.length());
				line = line.substring(0, ci);
				print(output, line);
				comment = properties.parsedCommentSymbol.concat(comment);
			} else {
				print(output, line);
			}
			if (properties.supressCommentExtraction) {
				print(output, comment);
				print(output, properties.lineEnd);
			} else {
				print(output, properties.lineEnd);
				print(output, comment);
			}
			println(output, properties.commentEndLine);
		}
		for (int i = 0; i < properties.tailLines.length; i ++ ) {
			if (properties.lineStartAlsoOnTailLines) print(output, properties.lineStart);
			print(output, properties.tailLines[i]);
			if (properties.lineEndAlsoOnTailLines) print(output, properties.lineEnd);
			println(output);
		}
	}
	
}
