package de.hechler.patrick.fileparser;


public class Replace {
	
	public final String regex;
	public final String replacement;
	
	public Replace(String regex, String replace) {
		this.regex = regex;
		this.replacement = replace;
	}
	
}
