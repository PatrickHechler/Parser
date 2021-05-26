package de.hechler.patrick.fileparser;

import de.hechler.patrick.zeugs.check.*;
import de.hechler.patrick.zeugs.check.anotations.*;

@CheckClass
public class MAINChecker extends Checker {
	
	@Start(onlyOnce = true)
	public void beginAll() {
		
	}
	
	@End(onlyOnce =  true)
	public void endAll() {
		
	}
	
	@Start
	public void start() {
		
	}
		
	@End
	public void end() {
		
	}
	
	
	@Check
	//TODO check
	public void check() {
		
	}
	
}
