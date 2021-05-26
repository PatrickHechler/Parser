package de.hechler.patrick.fileparser;

import de.hechler.patrick.zeugs.check.*;
import de.hechler.patrick.zeugs.check.anotations.*;

@CheckClass
//TODO checks
public class ParserChecker extends Checker {
	
	@Start(onlyOnce = true)
	void beginAll() {
		
	}
	
	@End(onlyOnce =  true)
	void endAll() {
		
	}
	
	@Start
	void start() {
		
	}
		
	@End
	void end() {
		
	}
	
	@Check
	void check() {
		
	}
	
}
