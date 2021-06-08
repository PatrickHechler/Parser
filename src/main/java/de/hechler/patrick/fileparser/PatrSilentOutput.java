package de.hechler.patrick.fileparser;

import java.io.OutputStream;


public class PatrSilentOutput extends OutputStream {
	
	private static PatrSilentOutput instance;
	
	private PatrSilentOutput() {}
	
	public synchronized static OutputStream getInstance() {
		if (instance == null) {
			instance = new PatrSilentOutput();
		}
		return instance;
	}
	
	@Override
	public void write(int b) {}
	
	@Override
	public void write(byte[] b) {}
	
	@Override
	public void write(byte[] b, int off, int len) {}
	
}
