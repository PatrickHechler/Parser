package de.hechler.patrick.fileparser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import de.hechler.patrick.fileparser.gui.ParserGUI;

public class Main {
	
	private static final String ASSEMBLER_POSTFIX = "asm";
	private static final String ARDUINO_POSTFIX   = "ino";
	private static final String MAIN_POSTFIX      = "c";
	private static final String OTHER_POSTFIX     = "txt";
	
	
	
	private static Parser      parser;
	private static Scanner     scan;
	private static PrintStream print;
	private static boolean     finishMsg;
	
	public static void main(String[] args) {
		if (args.length == 0) {
			new ParserGUI().load();
			return;
		}
		long start = System.currentTimeMillis();
		setup(args);
		if (parser == null) exit("parser is not set", args);
		if (scan == null) exit("input is not set", args);
		if (print == null) exit("output is not set", args);
		parser.parse(scan, print);
		if (finishMsg) System.out.println("finish parsing after: " + time(System.currentTimeMillis() - start));
	}
	
	private static String time(long time) {
		if (time < 2000) {
			return time + "ms";
		} else if (time < 240000) {
			return (time / 1000) + "sec";
		} else {
			System.err.println("wat was wron with me?!");
			if (time < 7200000) {
				return (time / 60000) + "min";
			} else if (time < 432000000) {
				return (time / 3600000) + "h";
			} else return (time / 86400000) + "days";
		}
	}
	
	private static void exit(String msg, String[] args) {
		System.err.println(msg);
		System.err.println("args: " + Arrays.deepToString(args));
		help(System.err);
		System.exit(1);
	}
	
	private static void help(PrintStream out) {
		out.println("<--help>");
		out.println("          to print this text on the default out");
		out.println("<-help> or <-?>");
		out.println("          to print this text on the default err");
		out.println("<--noFinish> or <--nf>");
		out.println("          to print no finish message after finishing with parsing");
		out.println("<--silent> or <--s>");
		out.println("          to print the parsed not to the generated target file (if no target is set it will be generated from the name of the source file, but only if silent is not set)");
		out.println("<--print>");
		out.println("          to print the parsed also to the default out");
		out.println("<-print>");
		out.println("          to print the parsed also to the default err");
		out.println("<--lsp> or <--lineSeparator> <CRLF> or <CR> or <LF>");
		out.println("          sets the line separator to the param");
		out.println("<-ep> or <-empty>");
		out.println("          to use the Empty property as default");
		out.println("<-aplp> or <-arduinoPotLoop>");
		out.println("          to set the default propertie to the arduino potential loop propertie");
		out.println("<-alp> or <-arduinoAsmLoop>");
		out.println("          to use the Arduino with empty asm loop property as default");
		out.println("          the void loop() will stil be unused for the parsing, but inside it is now be a asm-diriective");
		out.println("<-ap> or <-arduino>");
		out.println("          to use the Arduino property as default");
		out.println("<-mp> or <-main>");
		out.println("          to use the Main property as default");
		out.println("<-hl> or <-headLines> [END_MAGIX] <arg>* [END_MAGIX]");
		out.println("          to set the head lines (the END_MAGIX will not be added on the start or end of the head lines)");
		out.println("<-tl> or <-tailLines> [END_MAGIX] <arg>* [END_MAGIX]");
		out.println("          to set the tail lines (the END_MAGIX will not be added on the start or end of the tail lines)");
		out.println("<-saw> or <-startAftersWhite> <true>/<1> or <false>/<0>");
		out.println("          to set if the line start will be set after the whitespace at the begin of the line or before the whitespace of the line");
		out.println("<-ls> or <-lineStart> [LINE_START]");
		out.println("          to set the start of each printed line");
		out.println("<-le> or <-lineEnd> [LINE_END]");
		out.println("          to set the end of each printed line, this will be printed before the (comment if not deactivated) and commentEndLine");
		out.println("<-eoh> or <-endHead> <true>/<1> or <false>/<0>");
		out.println("          to enable <true>/<1> or disable <false>/<0> the endLine for the head");
		out.println("<-soh> or <-startHead> <true>/<1> or <false>/<0>");
		out.println("          to enable <true>/<1> or disable <false>/<0> the startLine for the head");
		out.println("<-eot> or <-endTail> <true>/<1> or <false>/<0>");
		out.println("          to enable <true>/<1> or disable <false>/<0> the endLine for the tail");
		out.println("<-sot> or <-startTail> <true>/<1> or <false>/<0>");
		out.println("          to enable <true>/<1> or disable <false>/<0> the startLine for the tail");
		out.println("<-rep> or <-replace> [REGEX] [REPLACEMENT]");
		out.println("          to try replacing in every line");
		out.println("          this option can be used multiple times");
		out.println("               when used multiple times the first replac-args will replace earlier and the ones after them");
		out.println("               the later replace-args will try to replace the replaced");
		out.println("          the replacement will be treated as a single line, even if there are line seperators inside");
		out.println("               these potential line separators will be overwritten with the overgiven line separator (<--lineSeparator>)");
		out.println("               for the line start and line end it will be only one line");
		out.println("          the replacement will be called befor lineStart or lineEnd had been set");
		out.println("<-orwr> or <-onlyRepWhenRep> <true>/<1> or <false>/<0>");
		out.println("          to enble/disable if the parse should stop modifiing the line after all replacements are done and they changed something");
		out.println("<-sr> or <-supressReplaces> <true>/<1> or <false>/<0>");
		out.println("          to supress(true/1) or allow(false/0) replaces");
		out.println("<-els> or <-explicitLineSep> <true>/<1> or <false>/<0>");
		out.println("          to set if the lines need a explicit line separator (<true>/<1>) or if it should be appanded after each line");
		out.println("          if it is set <true>/<1> the lines will be read with a line separator");
		out.println("<-scp> or <-supressCommentParsing> <true>/<1> or <false>/<0>");
		out.println("          to tell the parser that Assembler comments (by default ';') should be placed after the endLine (but not after the lineSeparator or endCommentLine)");
		out.println("<-parsedCommentSymbol> or <-pcs> [COMMENT_SYMBOL]");
		out.println("          to set the parsed comment symbol to COMMENT_SYMBOL");
		out.println("<-asmCommentSymbol> or <-acs> [COMMENT_SYMBOL]");
		out.println("          to set the Assembler comment symbol to COMMENT_SYMBOL");
		out.println("<-commentEndLine> or <-cel> [COMMENT_END_LINE]");
		out.println("          to print at the very end of each line this (after the endLine (and of course also after the extracted comment if the comments are not disabled))");
		out.println("<-src> [FILE]");
		out.println("          to set the source file");
		out.println("<-dest> or <-target> [FILE]");
		out.println("          to set the target file");
		out.println("<--cs> or <--charset>");
		out.println("          to set the charset of the source and target file");
		out.println("<--force> or <--forceOverride>");
		out.println("          to overwrite the target file if it already exsists");
	}
	
	private static void setup(String[] args) {
		finishMsg = true;
		ParserTemplate props = null;
		String[] headLines = null, tailLines = null;
		String lineStart = null, lineEnd = null;
		Boolean lineStartAlsoOnHeadLines = null, lineStartAlsoOnTailLines = null, lineEndAlsoOnHeadLines = null, lineEndAlsoOnTailLines = null;
		boolean silent = false;
		boolean out = false;
		boolean err = false;
		String src = null;
		String dest = null;
		boolean forceOverrde = false;
		String charset = null;
		String asmCommentSymbol = null;
		String parsedCommentSymbol = null;
		String commentEndLine = null;
		Boolean supressCommentExtraction = null;
		Boolean startAfterWhite = null;
		String lineSep = null;
		List <Replace> replaces = new ArrayList <>();
		Boolean continueAfterReplaces = null;
		Boolean supressReplaces = null;
		Boolean explicitLineSep = null;
		for (int i = 0; i < args.length; i ++ ) {
			switch (args[i].toLowerCase()) {
			case "--nf":
			case "--nofinish":
				if (finishMsg) exit("double of <--noFinish>", args);
				finishMsg = false;
				break;
			case "--s":
			case "--silent":
				if (silent) exit("double of <--silent>", args);
				silent = true;
				finishMsg = false;
				break;
			case "--print":
				if (out) exit("double of <--print>", args);
				out = true;
				break;
			case "-print":
				if (err) exit("double of <-print>", args);
				err = true;
				break;
			case "--lsp":
			case "--lineseparator":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-lsp>", args);
				if (lineSep != null) exit("line separator already set: " + ("\r\n".equals(lineSep) ? "CRLF" : ("\n".equals(lineSep) ? "LF" : "CR")), args);
				lineSep = "CRLF".equals(args[i]) ? "\r\n" : ("LF".equals(args[i]) ? "\n" : "\r");
				break;
			case "--help":
				help(System.out);
				if (args.length == 1) {
					System.exit(0);
				}
				break;
			case "-help":
			case "-?":
				help(System.err);
				break;
			case "-empty":
			case "-ep":
				if (props != null) exit("already default property selected: " + props, args);
				props = Parser.EMPTY_TEMPLATE;
				break;
			case "-arduinopotloop":
			case "-aplp":
				if (props != null) exit("already default property selected: " + props, args);
				props = Parser.ARDUINO_TEMPLATE_WITH_LOOP_REPLACE;
				break;
			case "-arduinoasmloop":
			case "-alp":
				if (props != null) exit("already default property selected: " + props, args);
				props = Parser.ARDUINO_TEMPLATE_WITH_EMPTY_ASM_LOOP;
				break;
			case "-arduino":
			case "-ap":
				if (props != null) exit("already default property selected: " + props, args);
				props = Parser.ARDUINO_TEMPLATE;
				break;
			case "-main":
			case "-mp":
				if (props != null) exit("already default property selected: " + props, args);
				props = Parser.MAIN_TEMPLATE;
				break;
			case "-headlines":
			case "-hl": {
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-hl>", args);
				if (headLines != null) exit("tail lines already set: " + Arrays.deepToString(headLines), args);
				String end = args[i];
				i ++ ;
				List <String> head = new ArrayList <>();
				while ( !args[i].equals(end)) {
					head.add(args[i]);
					i ++ ;
					if (i >= args.length) exit("need more arguments on option <-hl> (did not find end: '" + end + "')", args);
				}
				headLines = head.toArray(new String[head.size()]);
				break;
			}
			case "-taillines":
			case "-tl": {
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-tl>", args);
				if (tailLines != null) exit("tail lines already set: " + Arrays.deepToString(tailLines), args);
				String end = args[i];
				i ++ ;
				List <String> tail = new ArrayList <>();
				while ( !args[i].equals(end)) {
					tail.add(args[i]);
					i ++ ;
					if (i >= args.length) exit("need more arguments on option <-hl> (did not find end: '" + end + "')", args);
				}
				tailLines = tail.toArray(new String[tail.size()]);
				break;
			}
			case "-saw":
			case "-startafterwhite":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-startIgnoresWhite>", args);
				if (startAfterWhite != null) exit("start ignores White is already set: " + startAfterWhite, args);
				startAfterWhite = strToBool(args[i], args);
				break;
			case "-linestart":
			case "-ls":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-ls>", args);
				if (lineStart != null) exit("lines start already set: " + lineStart, args);
				lineStart = args[i];
				break;
			case "-lineend":
			case "-le":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-le>", args);
				if (lineEnd != null) exit("lines start already set: " + lineEnd, args);
				lineEnd = args[i];
				break;
			case "-endhead":
			case "-eoh":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-eoh>", args);
				if (lineEndAlsoOnHeadLines != null) exit("lines start already set: " + lineEndAlsoOnHeadLines, args);
				lineEndAlsoOnHeadLines = strToBool(args[i], args);
				break;
			case "-starthead":
			case "-soh":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-soh>", args);
				if (lineStartAlsoOnHeadLines != null) exit("lines start already set: " + lineStartAlsoOnHeadLines, args);
				lineStartAlsoOnHeadLines = strToBool(args[i], args);
				break;
			case "-endtail":
			case "-eot":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-eot>", args);
				if (lineEndAlsoOnTailLines != null) exit("lines start already set: " + lineEndAlsoOnTailLines, args);
				lineEndAlsoOnTailLines = strToBool(args[i], args);
				break;
			case "-starttail":
			case "-sot":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-sot>", args);
				if (lineStartAlsoOnTailLines != null) exit("lines start already set: " + lineStartAlsoOnTailLines, args);
				lineStartAlsoOnTailLines = strToBool(args[i], args);
				break;
			case "-replace":
			case "-rep":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-replace>", args);
				String regex = args[i];
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-replace>", args);
				String replacement = args[i];
				Replace rep = new Replace(regex, replacement);
				replaces.add(rep);
				break;
			case "-orwr":
			case "-onlyrepwhenrep":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-onlyRepWhenRep>", args);
				if (continueAfterReplaces != null) exit("replace when replace is already set: " + !continueAfterReplaces, args);
				continueAfterReplaces = !strToBool(args[i], args);
				break;
			case "-sr":
			case "-supressreplaces":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-supressReplaces>", args);
				if (supressReplaces != null) exit("supress Replaces is already set: " + supressReplaces, args);
				supressReplaces = strToBool(args[i], args);
				break;
			case "-els":
			case "-explicitlinesep":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-explicitLineSep>", args);
				if (explicitLineSep != null) exit("supress Comment parsing is already set: " + explicitLineSep, args);
				explicitLineSep = strToBool(args[i], args);
				break;
			case "-supresscommentparsing":
			case "-scp":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-supresscommentparsing>", args);
				if (supressCommentExtraction != null) exit("supress Comment parsing is already set: " + supressCommentExtraction, args);
				supressCommentExtraction = strToBool(args[i], args);
				break;
			case "-parsedcommentsymbol":
			case "-pcs":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-commentSymbol>", args);
				if (parsedCommentSymbol != null) exit("parsed comment symbol already set: old: '" + parsedCommentSymbol + "' new: '" + args[i] + "'", args);
				parsedCommentSymbol = args[i];
				break;
			case "-asmcommentsymbol":
			case "-acs":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-commentSymbol>", args);
				if (asmCommentSymbol != null) exit("Assembler comment symbol already set: old: '" + asmCommentSymbol + "' new: '" + args[i] + "'", args);
				asmCommentSymbol = args[i];
				break;
			case "-commentendline":
			case "-cel":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-commentEndLine>", args);
				if (commentEndLine != null) exit("comment end line already set: old: '" + commentEndLine + "' new: '" + args[i] + "'", args);
				commentEndLine = args[i];
				break;
			case "-src":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-src>", args);
				if (src != null) exit("only one source permitted: '" + src + "'", args);
				src = args[i];
				break;
			case "-dest":
			case "-target":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-target>", args);
				if (dest != null) exit("only one target permitted: '" + dest + "'", args);
				if (silent) exit("can't set target file with already be --silent! dest: '" + args[i] + "'", args);
				dest = args[i];
				break;
			case "--cs":
			case "--charset":
				i ++ ;
				if (i >= args.length) exit("need more arguments on option <-charset>", args);
				if (charset != null) exit("charset already set to: '" + charset + "'", args);
				charset = args[i];
				break;
			case "--force":
			case "--forceoverride":
				if (forceOverrde) exit("force override is already set!", args);
				forceOverrde = true;
				break;
			default:
				exit("unknown arg: [" + i + "] '" + args[i] + "' args: " + Arrays.deepToString(args), args);
			}
		}
		if (src == null) exit("no source file!", args);
		if (dest == null) {
			if (props == Parser.ARDUINO_TEMPLATE || props == Parser.ARDUINO_TEMPLATE_WITH_EMPTY_ASM_LOOP || props == Parser.ARDUINO_TEMPLATE_WITH_LOOP_REPLACE) {
				dest = src.replaceFirst("^(.*)\\." + ASSEMBLER_POSTFIX + "$", "$1\\." + ARDUINO_POSTFIX);
				if (dest == src) dest = src + "." + ARDUINO_POSTFIX;
			} else if (props == Parser.MAIN_TEMPLATE) {
				dest = src.replaceFirst("^(.*)\\." + ASSEMBLER_POSTFIX + "$", "$1\\." + MAIN_POSTFIX);
				if (dest == src) dest = src + "." + MAIN_POSTFIX;
			} else {
				dest = src.replaceFirst("^(.*)\\." + ASSEMBLER_POSTFIX + "$", "$1\\." + OTHER_POSTFIX);
				if (dest == src) dest = src + "." + OTHER_POSTFIX;
			}
		}
		Path target = Paths.get(dest);
		if ( !forceOverrde && Files.exists(target)) exit("destiny exsist already to force override use option <--force>", args);
		Path source = Paths.get(src);
		if ( !Files.exists(source)) exit("no source file found src: '" + src + "'", args);
		Charset cs;
		try {
			if (charset != null) cs = Charset.forName(charset);
			else cs = Charset.defaultCharset();
		} catch (UnsupportedCharsetException | IllegalCharsetNameException e) {
			e.printStackTrace();
			exit("class -> " + e.getClass().getName() + "   msg -> " + e.getLocalizedMessage(), args);
			cs = null;// to appease the compiler this line will never be called
		}
		try {
			scan = new Scanner(source, charset);
		} catch (UnsupportedCharsetException | IllegalCharsetNameException | IOException e) {
			e.printStackTrace();
			exit("class -> " + e.getClass().getName() + "   msg -> " + e.getLocalizedMessage(), args);
		}
		try {
			print = new PatrOutput(new FileOutputStream(dest), cs, System.lineSeparator());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			exit("class -> " + e.getClass().getName() + "   msg -> " + e.getLocalizedMessage(), args);
		}
		try {
			props = new ParserTemplate(headLines == null ? props.headLines : headLines, tailLines == null ? props.tailLines : tailLines, lineStart == null ? props.lineStart : lineStart,
					lineStartAlsoOnHeadLines == null ? props.lineStartAlsoOnHeadLines : lineStartAlsoOnHeadLines, lineStartAlsoOnTailLines == null ? props.lineStartAlsoOnTailLines : lineStartAlsoOnTailLines,
					lineEnd == null ? props.lineEnd : lineEnd, lineEndAlsoOnHeadLines == null ? props.lineEndAlsoOnHeadLines : lineEndAlsoOnHeadLines,
					lineEndAlsoOnTailLines == null ? props.lineEndAlsoOnTailLines : lineEndAlsoOnTailLines, supressCommentExtraction == null ? props.supressCommentExtraction : supressCommentExtraction,
					asmCommentSymbol == null ? props.asmCommentSymbol : asmCommentSymbol, parsedCommentSymbol == null ? props.parsedCommentSymbol : parsedCommentSymbol,
					commentEndLine == null ? props.commentEndLine : commentEndLine, startAfterWhite == null ? props.startAfterWhite : startAfterWhite,
					(replaces.isEmpty() ? (props == null ? Collections.emptyList() : props.replaces) : replaces), supressReplaces == null ? props.supressReplaces : supressReplaces,
					continueAfterReplaces == null ? props.continueAfterReplace : continueAfterReplaces, lineSep == null ? (props == null ? System.lineSeparator() : props.lineSeparator) : lineSep,
					explicitLineSep == null ? props.explicitLineSep : explicitLineSep);
		} catch (NullPointerException e) {
			StringBuilder build = new StringBuilder("i am missing some properties: [");
			if (headLines == null) build.append("headLines, ");
			if (tailLines == null) build.append("tailLines, ");
			if (lineStart == null) build.append("lineStart, ");
			if (lineStartAlsoOnHeadLines == null) build.append("lineStartAlsoOnHeadLines, ");
			if (lineStartAlsoOnTailLines == null) build.append("lineStartAlsoOnTailLines, ");
			if (lineEnd == null) build.append("lineEnd, ");
			if (lineEndAlsoOnHeadLines == null) build.append("lineEndAlsoOnHeadLines, ");
			if (lineEndAlsoOnTailLines == null) build.append("lineEndAlsoOnTailLines, ");
			if (supressCommentExtraction == null) build.append("supressCommentExtraction, ");
			if (asmCommentSymbol == null) build.append("asmCommentSymbol, ");
			if (parsedCommentSymbol == null) build.append("parsedCommentSymbol, ");
			if (commentEndLine == null) build.append("commentEndLine, ");
			if (startAfterWhite == null) build.append("startAfterWhite, ");
			if (replaces.isEmpty()) build.append("(replaces {optional}), ");
			if (supressReplaces == null) build.append("supressReplaces, ");
			if (continueAfterReplaces == null) build.append("onlyReplacesWhenReplaced, ");
			if (lineSep == null) build.append("(lineSeparator {optional}), ");
			if (explicitLineSep == null) build.append("explicitLineSep, ");
			exit(build.append(']').toString(), args);
		}
		parser = new Parser(props);
	}
	
	private static Boolean strToBool(String string, String[] args) {
		switch (string.toLowerCase()) {
		case "1":
		case "true":
			return true;
		case "0":
		case "false":
			return false;
		default:
			exit("awaitet boolean ['true' or '1' or 'false' or '0'] but got: '" + string + "'", args);
			throw new InternalError("did not exit!");
		}
	}
	
}
