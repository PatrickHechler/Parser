package de.hechler.patrick.fileparser;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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

public class ParserGUI extends JFrame {

	/** UID */
	private static final long serialVersionUID = 4993689297474827609L;

	private static final int VOID = 10;
	private static final int FIRST_X = 200;
	private static final int SECOND_X = 400;

	private static final int Y = 20;
	private static final int LINE = Y + VOID;

	public ParserGUI() {
	}

	@Arg("--noFinish")
	private boolean noFinish = true;
	@Arg("")
	private String template = null;
	@Arg("-headLines")
	private String[] headLines = null;
	@Arg("-tailLines")
	private String[] tailLines = null;
	@Arg("-lineStart")
	private String lineStart = null;
	@Arg("-lineEnd")
	private String lineEnd = null;
	@Arg("-endTail")
	private Boolean lineEndAlsoOnTailLines = null;
	@Arg("-endHead")
	private Boolean lineEndAlsoOnHeadLines = null;
	@Arg("-startHead")
	private Boolean lineStartAlsoOnTailLines = null;
	@Arg("-startTail")
	private Boolean lineStartAlsoOnHeadLines = null;
	@Arg("--silent")
	private boolean silent = false;
	@Arg("--print")
	private boolean out = false;
	@Arg("-print")
	private boolean err = false;
	@Arg("-src")
	private String src = null;
	@Arg("-target")
	private String dest = null;
	@Arg("--forceOverride")
	private boolean forceOverrde = false;
	@Arg("--charset")
	private String charset = null;
	@Arg("-asmCommentSymbol")
	private String asmCommentSymbol = null;
	@Arg("-parsedCommentSymbol")
	private String parsedCommentSymbol = null;
	@Arg("-commentEndLine")
	private String commentEndLine = null;
	@Arg("-supressCommentExtract")
	private Boolean supressCommentExtraction = null;
	@Arg("-startAftersWhite")
	private Boolean startAfterWhite = null;
	@Arg("--lineSeparator")
	private String lineSep = null;
	@Arg("-replace")
	private Replace[] replaces = null;
	@Arg("-onlyRepWhenRep")
	private Boolean onlyRepsAfterReplace = null;
	@Arg("-supressReplaces")
	private Boolean supressReplaces = null;
	@Arg("-explicitLineSep")
	private Boolean explicitLineSep = null;

	public ParserGUI load() {
		setTitle("ENTER ARGS");
		setLayout(null);
		setResizable(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		final Rectangle boundsNormal = new Rectangle(0, 0, 640, 285);
		final Rectangle boundsAdvanced = new Rectangle(0, 0, 640, 465);
		final Rectangle boundsAll = new Rectangle(0, 0, 640, 1005);
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
					if (f.isHidden())
						return false;
					if (f.isDirectory())
						return true;
					else if (!f.getName().toLowerCase().endsWith(".args"))
						return false;
					else
						return true;
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
					if (f.isDirectory())
						return true;
					else if (!f.getName().toLowerCase().endsWith(".args"))
						return false;
					else
						return true;
				}

			};
			argsFC.addChoosableFileFilter(argsFCFilter2);
			FileFilter[] allFilters = argsFC.getChoosableFileFilters();
			argsFC.setFileFilter(argsFCFilter);
			for (FileFilter check : allFilters) {
				if (!argsFCFilter.equals(check) && !argsFCFilter2.equals(check)) {
					argsFC.removeChoosableFileFilter(check);
				}
			}
		}

		final JButton coniformButton = new JButton("finish");
		coniformButton.addActionListener(e -> {
			String[] args = generateArgs();
			Main.main(args);
			setVisible(false);
			Runtime r = Runtime.getRuntime();
			args = null;
			setAllNull();
			r.runFinalization();
			r.exit(0);
		});
		final JButton loadArgsButton = new JButton("load");
		loadArgsButton.addActionListener(e -> {
			int returnVal = argsFC.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = argsFC.getSelectedFile();
				if (!Files.exists(file.toPath())) {
					JOptionPane.showMessageDialog(this, "the file '" + file.getPath() + "' does not exsit!",
							"NO SUCH FILE!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (file.isDirectory()) {
					JOptionPane.showMessageDialog(this, "i can not load from a folder! ('" + file.getPath() + "')",
							"NO FOLDERS!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					Serializer.load(this, file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		final JButton saveArgsButton = new JButton("save");
		saveArgsButton.addActionListener(e -> {
			int returnVal = argsFC.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = argsFC.getSelectedFile();
				if (file.isDirectory()) {
					JOptionPane.showMessageDialog(this, "i can not save to a folder! ('" + file.getPath() + "')",
							"NO FOLDERS!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (Files.exists(file.toPath())) {
					int chose = JOptionPane.showConfirmDialog(this,
							"this file exists already ('" + file.getPath() + "'), should I overwrite the file?");
					if (chose != JOptionPane.OK_OPTION) {
						return;
					}
				}
				try {
					Serializer.save(this, file, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		int x = VOID, y = VOID;
		coniformButton.setBounds(x, y, FIRST_X, Y);
		add(coniformButton);
		x += VOID + FIRST_X;
		{
			final int xWert = (SECOND_X - VOID) / 2;
			loadArgsButton.setBounds(x, y, xWert, Y);
			add(loadArgsButton);
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
		final JComboBox<String> forceOverComboBox = new JComboBox<String>();
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
				throw new AssertionError("illegal selectet item-index: " + i + " legal:[0,1] item-value: '"
						+ forceOverComboBox.getItemAt(i) + "'");
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
		final JComboBox<String> templateOverComboBox = new JComboBox<String>();
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
				throw new AssertionError("illegal selectet item-index: " + i + " legal:[0,1] item-value: '"
						+ templateOverComboBox.getItemAt(i) + "'");
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
		final JComboBox<String> lineSepComboBox = new JComboBox<String>();
		lineSepTField.setEditable(false);
		String sls = System.lineSeparator();
		lineSepComboBox.addItem("system line separator: ("
				+ ("\r\n".equals(sls) ? "CR LF" : "\n".equals(sls) ? "LF" : "\r".equals(sls) ? "CR" : "unknown") + ")");
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
				throw new AssertionError("illegal selectet item-index: " + i + " legal:[0,1] item-value: '"
						+ lineSepComboBox.getItemAt(i) + "'");
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
			final List<Component> advancedOptionsComps = new ArrayList<>();
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
					if (t == null || t.isEmpty())
						headLines = null;
					else
						headLines = t.split("(\r\n)|\r|\n");
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
			final JComboBox<String> headLinesComboBox = new JComboBox<String>();
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
					if (t == null || t.isEmpty())
						headLines = null;
					else
						headLines = t.split("(\r\n)|\r|\n");
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
			final JComboBox<String> tailLinesComboBox = new JComboBox<String>();
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
			lineStartText.setText("yout chosen line start");
			final JButton lineStartButton = new JButton("reset line start");
			lineStartButton.addActionListener((a) -> {
				lineStartText.setText("yout chosen line start");
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
			final JComboBox<String> startAfterWhiteComboBox = new JComboBox<String>(new String[] { "nothing choosen",
					"line start is after the whitespace", "line start is set an the real line start" });
			startAfterWhiteComboBox.addActionListener(e -> {
				int i = startAfterWhiteComboBox.getSelectedIndex();
				switch (i) {
				case 0:
					startAfterWhite = true;
					break;
				case 1:
					startAfterWhite = false;
					break;
				default:
					throw new AssertionError("illegal selectet item-index: " + i + " legal:[0,1] item-value: '"
							+ forceOverComboBox.getItemAt(i) + "'");
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
				List<Component> allOptionsComps = new ArrayList<Component>();

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

//		  		String commentEndLine = null; 
//		  		("<-commentEndLine> or <-cel> [COMMENT_END_LINE]");
//		  		("          to print at the very end of each line this (after the endLine (and of course also after the extracted comment if the comments are not disabled))");
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

//		  		Boolean supressCommentExtraction = null;
//		  		("<-scp> or <-supressCommentParsing> <true>/<1> or <false>/<0>");
//		  		("          to tell the parser that Assembler comments (by default ';') should be placed after the endLine (but not after the lineSeparator or endCommentLine)");
				final JTextField supressCommentExtractionTF = new JTextField("select extraction of cmments");
				supressCommentExtractionTF.setEditable(false);
				final JComboBox<String> supressCommentExtractionCB = new JComboBox<String>();
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

//				  List <Replace> replaces = new ArrayList <>();
//				  ("<-rep> or <-replace> [REGEX] [REPLACEMENT]");
//				  ("          to try replacing in every line");
//				  ("          this option can be used multiple times");
//				  ("               when used multiple times the first replac-args will replace earlier and the ones after them");
//				  ("               the later replace-args will try to replace the replaced");
//				  ("          the replacement will be treated as a single line, even if there are line seperators inside");
//				  ("               these potential line separators will be overwritten with the overgiven line separator (<--lineSeparator>)");
//				  ("               for the line start and line end it will be only one line");
//				  ("          the replacement will be called befor lineStart or lineEnd had been set");
				final JTextField replacesTextField = new JTextField("manage your replaces");
				replacesTextField.setEditable(false);
				final JButton replacesButton = new JButton();
				final JFrame replacesFrame = new JFrame("your replaces");
				{
					final JTextField repShowField1 = new JTextField("show replaces from: ");
					final JTextField repShowField2 = new JTextField("show replaces number: ");
					final JTextPane repShowPane1 = new JTextPane();
					final JTextPane repShowPane2 = new JTextPane();
					final List<JButton> repButtons = new ArrayList<>();
					final List<JTextPane> repTextPanes = new ArrayList<>();
					final List<JButton> shownRepButtons = repButtons.subList(0, repButtons.size());
					final List<JTextPane> shownrepTextPanes = new ArrayList<>();
					replacesFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
					replacesFrame.setVisible(false);
					
					
					// TODO continue here
				}
				replacesButton.addActionListener(e -> {
					replacesFrame.setVisible(true);
				});
				x = VOID;
				y += LINE;
				replacesTextField.setBounds(x, y, FIRST_X, Y);
				x += FIRST_X + VOID;
				replacesButton.setBounds(x, y, SECOND_X, Y);
				advancedOptionsComps.add(replacesTextField);
				advancedOptionsComps.add(replacesButton);
				add(replacesTextField);
				add(replacesButton);

				// TODO continue here

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

//		  @formatter:off

//		  boolean noFinish = true; ("<--noFinish> or <--nf>");
//		  ("          to print no finish message after finishing with parsing");

//		  Boolean lineStartAlsoOnHeadLines = null;
//		  ("<-soh> or <-startHead> <true>/<1> or <false>/<0>");
//		  ("          to enable <true>/<1> or disable <false>/<0> the startLine for the head");

//		  Boolean lineStartAlsoOnTailLines = null;
//		  ("<-sot> or <-startTail> <true>/<1> or <false>/<0>");
//		  ("          to enable <true>/<1> or disable <false>/<0> the startLine for the tail");

//		  Boolean lineEndAlsoOnHeadLines = null;
//		  ("<-eoh> or <-endHead> <true>/<1> or <false>/<0>");
//		  ("          to enable <true>/<1> or disable <false>/<0> the endLine for the head");

//		  Boolean lineEndAlsoOnTailLines = null;
//		  ("<-eot> or <-endTail> <true>/<1> or <false>/<0>");
//		  ("          to enable <true>/<1> or disable <false>/<0> the endLine for the tail");

//		  boolean silent = false; 
//		  ("<--silent> or <--s>");
//		  ("          to print the parsed not to the generated target file (if no target is set it will be generated from the name of the source file, but only if silent is not set)");

//		  boolean out = false; 
//		  ("<--print>");
//		  ("          to print the parsed also to the default out");

//		  boolean err = false; 
//		  ("<-print>");
//		  ("          to print the parsed also to the default err");

//		  Boolean continueAfterReplaces = null;
//		  ("<-orwr> or <-onlyRepWhenRep> <true>/<1> or <false>/<0>");
//		  ("          to enble/disable if the parse should stop modifiing the line after all replacements are done and they changed something");

//		  Boolean supressReplaces = null;
//		  ("<-sr> or <-supressReplaces> <true>/<1> or <false>/<0>");
//		  ("          to supress(true/1) or allow(false/0) replaces"); Boolean

//		  explicitLineSep = null;
//		  ("<-els> or <-explicitLineSep> <true>/<1> or <false>/<0>");
//		  ("          to set if the lines need a explicit line separator (<true>/<1>) or if it should be appanded after each line");
//		  ("          if it is set <true>/<1> the lines will be read with a line separator");

//		  @formatter:on

		setVisible(true);
		toFront();
		return this;
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
		Class<? extends ParserGUI> cls = getClass();
		Field[] fields = cls.getDeclaredFields();
		List<String> args = new ArrayList<>();
		for (Field field : fields) {
			Arg argAnot = field.getAnnotation(Arg.class);
			if (argAnot == null)
				continue;
			Class<?> type = field.getType();
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
							if (!"".equals(argSymbol)) {
								args.add(argSymbol);
							}

						} else if (type == String.class) {
							if (!"".equals(argSymbol)) {
								args.add(argSymbol);
							}
							args.add((String) val);
						} else if (type == String[].class) {
							if (!"".equals(argSymbol)) {
								args.add(argSymbol);
							}
							StringBuilder endSymbol = new StringBuilder("END");
							List<String> listVal = Arrays.asList((String[]) val);
							HashSet<String> zw = new HashSet<String>(listVal);
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
							for (int i = 0; i < reps.length; i++) {
								if (!"".equals(argSymbol)) {
									args.add(argSymbol);
								}
								args.add(reps[i].regex);
								args.add(reps[i].replacement);
							}
						} else {
							throw new AssertionError("unknown type ('" + type + "') of field '" + field
									+ "' with arg ('" + argSymbol + "') in my class ('" + cls + "')");
						}
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				throw new InternalError("this should be my own field msg: '" + e.getMessage() + "' lmsg: '"
						+ e.getLocalizedMessage() + "'", e);
			}
		}
		return args.toArray(new String[args.size()]);
	}

}
