package de.hechler.patrick.fileparser;

import java.io.PrintStream;
import java.util.*;

public class Parser {
	
	private static final String lsregex = "(\r\n)|\r|\n";
	
	public static final ParserTemplate EMPTY_TEMPLATE = new ParserTemplate(new String[0], new String[0], "", false, false, "", false, false, true, "", "", "", false,
			Collections.emptyList(), false, false, System.lineSeparator());
	public static final ParserTemplate MAIN_TEMPLATE = new ParserTemplate(new String[] {"int main() {", "\tasm volatile (" }, new String[] {"\t);", "\treturn 0;", "}" }, "\t\t\"",
			false, false, "\\n\"", false, false, false, ";", "//", "", true, Collections.emptyList(), false, false, System.lineSeparator());
	public static final ParserTemplate ARDUINO_TEMPLATE = new ParserTemplate(new String[] {"void setup() {", "\tasm volatile (" },
			new String[] {"\t);", "}", "", "void loop() {}" }, "\t\t\"", false, false, "\\n\"", false, false, false, ";", "//", "", true, Collections.emptyList(), false, false,
			System.lineSeparator());
	public static final ParserTemplate ARDUINO_TEMPLATE_WITH_EMPTY_ASM_LOOP = new ParserTemplate(new String[] {"void setup() {", "\tasm volatile (" },
			new String[] {"\t);", "}", "", "void loop() {", "\tasm volatile (", "\t\t\"\\n\"", "\t);", "}" }, "\t\t\"", false, false, "\\n\"", false, false, false, ";", "//", "",
			true, Collections.emptyList(), false, false, System.lineSeparator());
	public static final ParserTemplate ARDUINO_TEMPLATE_WITH_LOOP_REPLACE = new ParserTemplate(new String[] {"void setup() {", "\tasm volatile (" }, new String[] {"\t);", "}" },
			"\t\t\"", false, false, "\\n\"", false, false, false, ";", "//", "", true,
			Arrays.asList(new Replace("^(\\s*)([lL][oO][oO][pP])\\s*\\:\\s*$", "$1\t\t\"$2\\:\\\\n\"\n\t);\n}\n\nvoid loop() {\n\tasm volatile(\n$1\t\t\"$2\\:\\\\n\""),
					new Replace("^(\\s*)([lL][oO][oO][pP])\\s*\\:(\\s*)([^;]+)$",
							"$1\t\t\"$2\\:\\\\n\\\"\n\t);\n}\n\nvoid loop() {\n\tasm volatile(\n$1\t\t\\\"$2\\:\\\\n\\\"\n$3\t\t\"$4\\\\n\""),
					new Replace("^(\\s*)([lL][oO][oO][pP])\\s*\\:(\\s*);(.*)$", "$1\t\t\"$2\\:\\\\n\\\"\n\t);\n}\n\nvoid loop() {\n\tasm volatile(\n$1\t\t\"$2\\:\\\\n\\\"$3//$4"),
					new Replace("^(\\s*)([lL][oO][oO][pP])\\s*\\:(\\s*)([^;]+);(.*)$",
							"$1\t\t\"$2\\:\\\\n\\\"\n\t);\n}\n\nvoid loop() {\n\tasm volatile(\n$1\t\t\"$2\\:\\\\n\\\"\n$3\t\t\"$4\\\\n\\\"//$5")),
			false, false, System.lineSeparator());
	
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
		output.print(properties.lineSeparator);
	}
	
	private void println(PrintStream output, String str) {
		str = str.replaceAll(lsregex, properties.lineSeparator);
		output.print(str);
		output.print(properties.lineSeparator);
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
				String newLine = line.stripLeading();
				int dif = line.length() - newLine.length();
				String whitespace = line.substring(0, dif);
				line = line.substring(dif);
				print(output, whitespace);
				print(output, properties.lineStart);
				// compatibile with older versions:
				line = newLine;
				// compatible end2
			} else print(output, properties.lineStart);
			if ( !properties.supressCommentExtraction) {
				int ci = line.indexOf(properties.asmCommentSymbol);
				if (ci > -1) {
					comment = line.substring(ci + properties.asmCommentSymbol.length());
					line = line.substring(0, ci);
					print(output, line);
					comment = properties.parsedCommentSymbol.concat(comment);
				} else print(output, line);
			} else print(output, line);
			print(output, properties.lineEnd);
			print(output, comment);
			println(output, properties.commentEndLine);
		}
		for (int i = 0; i < properties.tailLines.length; i ++ ) {
			if (properties.lineStartAlsoOnTailLines) print(output, properties.lineStart);
			print(output, properties.tailLines[i]);
			if (properties.lineEndAlsoOnTailLines) print(output, properties.lineEnd);
			println(output);
		}
	}
	
	
	
	public static class ParserTemplate {
		
		public final String[] headLines;
		public final String[] tailLines;
		public final String lineStart;
		public final boolean lineStartAlsoOnHeadLines;
		public final boolean lineStartAlsoOnTailLines;
		public final String lineEnd;
		public final boolean lineEndAlsoOnHeadLines;
		public final boolean lineEndAlsoOnTailLines;
		public final boolean supressCommentExtraction;
		public final String asmCommentSymbol;
		public final String parsedCommentSymbol;
		public final String commentEndLine;
		public final boolean startAfterWhite;
		public final List <Replace> replaces;
		public final boolean supressReplaces;
		public final boolean continueAfterReplace;
		public final String lineSeparator;
		public Boolean explicitLineSep;
		
		
		
		public ParserTemplate(String[] headLines, String[] tailLines, String lineStart, boolean lineStartAlsoOnHeadLines, boolean lineStartAlsoOnTailLines, String lineEnd,
				boolean lineEndAlsoOnHeadLines, boolean lineEndAlsoOnTailLines, boolean supressCommentExtraction, String asmCommentSymbol, String parsedCommendSymbol,
				String commentEndLine, boolean startIgnoresWhite, List <Replace> replaces, boolean supressReplaces, boolean continueAfterReplace, String lineSeparator,
				boolean explicitLineSep) {
			this.headLines = headLines == null ? new String[0] : headLines.clone();
			this.tailLines = tailLines == null ? new String[0] : tailLines.clone();
			this.lineStart = lineStart == null ? "" : lineStart;
			this.lineStartAlsoOnHeadLines = lineStartAlsoOnHeadLines;
			this.lineStartAlsoOnTailLines = lineStartAlsoOnTailLines;
			this.lineEnd = lineEnd == null ? "" : lineEnd;
			this.lineEndAlsoOnHeadLines = lineEndAlsoOnHeadLines;
			this.lineEndAlsoOnTailLines = lineEndAlsoOnTailLines;
			this.supressCommentExtraction = supressCommentExtraction;
			this.asmCommentSymbol = asmCommentSymbol == null ? "" : asmCommentSymbol;
			this.parsedCommentSymbol = parsedCommendSymbol == null ? "" : parsedCommendSymbol;
			this.commentEndLine = commentEndLine == null ? "" : commentEndLine;
			this.startAfterWhite = startIgnoresWhite;
			this.replaces = Collections.unmodifiableList(replaces == null ? Collections.emptyList() : new ArrayList <>(replaces));
			this.supressReplaces = supressReplaces;
			this.continueAfterReplace = continueAfterReplace;
			this.lineSeparator = lineSeparator == null ? System.lineSeparator() : lineSeparator;
			this.explicitLineSep = explicitLineSep;
		}
		
	}
	
	public static class Replace {
		
		public final String regex;
		public final String replacement;
		
		public Replace(String regex, String replace) {
			this.regex = regex;
			this.replacement = replace;
		}
		
	}
	
}
