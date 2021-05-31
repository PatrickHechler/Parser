package de.hechler.patrick.fileparser;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;

import de.hechler.patrick.fileparser.serial.Deserializer;
import de.hechler.patrick.fileparser.serial.Serializer;

public class ParserGUI extends JFrame {
	
	/** UID */
	private static final long serialVersionUID = 4993689297474827609L;
	
	private static final int VOID     = 10;
	private static final int FIRST_X  = 200;
	private static final int SECOND_X = 400;
	
	private static final int Y    = 20;
	private static final int LINE = Y + VOID;
	
	
	private static final Serializer   SERIALIZER   = new Serializer(false, true, false, true, false);
	private static final Deserializer DESERIALIZER = new Deserializer(Collections.emptyMap());
	
	public ParserGUI() {
	}
	
	@Arg("--noFinish")
	private boolean   noFinish                 = false;
	@Arg("")
	private String    template                 = null;
	@Arg("-headLines")
	private String[]  headLines                = null;
	@Arg("-tailLines")
	private String[]  tailLines                = null;
	@Arg("-lineStart")
	private String    lineStart                = null;
	@Arg("-lineEnd")
	private String    lineEnd                  = null;
	@Arg("-endTail")
	private Boolean   lineEndAlsoOnTailLines   = null;
	@Arg("-endHead")
	private Boolean   lineEndAlsoOnHeadLines   = null;
	@Arg("-startHead")
	private Boolean   lineStartAlsoOnTailLines = null;
	@Arg("-startTail")
	private Boolean   lineStartAlsoOnHeadLines = null;
	@Arg("--silent")
	private boolean   silent                   = false;
	@Arg("--print")
	private boolean   out                      = false;
	@Arg("-print")
	private boolean   err                      = false;
	@Arg("-src")
	private String    src                      = null;
	@Arg("-target")
	private String    dest                     = null;
	@Arg("--forceOverride")
	private boolean   forceOverrde             = false;
	@Arg("--charset")
	private String    charset                  = null;
	@Arg("-asmCommentSymbol")
	private String    asmCommentSymbol         = null;
	@Arg("-parsedCommentSymbol")
	private String    parsedCommentSymbol      = null;
	@Arg("-commentEndLine")
	private String    commentEndLine           = null;
	@Arg("-supressCommentExtract")
	private Boolean   supressCommentExtraction = null;
	@Arg("-startAftersWhite")
	private Boolean   startAfterWhite          = null;
	@Arg("--lineSeparator")
	private String    lineSep                  = null;
	@Arg("-replace")
	private Replace[] replaces                 = null;
	@Arg("-onlyRepWhenRep")
	private Boolean   onlyRepsAfterReplace     = null;
	@Arg("-supressReplaces")
	private Boolean   supressReplaces          = null;
	@Arg("-explicitLineSep")
	private Boolean   explicitLineSep          = null;
	
	public ParserGUI load() throws UnsupportedCharsetException {
		setTitle("ENTER ARGS");
		setLayout(null);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		final Rectangle boundsNormal = new Rectangle(0, 0, 640, 285);
		final Rectangle boundsAdvanced = new Rectangle(0, 0, 640, 465);
		final Rectangle boundsAll = new Rectangle(0, 0, 640, 885);
		setBounds(boundsNormal);
		setLocationRelativeTo(null);
		final JFileChooser argsFC = new JFileChooser();
		{
			FileFilter argsFCFilter = new FileFilter() {
				
				@Override
				public String getDescription() {
					return "[.args] files";
				}
				
				@Override
				public boolean accept(File f) {
					if (f.isHidden()) return false;
					if (f.isDirectory()) return true;
					else if ( !f.getName().toLowerCase().endsWith(".args")) return false;
					else return true;
				}
				
			};
			argsFC.addChoosableFileFilter(argsFCFilter);
			FileFilter argsFCFilter2 = new FileFilter() {
				
				@Override
				public String getDescription() {
					return "[.args] files and hidden";
				}
				
				@Override
				public boolean accept(File f) {
					if (f.isDirectory()) return true;
					else if ( !f.getName().toLowerCase().endsWith(".args")) return false;
					else return true;
				}
				
			};
			argsFC.addChoosableFileFilter(argsFCFilter2);
			FileFilter[] allFilters = argsFC.getChoosableFileFilters();
			argsFC.setFileFilter(argsFCFilter);
			for (FileFilter check : allFilters) {
				if ( !argsFCFilter.equals(check) && !argsFCFilter2.equals(check)) {
					argsFC.removeChoosableFileFilter(check);
				}
			}
		}
		
		final JButton coniformButton = new JButton("finish");
		coniformButton.addActionListener(e -> {
			String[] args = generateArgs();
			Main.main(args);
			if ( !noFinish) {
				JOptionPane.showMessageDialog(this, "finish with parsing from '" + src + "'", "FINISH", JOptionPane.INFORMATION_MESSAGE);
			}
			setVisible(false);
			Runtime r = Runtime.getRuntime();
			args = null;
			setAllNull();
			r.runFinalization();
			r.exit(0);
		});
		final JButton saveArgsButton = new JButton("save");
		saveArgsButton.addActionListener(e -> {
			int returnVal = argsFC.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = argsFC.getSelectedFile();
				if (file.isDirectory()) {
					JOptionPane.showMessageDialog(this, "i can not save to a folder! ('" + file.getPath() + "')", "NO FOLDERS!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (Files.exists(file.toPath())) {
					int chose = JOptionPane.showConfirmDialog(this, "this file exists already ('" + file.getPath() + "'), should I overwrite the file?");
					if (chose != JOptionPane.OK_OPTION) {
						return;
					}
				}
				if ( !file.getName().endsWith(".args")) {
					file = new File(file.getPath() + ".args");
				}
				try {
					SERIALIZER.writeObject(new FileOutputStream(file), this);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		int x = VOID, y = VOID;
		coniformButton.setBounds(x, y, FIRST_X, Y);
		add(coniformButton);
		x += VOID + FIRST_X;
		final Rectangle loadArgsButtonBounds;
		{
			final int xWert = (SECOND_X - VOID) / 2;
			loadArgsButtonBounds = new Rectangle(x, y, xWert, Y);
			// .setBounds(x, y, xWert, Y);
			// add(loadArgsButton);
			x += VOID + xWert;
			saveArgsButton.setBounds(x, y, xWert, Y);
			add(saveArgsButton);
		}
		
		// String src = null;
		// ("<-src> [FILE]");
		// (" to set the source file");
		final JTextPane srcText = new JTextPane();
		final JFileChooser fc = new JFileChooser();
		srcText.setText("source file");
		final JButton srcButton = new JButton("source dir");
		srcButton.addActionListener((a) -> {
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				src = fc.getSelectedFile().getPath();
				srcText.setText(src);
			}
		});
		srcText.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				src = srcText.getText();
			}
			
		});
		x = VOID;
		y += LINE;
		srcButton.setBounds(x, y, FIRST_X, Y);
		add(srcButton);
		x += VOID + FIRST_X;
		srcText.setBounds(x, y, SECOND_X, Y);
		add(srcText);
		
		// String dest = null;
		// ("<-dest> or <-target> [FILE]");
		// (" to set the target file");
		final JTextPane destText = new JTextPane();
		destText.setText("target file");
		final JButton destButton = new JButton("target dir");
		destButton.addActionListener((a) -> {
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				dest = file.getPath();
				destText.setText(dest);
			}
		});
		destText.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				dest = destText.getText();
			}
			
		});
		x = VOID;
		y += LINE;
		destButton.setBounds(x, y, FIRST_X, Y);
		x += FIRST_X + VOID;
		add(destButton);
		destText.setBounds(x, y, SECOND_X, Y);
		add(destText);
		
		// boolean forceOverrde = false;
		// ("<--force> or <--forceOverride>");
		// (" to overwrite the target file if it already exists");
		final JTextField forceOverwrite = new JTextField("exit if target file exists:", 1);
		forceOverwrite.setEditable(false);
		final JComboBox <String> forceOverComboBox = new JComboBox <String>();
		forceOverComboBox.addItem("exit if target exsist");
		forceOverComboBox.addItem("overwrite if target exsist");
		forceOverComboBox.addActionListener((l) -> {
			int i = forceOverComboBox.getSelectedIndex();
			switch (i) {
			case 0:
				this.forceOverrde = false;
				break;
			case 1:
				this.forceOverrde = true;
				break;
			default:
				throw new AssertionError("illegal selectet item-index: " + i + " legal:[0,1] item-value: '" + forceOverComboBox.getItemAt(i) + "'");
			}
		});
		x = VOID;
		y += LINE;
		forceOverwrite.setBounds(x, y, FIRST_X, Y);
		x += VOID + FIRST_X;
		forceOverComboBox.setBounds(x, y, SECOND_X, Y);
		add(forceOverwrite);
		add(forceOverComboBox);
		
		// ParserTemplate props = null;
		// ("<-ep> or <-empty>");
		// (" to use the Empty property as default");
		// ("<-aplp> or <-arduinoPotLoop>");
		// (" to set the default propertie to the arduino potential loop propertie");
		// ("<-alp> or <-arduinoAsmLoop>");
		// (" to use the Arduino with empty asm loop property as default");
		// (" the void loop() will stil be unused for the parsing, but inside it is now
		// be a asm-diriective");
		// ("<-ap> or <-arduino>");
		// (" to use the Arduino property as default");
		// ("<-mp> or <-main>");
		// (" to use the Main property as default");
		final JTextField templateOverwrite = new JTextField("your choosen template:", 1);
		templateOverwrite.setEditable(false);
		final JComboBox <String> templateOverComboBox = new JComboBox <String>();
		template = "-arduino";
		templateOverComboBox.addItem("arduino with empty loop");
		templateOverComboBox.addItem("arduino with nearly empty loop");
		templateOverComboBox.addItem("arduino with loop generated by <loop:> marks");
		templateOverComboBox.addItem("c with main method");
		templateOverComboBox.addItem("empty template");
		templateOverComboBox.addItem("no template {if you use this everithing has to be set}");
		templateOverComboBox.addActionListener((l) -> {
			int i = templateOverComboBox.getSelectedIndex();
			switch (i) {
			// templateOverComboBox.addItem("arduino with empty loop");
			// ("<-ap> or <-arduino>");
			// (" to use the Arduino property as default");
			case 0:
				template = "-arduino";
				break;
			// templateOverComboBox.addItem("arduino with loop containing empty
			// asm-directive");
			// ("<-alp> or <-arduinoAsmLoop>");
			// (" to use the Arduino with empty asm loop property as default");
			// (" the void loop() will stil be unused for the parsing, but inside it is now
			// be a asm-diriective");
			case 1:
				template = "-arduinoAsmLoop";
				break;
			// templateOverComboBox.addItem("arduino with loop generated by <loop:> marks");
			// ("<-aplp> or <-arduinoPotLoop>");
			// (" to set the default propertie to the arduino potential loop propertie");
			case 2:
				template = "-arduinoPotLoop";
				break;
			// templateOverComboBox.addItem("c with main method");
			// ("<-mp> or <-main>");
			// (" to use the Main property as default");
			case 3:
				template = "-main";
				break;
			// templateOverComboBox.addItem("empty template");
			// ("<-ep> or <-empty>");
			// (" to use the Empty property as default");
			case 4:
				template = "-empty";
				break;
			// templateOverComboBox.addItem("no template {if you use this everithing has to
			// be set}");
			case 5:
				template = null;
				break;
			default:
				throw new AssertionError("illegal selectet item-index: " + i + " legal:[0,1] item-value: '" + templateOverComboBox.getItemAt(i) + "'");
			}
		});
		x = VOID;
		y += LINE;
		templateOverwrite.setBounds(x, y, FIRST_X, Y);
		x += VOID + FIRST_X;
		templateOverComboBox.setBounds(x, y, SECOND_X, Y);
		add(templateOverwrite);
		add(templateOverComboBox);
		
		// String charset = null;
		// ("<--cs> or <--charset>");
		// (" to set the charset of the source and target file");
		final JTextField charsetTField = new JTextField("Your chosen Charset (like UTF-8):", 1);
		charsetTField.setEditable(false);
		final JTextPane charsetTPane = new JTextPane();
		charsetTPane.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				String t = charsetTPane.getText();
				if (t == null) {
					charsetTPane.setText("");
					return;
				}
				t = t.trim();
				if (t.isEmpty()) {
					charsetTPane.setText("");
					return;
				}
				try {
					Charset.forName(t);
				} catch (IllegalArgumentException e2) {
					e2.printStackTrace();
					if (e2 instanceof IllegalCharsetNameException) {
						charsetTPane.setText("IllegalCharsetName msg: '" + e2.getMessage() + "' cs: '" + t + "'");
					} else if (e2 instanceof UnsupportedCharsetException) {
						charsetTPane.setText("UnsupportedCharset msg: '" + e2.getMessage() + "' cs: '" + t + "'");
					} else {
						charsetTPane.setText("IllegalArgument msg: '" + e2.getMessage() + "' cs: '" + t + "'");
					}
				}
			}
			
		});
		charsetTPane.setText(null);
		x = VOID;
		y += LINE;
		charsetTField.setBounds(x, y, FIRST_X, Y);
		x += VOID + FIRST_X;
		charsetTPane.setBounds(x, y, SECOND_X, Y);
		
		add(charsetTField);
		add(charsetTPane);
		
		// String lineSep = null;
		// ("<--lsp> or <--lineSeparator> <CRLF> or <CR> or <LF>");
		// (" sets the line separator to the param");
		final JTextField lineSepTField = new JTextField("Your chosen line separator:", 1);
		final JComboBox <String> lineSepComboBox = new JComboBox <String>();
		lineSepTField.setEditable(false);
		String sls = System.lineSeparator();
		lineSepComboBox.addItem("system line separator: (" + ("\r\n".equals(sls) ? "CR LF" : "\n".equals(sls) ? "LF" : "\r".equals(sls) ? "CR" : "unknown") + ")");
		lineSepComboBox.addItem("CR LF (Windows)");
		lineSepComboBox.addItem("CR (MacOs)");
		lineSepComboBox.addItem("LF (Linux)");
		lineSepComboBox.addActionListener((l) -> {
			int i = lineSepComboBox.getSelectedIndex();
			switch (i) {
			case 0:
				lineSep = null;
				break;
			case 1:
				lineSep = "CR LF";
				break;
			case 2:
				lineSep = "CR";
				break;
			case 3:
				lineSep = "LF";
				break;
			default:
				throw new AssertionError("illegal selectet item-index: " + i + " legal:[0,1] item-value: '" + lineSepComboBox.getItemAt(i) + "'");
			}
		});
		x = VOID;
		y += LINE;
		lineSepTField.setBounds(x, y, FIRST_X, Y);
		x += VOID + FIRST_X;
		lineSepComboBox.setBounds(x, y, SECOND_X, Y);
		add(lineSepTField);
		add(lineSepComboBox);
		
		final String normalOptionsText = "normal options";
		final String advancedOptionsText = "advanced options";
		final String allOptionsText = "all options";
		final JButton advancedOptionsButton = new JButton(advancedOptionsText);
		x = VOID;
		y += LINE;
		advancedOptionsButton.setBounds(x, y, FIRST_X, Y);
		
		{// advanced options
			final List <Component> advancedOptionsComps = new ArrayList <>();
			// String[] headLines = null;
			// ("<-hl> or <-headLines> [END_MAGIX] <arg>* [END_MAGIX]");
			// (" to set the head lines (the END_MAGIX will not be added on the start or end
			// of the head lines)");
			final JButton headLinesButton = new JButton("set head lines");
			final JFrame headLinesFrame = new JFrame("head lines:");
			final JTextArea headLinesTestArea = new JTextArea();
			headLinesFrame.addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosed(WindowEvent e) {
					String t = headLinesTestArea.getText();
					if (t == null || t.isEmpty()) headLines = null;
					else headLines = t.split("(\r\n)|\r|\n");
				};
				
			});
			headLinesFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
			headLinesFrame.setVisible(false);
			headLinesFrame.setLayout(null);
			headLinesFrame.setBounds(0, 0, 500, 500);
			headLinesFrame.setLocationRelativeTo(null);
			headLinesFrame.add(headLinesTestArea);
			headLinesFrame.addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
				
				@Override
				public void ancestorResized(HierarchyEvent e) {
					headLinesTestArea.setBounds(headLinesFrame.getBounds());
				}
				
			});
			headLinesTestArea.setBounds(0, 0, 500, 500);
			headLinesButton.addActionListener(a -> {
				headLinesFrame.setVisible(true);
			});
			final JComboBox <String> headLinesComboBox = new JComboBox <String>();
			headLinesComboBox.addItem("not choosen");
			headLinesComboBox.addItem("head lines are enabled");
			headLinesComboBox.addItem("head lines are disabled");
			x = VOID;
			y += LINE;
			headLinesButton.setBounds(x, y, FIRST_X, Y);
			x += FIRST_X + VOID;
			headLinesComboBox.setBounds(x, y, SECOND_X, Y);
			advancedOptionsComps.add(headLinesButton);
			advancedOptionsComps.add(headLinesComboBox);
			add(headLinesButton);
			add(headLinesComboBox);
			
			// String[] tailLines = null;
			// ("<-tl> or <-tailLines> [END_MAGIX] <arg>* [END_MAGIX]");
			// (" to set the tail lines (the END_MAGIX will not be added on the start or end
			// of the tail lines)");
			final JButton tailLinesButton = new JButton("set head lines");
			final JFrame tailLinesFrame = new JFrame("head lines:");
			final JTextArea tailLinesTestArea = new JTextArea();
			tailLinesFrame.addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosed(WindowEvent e) {
					String t = tailLinesTestArea.getText();
					if (t == null || t.isEmpty()) headLines = null;
					else headLines = t.split("(\r\n)|\r|\n");
				};
				
			});
			tailLinesFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
			tailLinesFrame.setVisible(false);
			tailLinesFrame.setLayout(null);
			tailLinesFrame.setBounds(0, 0, 500, 500);
			tailLinesFrame.setLocationRelativeTo(null);
			tailLinesFrame.add(tailLinesTestArea);
			tailLinesFrame.addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
				
				@Override
				public void ancestorResized(HierarchyEvent e) {
					tailLinesTestArea.setBounds(tailLinesFrame.getBounds());
				}
				
			});
			tailLinesTestArea.setBounds(0, 0, 500, 500);
			tailLinesButton.addActionListener(a -> {
				tailLinesFrame.setVisible(true);
			});
			final JComboBox <String> tailLinesComboBox = new JComboBox <String>();
			tailLinesComboBox.addItem("not choosen");
			tailLinesComboBox.addItem("head lines are enabled");
			tailLinesComboBox.addItem("head lines are disabled");
			x = VOID;
			y += LINE;
			tailLinesButton.setBounds(x, y, FIRST_X, Y);
			x += FIRST_X + VOID;
			tailLinesComboBox.setBounds(x, y, SECOND_X, Y);
			advancedOptionsComps.add(tailLinesButton);
			advancedOptionsComps.add(tailLinesComboBox);
			add(tailLinesButton);
			add(tailLinesComboBox);
			
			// String lineStart = null;
			// ("<-ls> or <-lineStart> [LINE_START]");
			// (" to set the start of each printed line");
			final JTextPane lineStartText = new JTextPane();
			lineStartText.setText("your chosen line start");
			final JButton lineStartButton = new JButton("reset line start");
			lineStartButton.addActionListener((a) -> {
				lineStartText.setText("your chosen line start");
				lineStart = null;
			});
			lineSepTField.addFocusListener(new FocusAdapter() {
				
				@Override
				public void focusLost(FocusEvent e) {
					lineStart = lineStartText.getText();
				}
				
			});
			x = VOID;
			y += LINE;
			lineStartButton.setBounds(x, y, FIRST_X, Y);
			x += FIRST_X + VOID;
			lineStartText.setBounds(x, y, SECOND_X, Y);
			advancedOptionsComps.add(lineStartText);
			advancedOptionsComps.add(lineStartButton);
			add(lineStartText);
			add(lineStartButton);
			
			// String lineEnd = null;
			// ("<-le> or <-lineEnd> [LINE_END]");
			// (" to set the end of each printed line, this will be printed before the
			// (comment if not deactivated) and commentEndLine");
			final JTextPane lineEndText = new JTextPane();
			lineEndText.setText("your chosen line end");
			final JButton lineEndButton = new JButton("reset line end");
			lineEndButton.addActionListener((a) -> {
				lineEndText.setText("your chosen line end");
				lineStart = null;
			});
			lineEndText.addFocusListener(new FocusAdapter() {
				
				@Override
				public void focusLost(FocusEvent e) {
					lineEnd = lineEndText.getText();
				}
				
			});
			x = VOID;
			y += LINE;
			lineEndButton.setBounds(x, y, FIRST_X, Y);
			x += FIRST_X + VOID;
			lineEndText.setBounds(x, y, SECOND_X, Y);
			advancedOptionsComps.add(lineEndText);
			advancedOptionsComps.add(lineEndButton);
			add(lineEndText);
			add(lineEndButton);
			
			// Boolean startAfterWhite = null;
			// ("<-saw> or <-startAftersWhite> <true>/<1> or <false>/<0>");
			// (" to set if the line start will be set after the whitespace at the begin of
			// the line or before the whitespace of the line");
			final JTextField startAfterWhiteText = new JTextField("line start will be set at:");
			startAfterWhiteText.setEditable(false);
			final JComboBox <String> startAfterWhiteComboBox = new JComboBox <String>(new String[] {"nothing choosen", "line start is after the whitespace", "line start is set an the real line start" });
			startAfterWhiteComboBox.addActionListener(e -> {
				int i = startAfterWhiteComboBox.getSelectedIndex();
				switch (i) {
				case 0:
					startAfterWhite = null;
					break;
				case 1:
					startAfterWhite = true;
					break;
				case 2:
					startAfterWhite = false;
					break;
				default:
					throw new AssertionError("illegal selectet item-index: " + i + " legal:[0,1] item-value: '" + forceOverComboBox.getItemAt(i) + "'");
				}
			});
			x = VOID;
			y += LINE;
			startAfterWhiteText.setBounds(x, y, FIRST_X, Y);
			x += FIRST_X + VOID;
			startAfterWhiteComboBox.setBounds(x, y, SECOND_X, Y);
			advancedOptionsComps.add(startAfterWhiteText);
			advancedOptionsComps.add(startAfterWhiteComboBox);
			add(startAfterWhiteText);
			add(startAfterWhiteComboBox);
			
			x = VOID;
			y += LINE;
			final JButton allOptionsButton = new JButton(allOptionsText);
			allOptionsButton.setBounds(x, y, FIRST_X, Y);
			advancedOptionsComps.add(allOptionsButton);
			add(allOptionsButton);
			{
				List <Component> allOptionsComps = new ArrayList <Component>();
				
				// String asmCommentSymbol = null;
				// ("<-asmCommentSymbol> or <-acs> [COMMENT_SYMBOL]");
				// (" to set the Assembler comment symbol to COMMENT_SYMBOL");
				final JButton asmCommentSymbolButton = new JButton("reset unparsed comment");
				final JTextPane asmCommentSymbolTextPane = new JTextPane();
				asmCommentSymbolTextPane.setText("define your unparsed commend symbol here");
				asmCommentSymbolTextPane.addFocusListener(new FocusAdapter() {
					
					@Override
					public void focusLost(FocusEvent e) {
						asmCommentSymbol = asmCommentSymbolTextPane.getText();
					}
					
				});
				asmCommentSymbolButton.addActionListener(a -> {
					asmCommentSymbolTextPane.setText("define your unparsed commend symbol here");
				});
				x = VOID;
				y += LINE;
				asmCommentSymbolButton.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				asmCommentSymbolTextPane.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(asmCommentSymbolButton);
				advancedOptionsComps.add(asmCommentSymbolTextPane);
				add(asmCommentSymbolButton);
				add(asmCommentSymbolTextPane);
				
				// String parsedCommentSymbol = null;
				// ("<-parsedCommentSymbol> or <-pcs> [COMMENT_SYMBOL]");
				// (" to set the parsed comment symbol to COMMENT_SYMBOL");
				final JButton parsedCommentSymbolButton = new JButton("reset parsed comment");
				final JTextPane parsedCommentSymbolTextPane = new JTextPane();
				parsedCommentSymbolTextPane.setText("define your parsed commend symbol here");
				parsedCommentSymbolTextPane.addFocusListener(new FocusAdapter() {
					
					@Override
					public void focusLost(FocusEvent e) {
						asmCommentSymbol = parsedCommentSymbolTextPane.getText();
					}
					
				});
				parsedCommentSymbolButton.addActionListener(a -> {
					parsedCommentSymbolTextPane.setText("define your parsed commend symbol here");
				});
				x = VOID;
				y += LINE;
				parsedCommentSymbolButton.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				parsedCommentSymbolTextPane.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(parsedCommentSymbolButton);
				advancedOptionsComps.add(parsedCommentSymbolTextPane);
				add(parsedCommentSymbolButton);
				add(parsedCommentSymbolTextPane);
				
				// String commentEndLine = null;
				// ("<-commentEndLine> or <-cel> [COMMENT_END_LINE]");
				// (" to print at the very end of each line this (after the endLine (and of course also after the extracted comment if the comments are not disabled))");
				final JButton commentEndLineButton = new JButton("reset comment end Line");
				final JTextPane commentEndLineTextPane = new JTextPane();
				commentEndLineTextPane.setText("define your comment end Line here");
				commentEndLineTextPane.addFocusListener(new FocusAdapter() {
					
					@Override
					public void focusLost(FocusEvent e) {
						asmCommentSymbol = commentEndLineTextPane.getText();
					}
					
				});
				commentEndLineButton.addActionListener(a -> {
					commentEndLineTextPane.setText("define your parsed commend symbol here");
				});
				x = VOID;
				y += LINE;
				commentEndLineButton.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				commentEndLineTextPane.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(commentEndLineButton);
				advancedOptionsComps.add(commentEndLineTextPane);
				add(commentEndLineButton);
				add(commentEndLineTextPane);
				
				// Boolean supressCommentExtraction = null;
				// ("<-scp> or <-supressCommentParsing> <true>/<1> or <false>/<0>");
				// (" to tell the parser that Assembler comments (by default ';') should be placed after the endLine (but not after the lineSeparator or endCommentLine)");
				final JTextField supressCommentExtractionTF = new JTextField("select extraction of cmments");
				supressCommentExtractionTF.setEditable(false);
				final JComboBox <String> supressCommentExtractionCB = new JComboBox <String>();
				supressCommentExtractionCB.addItem("nothing choosen");
				supressCommentExtractionCB.addItem("comments will be extracted behind the end line");
				supressCommentExtractionCB.addItem("comments will be placed before the end line");
				supressCommentExtractionCB.addActionListener(a -> {
					int i = supressCommentExtractionCB.getSelectedIndex();
					switch (i) {
					case 0:
						supressCommentExtraction = null;
						break;
					case 1:
						supressCommentExtraction = false;
						break;
					case 2:
						supressCommentExtraction = true;
						break;
					default:
						throw new InternalError("illegal selected index: " + i);
					}
				});
				x = VOID;
				y += LINE;
				supressCommentExtractionTF.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				supressCommentExtractionCB.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(supressCommentExtractionTF);
				advancedOptionsComps.add(supressCommentExtractionCB);
				add(supressCommentExtractionTF);
				add(supressCommentExtractionCB);
				
				// List <Replace> replaces = new ArrayList <>();
				// ("<-rep> or <-replace> [REGEX] [REPLACEMENT]");
				// (" to try replacing in every line");
				// (" this option can be used multiple times");
				// (" when used multiple times the first replac-args will replace earlier and the ones after them");
				// (" the later replace-args will try to replace the replaced");
				// (" the replacement will be treated as a single line, even if there are line seperators inside");
				// (" these potential line separators will be overwritten with the overgiven line separator (<--lineSeparator>)");
				// (" for the line start and line end it will be only one line");
				// (" the replacement will be called befor lineStart or lineEnd had been set");
				replacesFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
				replacesFrame.setVisible(false);
				replacesFrame.setLayout(null);
				replacesFrame.setBounds(0, 0, 550, 750);
				replacesFrame.setLocationRelativeTo(null);
				rebuildReplaces(true);
				replacesManageText.setEditable(false);
				replacesManageButton.addActionListener(e -> {
					rebuildReplaces(false);
				});
				{
					repAddReplace.addActionListener(e -> {
						// I was stupid, so I needed this:
						// if (replaces != null && replaces.length > 0 && replaces[replaces.length - 1] == null) {
						// System.err.println("no action: " + e);
						// return;
						// }
						// System.err.println("action: " + e);
						if (replaces != null) {
							Replace[] zw = new Replace[replaces.length + 1];
							System.arraycopy(replaces, 0, zw, 0, replaces.length);
							zw[replaces.length] = new Replace("your regex", "your replacement");
							replaces = zw;
						} else {
							replaces = new Replace[1];
							replaces[0] = new Replace("your regex", "your replacement");
						}
						rebuildReplaces(false);
					});
					repAddReplace.removeActionListener(repAddReplace.getActionListeners().length > 1 ? repAddReplace.getActionListeners()[0] : null);
					repDeleteAllReplaces.addActionListener(e -> {
						replaces = null;
						rebuildReplaces(false);
					});
					repDeleteAllReplaces.removeActionListener(repDeleteAllReplaces.getActionListeners().length > 1 ? repDeleteAllReplaces.getActionListeners()[0] : null);
					// repAddReplace.setBounds(VOID + x1 + VOID, yy, x2 + x3 + VOID, Y);
					replacesFrame.add(repAddReplace);
					// repDeleteAllReplaces.setBounds(VOID, yy, x1, Y);
					replacesFrame.add(repDeleteAllReplaces);
				}
				x = VOID;
				y += LINE;
				replacesManageText.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				replacesManageButton.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(replacesManageText);
				advancedOptionsComps.add(replacesManageButton);
				add(replacesManageText);
				add(replacesManageButton);
				
				// Boolean continueAfterReplaces = null;
				// ("<-orwr> or <-onlyRepWhenRep> <true>/<1> or <false>/<0>");
				// (" to enble/disable if the parse should stop modifiing the line after all replacements are done and they changed something");
				final JTextField onlyRepalcesWhenReplacedTF = new JTextField("after replaced finish and changed:");
				onlyRepalcesWhenReplacedTF.setEditable(false);
				final JComboBox <String> onlyReplacesWhenReplacedCB = new JComboBox <String>();
				onlyReplacesWhenReplacedCB.addItem("nothing choosen");
				onlyReplacesWhenReplacedCB.addItem("after the replaces replaced something continues with end/start line");
				onlyReplacesWhenReplacedCB.addItem("after the replaces replaced something the parser goes to the next line");
				onlyReplacesWhenReplacedCB.addActionListener(a -> {
					int i = onlyReplacesWhenReplacedCB.getSelectedIndex();
					switch (i) {
					case 0:
						onlyRepsAfterReplace = null;
						break;
					case 1:
						onlyRepsAfterReplace = false;
						break;
					case 2:
						onlyRepsAfterReplace = true;
						break;
					default:
						throw new InternalError("illegal selected index: " + i);
					}
				});
				x = VOID;
				y += LINE;
				onlyRepalcesWhenReplacedTF.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				onlyReplacesWhenReplacedCB.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(onlyRepalcesWhenReplacedTF);
				advancedOptionsComps.add(onlyReplacesWhenReplacedCB);
				add(onlyRepalcesWhenReplacedTF);
				add(onlyReplacesWhenReplacedCB);
				
				
				// Boolean supressReplaces = null;
				// ("<-sr> or <-supressReplaces> <true>/<1> or <false>/<0>");
				// (" to supress(true/1) or allow(false/0) replaces"); Boolean
				final JTextField supressReplacesTextField = new JTextField("supress all replaces:");
				supressReplacesTextField.setEditable(false);
				final JComboBox <String> supressReplacesComboBox = new JComboBox <String>();
				supressReplacesComboBox.addItem("nothing choosen");
				supressReplacesComboBox.addItem("ignpore all replaces (act like there are none)");
				supressReplacesComboBox.addItem("use the replaces to replace");
				supressReplacesComboBox.addActionListener(a -> {
					int i = supressReplacesComboBox.getSelectedIndex();
					switch (i) {
					case 0:
						supressReplaces = null;
						break;
					case 1:
						supressReplaces = false;
						break;
					case 2:
						supressReplaces = true;
						break;
					default:
						throw new InternalError("illegal selected index: " + i);
					}
				});
				x = VOID;
				y += LINE;
				supressReplacesTextField.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				supressReplacesComboBox.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(supressReplacesTextField);
				advancedOptionsComps.add(supressReplacesComboBox);
				add(supressReplacesTextField);
				add(supressReplacesComboBox);
				
				// explicitLineSep = null;
				// ("<-els> or <-explicitLineSep> <true>/<1> or <false>/<0>");
				// (" to set if the lines need a explicit line separator (<true>/<1>) or if it should be appanded after each line");
				// (" if it is set <true>/<1> the lines will be read with a line separator");
				final JTextField explicitLineSepTextField = new JTextField("is the line separator explicit?");
				explicitLineSepTextField.setEditable(false);
				final JComboBox <String> explicitLineSepComboBox = new JComboBox <String>();
				explicitLineSepComboBox.addItem("nothing choosen");
				explicitLineSepComboBox.addItem("every line will need a line separator to separate the line");
				explicitLineSepComboBox.addItem("the line separator will be automaticly placed after each line");
				explicitLineSepComboBox.addActionListener(a -> {
					int i = explicitLineSepComboBox.getSelectedIndex();
					switch (i) {
					case 0:
						explicitLineSep = null;
						break;
					case 1:
						explicitLineSep = false;
						break;
					case 2:
						explicitLineSep = true;
						break;
					default:
						throw new InternalError("illegal selected index: " + i);
					}
				});
				x = VOID;
				y += LINE;
				explicitLineSepTextField.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				explicitLineSepComboBox.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(explicitLineSepTextField);
				advancedOptionsComps.add(explicitLineSepComboBox);
				add(explicitLineSepTextField);
				add(explicitLineSepComboBox);
				
				// Boolean lineStartAlsoOnHeadLines = null;
				// ("<-soh> or <-startHead> <true>/<1> or <false>/<0>");
				// (" to enable <true>/<1> or disable <false>/<0> the startLine for the head");
				final JTextField lineStartAlsoOnHeadLinesTextField = new JTextField("line start on head lines");
				lineStartAlsoOnHeadLinesTextField.setEditable(false);
				final JComboBox <String> lineStartAlsoOnHeadLinesComboBox = new JComboBox <String>();
				lineStartAlsoOnHeadLinesComboBox.addItem("nothing choosen");
				lineStartAlsoOnHeadLinesComboBox.addItem("the head lines will have no line start");
				lineStartAlsoOnHeadLinesComboBox.addItem("the line start will be added to the start of each head line");
				lineStartAlsoOnHeadLinesComboBox.addActionListener(a -> {
					int i = lineStartAlsoOnHeadLinesComboBox.getSelectedIndex();
					switch (i) {
					case 0:
						lineStartAlsoOnHeadLines = null;
						break;
					case 1:
						lineStartAlsoOnHeadLines = false;
						break;
					case 2:
						lineStartAlsoOnHeadLines = true;
						break;
					default:
						throw new InternalError("illegal selected index: " + i);
					}
				});
				x = VOID;
				y += LINE;
				lineStartAlsoOnHeadLinesTextField.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				lineStartAlsoOnHeadLinesComboBox.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(lineStartAlsoOnHeadLinesTextField);
				advancedOptionsComps.add(lineStartAlsoOnHeadLinesComboBox);
				add(lineStartAlsoOnHeadLinesTextField);
				add(lineStartAlsoOnHeadLinesComboBox);
				
				// Boolean lineStartAlsoOnTailLines = null;
				// ("<-sot> or <-startTail> <true>/<1> or <false>/<0>");
				// (" to enable <true>/<1> or disable <false>/<0> the startLine for the tail");
				final JTextField lineStartAlsoOnTailLinesTextField = new JTextField("line start on tail lines");
				lineStartAlsoOnTailLinesTextField.setEditable(false);
				final JComboBox <String> lineStartAlsoOnTailLinesComboBox = new JComboBox <String>();
				lineStartAlsoOnTailLinesComboBox.addItem("nothing choosen");
				lineStartAlsoOnTailLinesComboBox.addItem("the tail lines will have no line start");
				lineStartAlsoOnTailLinesComboBox.addItem("the line start will be added to the start of each tail line");
				lineStartAlsoOnTailLinesComboBox.addActionListener(a -> {
					int i = lineStartAlsoOnTailLinesComboBox.getSelectedIndex();
					switch (i) {
					case 0:
						lineStartAlsoOnTailLines = null;
						break;
					case 1:
						lineStartAlsoOnTailLines = false;
						break;
					case 2:
						lineStartAlsoOnTailLines = true;
						break;
					default:
						throw new InternalError("illegal selected index: " + i);
					}
				});
				x = VOID;
				y += LINE;
				lineStartAlsoOnTailLinesTextField.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				lineStartAlsoOnTailLinesComboBox.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(lineStartAlsoOnTailLinesTextField);
				advancedOptionsComps.add(lineStartAlsoOnTailLinesComboBox);
				add(lineStartAlsoOnTailLinesTextField);
				add(lineStartAlsoOnTailLinesComboBox);
				
				// Boolean lineEndAlsoOnHeadLines = null;
				// ("<-eoh> or <-endHead> <true>/<1> or <false>/<0>");
				// (" to enable <true>/<1> or disable <false>/<0> the endLine for the head");
				final JTextField lineEndAlsoOnHeadLinesTextField = new JTextField("line end on head lines");
				lineEndAlsoOnHeadLinesTextField.setEditable(false);
				final JComboBox <String> lineEndAlsoOnHeadLinesComboBox = new JComboBox <String>();
				lineEndAlsoOnHeadLinesComboBox.addItem("nothing choosen");
				lineEndAlsoOnHeadLinesComboBox.addItem("the head lines will have no line end");
				lineEndAlsoOnHeadLinesComboBox.addItem("the line end will be added to the end of each head line");
				lineEndAlsoOnHeadLinesComboBox.addActionListener(a -> {
					int i = lineEndAlsoOnHeadLinesComboBox.getSelectedIndex();
					switch (i) {
					case 0:
						lineEndAlsoOnHeadLines = null;
						break;
					case 1:
						lineEndAlsoOnHeadLines = false;
						break;
					case 2:
						lineEndAlsoOnHeadLines = true;
						break;
					default:
						throw new InternalError("illegal selected index: " + i);
					}
				});
				x = VOID;
				y += LINE;
				lineEndAlsoOnHeadLinesTextField.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				lineEndAlsoOnHeadLinesComboBox.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(lineEndAlsoOnHeadLinesTextField);
				advancedOptionsComps.add(lineEndAlsoOnHeadLinesComboBox);
				add(lineEndAlsoOnHeadLinesTextField);
				add(lineEndAlsoOnHeadLinesComboBox);
				
				// Boolean lineEndAlsoOnTailLines = null;
				// ("<-eot> or <-endTail> <true>/<1> or <false>/<0>");
				// (" to enable <true>/<1> or disable <false>/<0> the endLine for the tail");
				final JTextField lineEndAlsoOnTailLinesTextField = new JTextField("line end on tail lines");
				lineEndAlsoOnTailLinesTextField.setEditable(false);
				final JComboBox <String> lineEndAlsoOnTailLinesComboBox = new JComboBox <String>();
				lineEndAlsoOnTailLinesComboBox.addItem("nothing choosen");
				lineEndAlsoOnTailLinesComboBox.addItem("the tail lines will have no line end");
				lineEndAlsoOnTailLinesComboBox.addItem("the line end will be added to the end of each tail line");
				lineEndAlsoOnTailLinesComboBox.addActionListener(a -> {
					int i = lineEndAlsoOnTailLinesComboBox.getSelectedIndex();
					switch (i) {
					case 0:
						lineEndAlsoOnTailLines = null;
						break;
					case 1:
						lineEndAlsoOnTailLines = false;
						break;
					case 2:
						lineEndAlsoOnTailLines = true;
						break;
					default:
						throw new InternalError("illegal selected index: " + i);
					}
				});
				x = VOID;
				y += LINE;
				lineEndAlsoOnTailLinesTextField.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				lineEndAlsoOnTailLinesComboBox.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(lineEndAlsoOnTailLinesTextField);
				advancedOptionsComps.add(lineEndAlsoOnTailLinesComboBox);
				add(lineEndAlsoOnTailLinesTextField);
				add(lineEndAlsoOnTailLinesComboBox);
				
				// boolean noFinish = true; ("<--noFinish> or <--nf>");
				// (" to print no finish message after finishing with parsing");
				final JTextField noFinishField = new JTextField("finish message:");
				noFinishField.setEditable(false);
				final JComboBox <String> noFinishComboBox = new JComboBox <String>();
				noFinishComboBox.addItem("after the parsing a finish message will come");
				noFinishComboBox.addItem("there will be no finish message");
				noFinishComboBox.addActionListener(a -> {
					int i = noFinishComboBox.getSelectedIndex();
					switch (i) {
					case 0:
						noFinish = false;
						break;
					case 1:
						noFinish = true;
						break;
					default:
						throw new InternalError("illegal selected index: " + i);
					}
				});
				x = VOID;
				y += LINE;
				noFinishField.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				noFinishComboBox.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(noFinishField);
				advancedOptionsComps.add(noFinishComboBox);
				add(noFinishField);
				add(noFinishComboBox);
				
				// boolean out = false;
				// ("<--print>");
				// (" to print the parsed also to the default out");
				// boolean err = false;
				// ("<-print>");
				// (" to print the parsed also to the default err");
				// boolean silent = false;
				// ("<--silent> or <--s>");
				// (" to print the parsed not to the generated target file (if no target is set it will be generated from the name of the source file, but only if silent is not set)");
				final JTextField outPrintField = new JTextField("also print parsed:");
				outPrintField.setEditable(false);
				final JComboBox <String> outPrintComboBox = new JComboBox <String>();
				outPrintComboBox.addItem("nothing will be printed (except of the target file)");
				outPrintComboBox.addItem("the default out will be printed too");
				outPrintComboBox.addItem("the default err will be printed too");
				outPrintComboBox.addItem("the default out and the default err will be printed too");
				outPrintComboBox.addItem("the parser will output nothing (useless)");
				outPrintComboBox.addItem("only the default out will be printed");
				outPrintComboBox.addItem("only the default err will be printed too");
				outPrintComboBox.addItem("only the default out and the default err will be printed too");
				outPrintComboBox.addActionListener(a -> {
					int i = outPrintComboBox.getSelectedIndex();
					switch (i) {
					case 0:
						out = false;
						err = false;
						silent = false;
						break;
					case 1:
						out = true;
						err = false;
						silent = false;
						break;
					case 2:
						out = false;
						err = true;
						silent = false;
						break;
					case 3:
						out = true;
						err = true;
						silent = false;
						break;
					case 4:
						out = false;
						err = false;
						silent = true;
						break;
					case 5:
						out = true;
						err = false;
						silent = true;
						break;
					case 6:
						out = false;
						err = true;
						silent = true;
						break;
					case 7:
						out = true;
						err = true;
						silent = true;
						break;
					default:
						throw new InternalError("illegal selected index: " + i);
					}
				});
				x = VOID;
				y += LINE;
				outPrintField.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				outPrintComboBox.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(outPrintField);
				advancedOptionsComps.add(outPrintComboBox);
				add(outPrintField);
				add(outPrintComboBox);
				
				
				final JButton loadArgsButton = new JButton("load");
				loadArgsButton.addActionListener(e -> {
					int returnVal = argsFC.showOpenDialog(this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = argsFC.getSelectedFile();
						if ( !Files.exists(file.toPath())) {
							JOptionPane.showMessageDialog(this, "the file '" + file.getPath() + "' does not exsit!", "NO SUCH FILE!", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if (file.isDirectory()) {
							JOptionPane.showMessageDialog(this, "i can not load from a folder! ('" + file.getPath() + "')", "NO FOLDERS!", JOptionPane.ERROR_MESSAGE);
							return;
						}
						try {
							DESERIALIZER.overwriteObject(new FileInputStream(file), this);
							{
								// private boolean noFinish = false;
								noFinishComboBox.setSelectedIndex(noFinish ? 1 : 0);
								// private String template = null;
								/*
								//@formatter:off
				template = "-arduino";
0							templateOverComboBox.addItem("arduino with empty loop");
				template = "-arduinoAsmLoop";
1							templateOverComboBox.addItem("arduino with nearly empty loop");
				template = "-arduinoPotLoop";
2							templateOverComboBox.addItem("arduino with loop generated by <loop:> marks");
				template = "-main";
3							templateOverComboBox.addItem("c with main method");
				template = "-empty";
4							templateOverComboBox.addItem("empty template");
				template = null;
5							templateOverComboBox.addItem("no template {if you use this everithing has to be set}");
								//@formatter:on
								 */
								if (template == null) {
									templateOverComboBox.setSelectedIndex(5);
								} else {
									switch (template) {
									case "-arduino":
										templateOverComboBox.setSelectedIndex(0);
										break;
									case "-arduinoAsmLoop":
										templateOverComboBox.setSelectedIndex(1);
										break;
									case "-arduinoPotLoop":
										templateOverComboBox.setSelectedIndex(2);
										break;
									case "-main":
										templateOverComboBox.setSelectedIndex(3);
										break;
									case "-empty":
										templateOverComboBox.setSelectedIndex(4);
										break;
									default:
										throw new IllegalStateException("illegal template: '" + template + "'");
									}
								}
								// private String[] headLines = null;
								if (headLines != null) {
									StringBuilder build = new StringBuilder();
									if (headLines.length > 0) {
										build.append(headLines[0]);
									}
									for (int i = 1; i < headLines.length; i ++ ) {
										build.append('\n').append(headLines[i]);
									}
								} else {
									headLinesTestArea.setText(null);
								}
								
								// private String[] tailLines = null;
								if (tailLines != null) {
									StringBuilder build = new StringBuilder();
									if (tailLines.length > 0) {
										build.append(tailLines[0]);
									}
									for (int i = 1; i < tailLines.length; i ++ ) {
										build.append('\n').append(tailLines[i]);
									}
								} else {
									tailLinesTestArea.setText("");
								}
								
								// private String lineStart = null;
								if (lineStart == null) {
									lineStartText.setText("your chosen line start");
								} else {
									lineStartText.setText(lineStart);
								}
								// private String lineEnd = null;
								if (lineEnd == null) {
									lineEndText.setText("your chosen line end");
								} else {
									lineEndText.setText(lineEnd);
								}
								
								// private Boolean lineEndAlsoOnTailLines = null;
								lineEndAlsoOnTailLinesComboBox.setSelectedIndex(lineEndAlsoOnTailLines == null ? 0 : lineEndAlsoOnTailLines ? 2 : 1); // null=0, false=1, true=2
								
								// private Boolean lineEndAlsoOnHeadLines = null;
								lineEndAlsoOnHeadLinesComboBox.setSelectedIndex(lineEndAlsoOnHeadLines == null ? 0 : lineEndAlsoOnHeadLines ? 2 : 1); // null=0, false=1, true=2
								
								// private Boolean lineStartAlsoOnTailLines = null;
								lineStartAlsoOnTailLinesComboBox.setSelectedIndex(lineStartAlsoOnTailLines == null ? 0 : lineStartAlsoOnTailLines ? 2 : 1); // null=0, false=1, true=2
								
								// private Boolean lineStartAlsoOnHeadLines = null;
								lineStartAlsoOnHeadLinesComboBox.setSelectedIndex(lineStartAlsoOnHeadLines == null ? 0 : lineStartAlsoOnHeadLines ? 2 : 1); // null=0, false=1, true=2
								
								// private boolean silent = false;
								// private boolean out = false;
								// private boolean err = false;
								/*
//@formatter:off
					case 0: out = false,err = false,silent = false;
					case 1: out = true ,err = false,silent = false;
					case 2: out = false,err = true, silent = false;
					case 3: out = true ,err = true, silent = false;
					case 4: out = false,err = false,silent = true ;
					case 5: out = true ,err = false,silent = true ;
					case 6: out = false,err = true, silent = true ;
					case 7: out = true ,err = true, silent = true ;
								 */
								outPrintComboBox.setSelectedIndex(
										silent 	? (err  ? (out ? 7 : 6)
														: (out ? 5 : 4)) 
												: (err  ? (out ? 3 : 2)
														: (out ? 1 : 0)));
//@formatter:on
								
								// private String src = null;
								if (src == null) {
									srcText.setText("source file");
								} else {
									srcText.setText(src);
								}
								// private String dest = null;
								if (dest == null) {
									destText.setText("target file");
								} else {
									destText.setText(dest);
								}
								
								// private boolean forceOverrde = false;
								forceOverComboBox.setSelectedIndex(forceOverrde ? 1 : 0);
								
								// private String charset = null;
								if (charset != null) {
									try {
										Charset.forName(charset);
									} catch (IllegalArgumentException e1) {
										JOptionPane.showMessageDialog(this, "the charset could not be loaded", e1.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
									}
									charsetTPane.setText(charset);
								} else {
									charsetTPane.setText("");
								}
								
								// private String asmCommentSymbol = null;
								if (asmCommentSymbol != null) {
									asmCommentSymbolTextPane.setText(asmCommentSymbol);
								} else {
									asmCommentSymbolTextPane.setText("define your unparsed commend symbol here");
								}
								
								// private String parsedCommentSymbol = null;
								if (parsedCommentSymbol != null) {
									parsedCommentSymbolTextPane.setText(parsedCommentSymbol);
								} else {
									parsedCommentSymbolTextPane.setText("define your parsed commend symbol here");
								}
								
								// private String commentEndLine = null;
								if (commentEndLine != null) {
									commentEndLineTextPane.setText(commentEndLine);
								} else {
									commentEndLineTextPane.setText("define your comment end Line here");
								}
								
								// private Boolean supressCommentExtraction = null;
								supressCommentExtractionCB.setSelectedIndex(supressCommentExtraction == null ? 0 : (supressCommentExtraction ? 2 : 1)); // null=0,false=1,true=2
								
								// private Boolean startAfterWhite = null;
								startAfterWhiteComboBox.setSelectedIndex(startAfterWhite == null ? 0 : (startAfterWhite ? 1 : 2)); // null=0,true=1,false=2
								
								// private String lineSep = null;
								/*
//@formatter:off
			case 0:
				lineSep = null;
				break;
			case 1:
				lineSep = "CR LF";
				break;
			case 2:
				lineSep = "CR";
				break;
			case 3:
				lineSep = "LF";
//@formatter:on
								 */
								if (lineSep != null) {
									switch (lineSep) {
									case "CR LF":
										lineSepComboBox.setSelectedIndex(1);
										break;
									case "CR":
										lineSepComboBox.setSelectedIndex(2);
										break;
									case "LF":
										lineSepComboBox.setSelectedIndex(3);
										break;
									default:
										lineSepComboBox.setSelectedIndex(0);
										JOptionPane.showMessageDialog(this, "illegal line separator: {'" + lineSep.replaceAll("\r", "\\[CR\\]").replaceAll("\n", "\\[LF\\]").replaceAll("\t", "\\[TAB\\]") + "'}",
												"illegal line separator", JOptionPane.ERROR_MESSAGE);
									}
								} else {
									lineSepComboBox.setSelectedIndex(0);
								}
								
//								private Replace[] replaces                 = null;
								// TODO possibly do something here
								
//								private Boolean   onlyRepsAfterReplace     = null;
								onlyReplacesWhenReplacedCB.setSelectedIndex(onlyRepsAfterReplace == null ? 0 : onlyRepsAfterReplace ? 2 : 1);//null,false,true
								
//								private Boolean   supressReplaces          = null;
								supressReplacesComboBox.setSelectedIndex(supressReplaces == null ? 0 : supressReplaces ? 2 : 1);//null,false,true
								
//								private Boolean   explicitLineSep          = null;
								explicitLineSepComboBox.setSelectedIndex(explicitLineSep == null ? 0 : explicitLineSep ? 2 : 1);//null,false,true
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
				loadArgsButton.setBounds(loadArgsButtonBounds);
				add(loadArgsButton);
				
				{// all options button
					allOptionsButton.addActionListener(a -> {
						if (allOptionsText.equals(allOptionsButton.getText())) {
							allOptionsButton.setText(advancedOptionsText);
							for (Component activate : allOptionsComps) {
								activate.setEnabled(true);
								activate.setVisible(true);
							}
							setBounds(boundsAll);
							setLocationRelativeTo(null);
							repaint();
						} else {
							allOptionsButton.setText(allOptionsText);
							for (Component deactivate : allOptionsComps) {
								deactivate.setEnabled(false);
								deactivate.setVisible(false);
							}
							setBounds(boundsAdvanced);
							setLocationRelativeTo(null);
							repaint();
						}
					});
				}
				for (Component deactivate : allOptionsComps) {// init: normal opts.
					deactivate.setEnabled(false);
					deactivate.setVisible(false);
				}
			}
			
			{// advanced options button
				advancedOptionsButton.addActionListener(a -> {
					if (advancedOptionsText.equals(advancedOptionsButton.getText())) {
						advancedOptionsButton.setText(normalOptionsText);
						for (Component activate : advancedOptionsComps) {
							activate.setEnabled(true);
							activate.setVisible(true);
						}
						setBounds(boundsAdvanced);
						setLocationRelativeTo(null);
						repaint();
					} else {
						advancedOptionsButton.setText(advancedOptionsText);
						for (Component deactivate : advancedOptionsComps) {
							deactivate.setEnabled(false);
							deactivate.setVisible(false);
						}
						allOptionsButton.setText(allOptionsText);
						setBounds(boundsNormal);
						setLocationRelativeTo(null);
						repaint();
					}
				});
			}
			for (Component deactivate : advancedOptionsComps) {// init: normal opts.
				deactivate.setEnabled(false);
				deactivate.setVisible(false);
			}
		}
		
		add(advancedOptionsButton);
		
		
		setVisible(true);
		toFront();
		return this;
	}
	
	final JTextField       replacesManageText     = new JTextField("manage your replaces:");
	final JButton          replacesManageButton   = new JButton("manage your replaces");
	final JFrame           replacesFrame          = new JFrame("your replaces");
	final JButton          repDeleteAllReplaces   = new JButton("reset");
	final JButton          repAddReplace          = new JButton("add a replacement (left field: regex, right field: replacement)");
	final List <Component> repRemovableComponents = new ArrayList <Component>();
	
	private void rebuildReplaces(boolean init) {
		// final JScrollBar repScrollBar = new JScrollBar();
		int yy = VOID;
		final int x1 = 100, x2 = 200, x3 = x2;
		for (Component rem : repRemovableComponents) {
			replacesFrame.remove(rem);
		}
		repRemovableComponents.clear();
		if (replaces != null) {
			for (int i = 0; i < replaces.length; i ++ ) {
				final int ai = i;
				int xx = VOID;
				final JButton b = new JButton("delete");
				b.setBounds(xx, yy, x1, Y);
				replacesFrame.add(b);
				xx += VOID + x1;
				final JTextPane tp1 = new JTextPane();
				tp1.setText(replaces[ai].regex);
				tp1.setBounds(xx, yy, x2, Y);
				replacesFrame.add(tp1);
				xx += VOID + x2;
				final JTextPane tp2 = new JTextPane();
				tp2.setText(replaces[ai].replacement);
				tp2.setBounds(xx, yy, x3, Y);
				replacesFrame.add(tp2);
				xx += VOID + x2;
				yy += LINE;
				repRemovableComponents.add(b);
				repRemovableComponents.add(tp1);
				repRemovableComponents.add(tp2);
				b.addActionListener(e -> {
					Replace[] zw = new Replace[replaces.length - 1];
					System.arraycopy(replaces, 0, zw, 0, ai);
					System.arraycopy(replaces, ai + 1, zw, ai, zw.length - ai);
					replaces = zw;
					rebuildReplaces(false);
				});
				tp1.addFocusListener(new FocusAdapter() {
					
					@Override
					public void focusLost(FocusEvent e) {
						replaces[ai] = new Replace(tp1.getText(), replaces[ai].replacement);
						rebuildReplaces(false);
					}
					
				});
				tp2.addFocusListener(new FocusAdapter() {
					
					@Override
					public void focusLost(FocusEvent e) {
						replaces[ai] = new Replace(replaces[ai].regex, tp2.getText());
						rebuildReplaces(false);
					}
					
				});
			}
		}
		// if (init) {
		// repAddReplace.addActionListener(e -> {
		// if (replaces != null && replaces.length > 0 && replaces[replaces.length - 1] == null) {
		// System.err.println("no action: " + e);
		// return;
		// }
		// System.err.println("action: " + e);
		// if (replaces != null) {
		// replaces = Arrays.copyOf(replaces, replaces.length + 1);
		// } else {
		// replaces = new Replace[1];
		// }
		// rebuildReplaces(false);
		// try {
		// Thread.sleep(10);
		// } catch (InterruptedException e1) {
		// e1.printStackTrace();
		// }
		// });
		// repAddReplace.removeActionListener(repAddReplace.getActionListeners().length > 1 ? repAddReplace.getActionListeners()[0] : null);
		// repDeleteAllReplaces.addActionListener(e -> {
		// replaces = null;
		// rebuildReplaces(false);
		// });
		// repDeleteAllReplaces.removeActionListener(repDeleteAllReplaces.getActionListeners().length > 1 ? repDeleteAllReplaces.getActionListeners()[0] : null);
		repAddReplace.setBounds(VOID + x1 + VOID, yy, x2 + x3 + VOID, Y);
		// replacesFrame.add(repAddReplace);
		repDeleteAllReplaces.setBounds(VOID, yy, x1, Y);
		// replacesFrame.add(repDeleteAllReplaces);
		replacesFrame.setVisible(false);
		if ( !init) {
			replacesFrame.repaint();
			replacesFrame.setVisible(true);
			replacesFrame.repaint();
		}
		
		// repScrollBar.setBounds(0, 0, 10, replacesFrame.getBounds().height - 30);
		// replacesFrame.add(repScrollBar);
	}
	
	private void setAllNull() {
		// to free them for the garbage collector
		template = null;
		headLines = null;
		tailLines = null;
		lineStart = null;
		lineEnd = null;
		lineEndAlsoOnTailLines = null;
		lineEndAlsoOnHeadLines = null;
		lineStartAlsoOnTailLines = null;
		lineStartAlsoOnHeadLines = null;
		src = null;
		dest = null;
		charset = null;
		asmCommentSymbol = null;
		parsedCommentSymbol = null;
		commentEndLine = null;
		supressCommentExtraction = null;
		startAfterWhite = null;
		lineSep = null;
		replaces = null;
		onlyRepsAfterReplace = null;
		supressReplaces = null;
		explicitLineSep = null;
	}
	
	private String[] generateArgs() {
		Class <? extends ParserGUI> cls = getClass();
		Field[] fields = cls.getDeclaredFields();
		List <String> args = new ArrayList <>();
		for (Field field : fields) {
			Arg argAnot = field.getAnnotation(Arg.class);
			if (argAnot == null) continue;
			Class <?> type = field.getType();
			try {
				String argSymbol = argAnot.value();
				if (type == Boolean.TYPE) {// .TYPE is for the primitive type boolean
					if (field.getBoolean(this)) {
						args.add(argSymbol);
					} // else nothing has to be added
				} else {
					Object val = field.get(this);
					if (val != null) {
						if (type == Boolean.class) {
							if ( !"".equals(argSymbol)) {
								args.add(argSymbol);
							}
							if ((Boolean) val) {
								args.add("true");
							} else {
								args.add("false");
							}
						} else if (type == String.class) {
							if ( !"".equals(argSymbol)) {
								args.add(argSymbol);
							}
							args.add((String) val);
						} else if (type == String[].class) {
							if ( !"".equals(argSymbol)) {
								args.add(argSymbol);
							}
							StringBuilder endSymbol = new StringBuilder("END");
							List <String> listVal = Arrays.asList((String[]) val);
							HashSet <String> zw = new HashSet <String>(listVal);
							String es = endSymbol.toString();
							while (zw.contains(es)) {
								endSymbol.append('#');
								es = endSymbol.toString();
							}
							args.add(es);
							args.addAll(listVal);
							args.add(es);
						} else if (type == Replace[].class) {
							Replace[] reps = (Replace[]) val;
							for (int i = 0; i < reps.length; i ++ ) {
								if ( !"".equals(argSymbol)) {
									args.add(argSymbol);
								}
								args.add(reps[i].regex);
								args.add(reps[i].replacement);
							}
						} else {
							throw new AssertionError("unknown type ('" + type + "') of field '" + field + "' with arg ('" + argSymbol + "') in my class ('" + cls + "')");
						}
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				throw new InternalError("this should be my own field msg: '" + e.getMessage() + "' lmsg: '" + e.getLocalizedMessage() + "'", e);
			}
		}
		return args.toArray(new String[args.size()]);
	}
	
}
