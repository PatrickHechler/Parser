void setup() {
	asm volatile (
		"Setup:\n"
			"mov AX, 0xFF\n"
			"out 0x04, AX\n"//festlegen der Datenrichtung
	    		"mov DX, 3\n"//aktiviere nicht Pin[11];Bit[3] von den hinteren (PIN[8-13]) ist das du Trottel
		"LOOP:\n"
			"mov BX, 5\n"//die Zeile zwei Zeilen darüber ist aus irgendeinem grund mit 4 leerzeichen mehr eingerückt als sie eigentlich sollte
		"lOoP:mov BX, AX\n"
		"lOOp:\n"//ich brauche halt drei loops
			"XOR BX, BX\n"//leere BX und kein C commentar (gibt es hier nähmlich nicht!)
			"nop\n"
			"nop\n"
			"nop\n"//ein paar nops am ende
		"loop:jmp loop\n"//jep eine endlosschleife und das vierte loop
	);
}

void loop() {}
