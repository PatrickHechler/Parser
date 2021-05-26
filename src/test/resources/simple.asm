Setup:
	mov AX, 0xFF
	out 0x04, AX;festlegen der Datenrichtung
	    mov DX, 3;aktiviere nicht Pin[11];Bit[3] von den hinteren (PIN[8-13]) ist das du Trottel
LOOP:
	mov BX, 5;die Zeile zwei Zeilen darüber ist aus irgendeinem grund mit 4 leerzeichen mehr eingerückt als sie eigentlich sollte
lOoP:mov BX, AX
lOOp:;ich brauche halt drei loops
	XOR BX, BX;leere BX und kein C commentar (gibt es hier nähmlich nicht!)
	nop
	nop
	nop;ein paar nops am ende
loop:jmp loop;jep eine endlosschleife und das vierte loop
