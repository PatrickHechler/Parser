package de.hechler.patrick.fileparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParserTemplate {
	
	public final String[] headLines;
	public final String[] tailLines;
	public final String lineStart;
	public final boolean lineStartAlsoOnHeadLines;
	public final boolean lineStartAlsoOnTailLines;
	public final String lineEnd;
	public final boolean lineEndAlsoOnHeadLines;
	public final boolean lineEndAlsoOnTailLines;
	public final boolean supressCommentExtraction;
	public final String unparsedCommentSymbol;
	public final String parsedCommentSymbol;
	public final String commentEndLine;
	public final boolean startAfterWhite;
	public final List <Replace> replaces;
	public final boolean supressReplaces;
	public final boolean continueAfterReplace;
	public final String lineSeparator;
	public final Boolean explicitLineSep;
	
	
	
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
		this.unparsedCommentSymbol = asmCommentSymbol == null ? "" : asmCommentSymbol;
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

