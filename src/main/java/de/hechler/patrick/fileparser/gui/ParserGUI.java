package de.hechler.patrick.fileparser.gui;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import de.hechler.patrick.fileparser.ParserTemplate;


public class ParserGUI extends JFrame {
	
	/** UID */
	private static final long serialVersionUID = 4993689297474827609L;
	
	
	public ParserGUI() {}
	
	public ParserGUI load() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setLayout(null);
		
		setTitle("ENTER ARGS");
		
		setResizable(true);
		JFileChooser fc = new JFileChooser();
		JButton srcDir = new JButton("source dir");
		srcDir.addActionListener((a) -> {
			System.out.println("browse files");
			int returnVal = fc.showOpenDialog(this);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				System.out.println("user has canceld");
			}else {
				System.out.println(fc.getSelectedFile());
			}
		});
		
		srcDir.setBounds(10, 10, 100, 20);
		add(srcDir);
		
		setBounds(0, 0, 500, 100);
		setLocationRelativeTo(null);
		/*
		out.println("<--help>");
		out.println("          to print this text on the default out");
		out.println("<-help> or <-?>");
		out.println("          to print this text on the default err");
		out.println("<--noFinish> or <--nf>");
		out.println("          to print no finish message after finishing with parsing");
		out.println("<--silent> or <--s>");
		out.println(
				"          to print the parsed not to the generated target file (if no target is set it will be generated from the name of the source file, but only if silent is not set)");
		out.println("<--print>");
		out.println("          to print the parsed also to the default out");
		out.println("<-print>");
		out.println("          to print the parsed also to the default err");
		out.println("<--ls> or <--lineSeparator> <CRLF> or <CR> or <LF>");
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
		 */
		
		setVisible(true);
		toFront();
		return this;
	}
	
	public String[] getArgs() {
		// TODO Auto-generated method stub
		
		throw new RuntimeException("noch nicht gemacht!");
	}
	
}
