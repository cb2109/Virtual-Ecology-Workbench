package VEW.Planktonica2.UIComponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import VEW.Planktonica2.ControllerStructure.SelectableItem;
import VEW.Planktonica2.ControllerStructure.VEWController;
import VEW.Planktonica2.Model.Catagory;
import VEW.Planktonica2.Model.GlobalVariable;
import VEW.Planktonica2.Model.Unit;
import VEW.Planktonica2.Model.VariableType;
import VEW.XMLCompiler.ASTNodes.AmbientVariableTables;


public class AutocompleteBox {

	private final JList list = new JList();
	private final JEditorPane description = new JEditorPane();
	private final JPopupMenu acbox = new JPopupMenu();
	JTextPane target;
	private String current_word;
	private int caret_position;
	private Catagory current_catagory;
	private boolean visible = false;

	private HashMap<String,String> rule_functions 
		= new HashMap<String,String>();
	
	private HashMap<String,String> expr_functions 
		= new HashMap<String,String>();
	
	private VEWController controller;
	
	public HashMap<String, String> getRule_functions() {
		return rule_functions;
	}

	public HashMap<String, String> getExpr_functions() {
		return expr_functions;
	}

	public String getCurrent_word() {
		return current_word;
	}

	public void setCurrent_word(String currentWord) {
		current_word = currentWord;
	}
	
	public JTextPane getTarget() {
		return target;
	}

	public JEditorPane getDescription() {
		return description;
	}
	
	public void setCurrent_catagory(Catagory current_catagory) {
		this.current_catagory = current_catagory;
	}

	public Catagory getCurrent_catagory() {
		return current_catagory;
	}

	public AutocompleteBox(JTextPane text, VEWController controller) {
		this.controller = controller;
		// Set up the list view
		JScrollPane scroll_list = new JScrollPane(list); 
		scroll_list.setPreferredSize(new Dimension(150,100));
        scroll_list.getVerticalScrollBar().setFocusable( false ); 
        scroll_list.getHorizontalScrollBar().setFocusable( false ); 
        scroll_list.setBorder(null);
        // Set up the description view
        JScrollPane scroll_desc = new JScrollPane(description); 
        scroll_desc.setPreferredSize(new Dimension(200,100));
        scroll_desc.getVerticalScrollBar().setFocusable( false ); 
        scroll_desc.getHorizontalScrollBar().setFocusable( false );
        // Put them on a panel
        JPanel container = new JPanel(new BorderLayout());
        container.add(scroll_list,BorderLayout.WEST);
        container.add(scroll_desc,BorderLayout.EAST);
        container.setPreferredSize(new Dimension(350,100));
        // Set up the description pane
        description.setEditable(false);
        description.setContentType("text/html");
        description.setBackground(Color.decode("#FFF9BD"));
        // Set up the box itself
        acbox.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        acbox.add(container);
        target = text;
        list.addMouseListener(new AutocompleteClickListener(this));
        list.addListSelectionListener(new SelectionListener(this));
        list.addKeyListener(new AutocompleteKeyListener(this));
        current_word = "";
        // Add all functions to their hash maps
        fill_function_maps();
        
	}

	private void fill_function_maps() {
		rule_functions.put("uptake([chemical],[amount])",
			"Issue a request to consume [amount] of a given [chemical].");
        rule_functions.put("release([chemical],[amount])",
			"Release [amount] of a given [chemical] from the internal chemical pool.");
        rule_functions.put("ingest([foodset],[threshold],[rate])",
		   "Consume from [foodset] at a rate of [rate], if at least [threshold] entities of the current food type exist.");
        rule_functions.put("change([stage])",
			"Change from the current stage into [stage].");
        rule_functions.put("pchange([stage],[p])",
			"Change from the current stage into [stage] with probability [p].");
        rule_functions.put("divide([number])",
			"Divide the current agent into [number] separate agents.");
        rule_functions.put("create([stage],[number])",
			"Create [number] new agents of type [stage].");
        
        expr_functions.put("integrate([expr])",
        	"Integrate a property that varies over depth based on movement in the previous timestep.");
        expr_functions.put("varhist([variable],[timesteps])",
        	"Return the value of [variable] from [timesteps] steps in the past.");
        expr_functions.put("abs([expr])",
        	"Return the absolute value of [expr].");
        expr_functions.put("acos([expr])",
    		"Return the <i>arccosine</i> of [expr]");
        expr_functions.put("asin([expr])",
			"Return the <i>arcsine</i> of [expr]");
        expr_functions.put("atan([expr])",
			"Return the <i>arctangent</i> of [expr]");
        expr_functions.put("cos([expr])",
			"Return the <i>cosine</i> of [expr]");
        expr_functions.put("exp([expr])",
			"Return <i>e</i> ^ [expr]");
        expr_functions.put("ln([expr])",
			"Return the <i>natural logarithm</i> of [expr]");
        expr_functions.put("log10([expr])",
			"Return logarithm to the base 10 of [expr]");
        expr_functions.put("rnd([expr])",
			"Return a random number between 1 and [expr] inclusive.");
        expr_functions.put("sin([expr])",
        	"Return the <i>sine</i> of [expr]");
        expr_functions.put("sqrt([expr])",
    		"Return the sqare root of [expr]");
        expr_functions.put("tan([expr])",
    		"Return the <i>tangent</i> of [expr]");
        expr_functions.put("densityAt([expr])",
			"Return the density of water at depth [expr]");
        expr_functions.put("depthForFI([expr])",
			"Return the closest depth at which the full irradiance <i>(Wm-2)</i> is [expr].");
        expr_functions.put("depthForVI([expr])",
			"Return the closest depth at which the visible irradiance <i>(Wm-2)</i> is [expr].");
        expr_functions.put("fullIrradAt([expr])",
			"Return the full irradiance of the water at depth [expr].");
        expr_functions.put("salinityAt([expr])",
			"Return the salinity of the water at depth [expr].");
        expr_functions.put("temperatureAt([expr])",
			"Return the temperature of the water at depth [expr].");
        expr_functions.put("UVIrradAt([expr])",
			"Return the Ultra Violet irradiance <i>(Wm-2)</i> at depth [expr].");
        expr_functions.put("average([vexpr])",
			"Return the average value of an expression that evaluates to a vector.");
        expr_functions.put("product([vexpr])",
			"Return the vector product of an expression that evaluates to a vector.");
        expr_functions.put("sum([vexpr])",
			"Return the vector sum of an expression that evaluates to a vector.");
        expr_functions.put("max([expr],[expr])",
			"Return the larger of two expressions.");
        expr_functions.put("min([expr],[expr])",
		"Return the smaller of two expressions.");
        expr_functions.put("all([boolean])",
        	"Returns true if the boolean condition is true for all values of an array");
        expr_functions.put("some([boolean])",
    		"Returns true if the boolean condition is true for at least one value of an array");
        expr_functions.put("none([boolean])",
    		"Returns true if the boolean condition is false for all values of an array");
	}
	
	public JList getList() {
		return list;
	}

	public JPopupMenu getAcbox() {
		return acbox;
	}

	public void new_word() {
		current_word = "";
	}
	
	public void show_suggestions(KeyEvent e) {
		this.visible = true;
		String key_val = KeyEvent.getKeyText(e.getKeyCode());
		if (key_val.equals("Minus") && e.isShiftDown())
			key_val = "_";
		if (is_number(key_val) && e.isShiftDown()) {
			if (key_val.equals("2")) {
				// It must be a double quote
				int pos = target.getCaretPosition();
				try {
					target.getDocument().insertString(pos, "\"", null);
					target.setCaretPosition(pos);
				} catch (BadLocationException e1) {
				}
			} else {
				// It's some other form of punctuation, so reset the autocomplete
				new_word();
				hide_suggestions();
			}
			return;
		}
		if (is_letter_or_number(key_val) || key_val.equals("_") || 
				(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && current_word.length() > 1)) {
			ArrayList<String> suggestions = find_suggestions();
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				current_word = current_word.substring(0, current_word.length() - 1);
			else {
				current_word += key_val.toLowerCase();
			}
			ArrayList<String> possible = new ArrayList<String>();
			for (String s : suggestions) {
				String suggest = s.toLowerCase();
				if (suggest.startsWith(current_word)) {
					possible.add(s);
				}
				//possible.add("djjazzyjeff");
			}
			if (possible.size() == 0) {
				hide_suggestions();
				return;
			}
			// TODO - sort list alphabetically
			list.setListData(possible.toArray());
			// Set a minimum size for the list
			list.setVisibleRowCount(list.getModel().getSize() < 6 ? list.getModel().getSize() : 6);
			Point p = target.getCaret().getMagicCaretPosition();
			if (acbox.isVisible()) {
				SwingUtilities.convertPointToScreen(p, target);
				acbox.setLocation(p.x, p.y + 20);
			} else {
				acbox.show(target, p.x, p.y + 20);
			}
			target.requestFocus();
		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN){
			// Give focus to the list box if it is visible
			if (visible && acbox.isVisible() && !current_word.equals("")) {
				list.requestFocus();
				list.setSelectedIndex(0);
				target.setCaretPosition(caret_position);
			}
		} else {
			new_word();
			hide_suggestions();
		}
		caret_position = target.getCaretPosition();
	}

	private ArrayList<String> find_suggestions() {
		ArrayList<String> suggestions = new ArrayList<String>();
		// Get all global variables
		add_to_suggestions(suggestions, AmbientVariableTables.getTables().getAllVariableNames());
		// Get all possible rules
		add_to_suggestions(suggestions, rule_functions.keySet().toArray());
		// Get all functions usable in [expr]s
		add_to_suggestions(suggestions, expr_functions.keySet().toArray());
		// Get the currently selected functional group/chemical and extract it's variables
		SelectableItem si = this.controller.getSelectedCatagory();
		if (si instanceof Catagory) {
			Catagory c = (Catagory) si;
			this.current_catagory = c;
			add_to_suggestions(suggestions, c.get_state_vars());
			add_to_suggestions(suggestions, c.get_params());
			add_to_suggestions(suggestions, c.get_local_vars());
			add_to_suggestions(suggestions, c.get_variety_concs());
			add_to_suggestions(suggestions, c.get_variety_states());
			add_to_suggestions(suggestions, c.get_variety_params());
			add_to_suggestions(suggestions, c.get_variety_locals());
		}
		return suggestions;
	}

	private void add_to_suggestions(ArrayList<String> suggestions, Object[] strings) {
		for (int i = 0; i < strings.length; i++)
			suggestions.add(strings[i].toString());
	}
	
	
	private boolean is_letter_or_number(String keyVal) {
		return (keyVal.length() == 1 && Character.isLetterOrDigit(keyVal.charAt(0)));
	}

	private boolean is_number(String keyVal) {
		return (keyVal.length() == 1 && Character.isDigit(keyVal.charAt(0)));
	}
	
	public void hide_suggestions() {
		this.visible = false;
		description.setText("");
		acbox.setVisible(false);
	}
	
	public void insert_selection() {
		// Get the selected item
		String select = list.getSelectedValue().toString();
		try {
			// Try to place it in the text box
			int pos = target.getCaretPosition();
			int length = current_word.length();
			target.getDocument().remove(pos - length, length);
			pos = target.getCaretPosition();
			target.getDocument().insertString(pos, select, null);
		} catch (BadLocationException e) {
			// Should never happen
		}
		current_word = "";
		hide_suggestions();
	}
	
	static class SelectionListener implements ListSelectionListener {

		private AutocompleteBox parent;
		
		public SelectionListener(AutocompleteBox autocompleteBox) {
			this.parent = autocompleteBox;
		}
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (parent.getList().getSelectedValue() != null) {
				String name = parent.getList().getSelectedValue().toString();
				String text = "<html><PRE><b>" + name + "</b>\n\n";
				Object var = AmbientVariableTables.getTables().checkAllTables(name);
				if (var instanceof GlobalVariable) {
					GlobalVariable gvar = (GlobalVariable) var;
					text += gvar.getDesc() + "\n";
					text += "<b>Units:</b>";
					for (Unit u : gvar.getUnits()) {
						text += u.format();
					}
				} else if (parent.getRule_functions().get(name) != null) {
					text += parent.getRule_functions().get(name);
				} else if (parent.getExpr_functions().get(name) != null) {
					text += parent.getExpr_functions().get(name);
				} else if (this.parent.getCurrent_catagory() != null) {
					VariableType v = this.parent.getCurrent_catagory().checkAllVariableTables(name);
					text += v.getDesc() + "\n";
					text += "<b>Units:</b>";
					for (Unit u : v.getUnits()) {
						text += u.format();
					}
				}
				text += "</PRE></html>";
				parent.getDescription().setText(text);
				parent.getDescription().setCaretPosition(0);
			}
		}
		
	}
	
	static class AutocompleteClickListener implements MouseListener {
		
		private AutocompleteBox parent;
		private boolean clicked;
		private int selected = -1;
		
		public AutocompleteClickListener(AutocompleteBox autocompleteBox) {
			this.parent = autocompleteBox;
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (clicked && parent.getList().getSelectedIndex() == selected) {
				parent.insert_selection();
				clicked = false;
				selected = -1;
			} else {
				clicked = true;
				selected = parent.getList().getSelectedIndex();
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}
	
	static class AutocompleteKeyListener implements KeyListener {

		private AutocompleteBox parent;
		
		public AutocompleteKeyListener(AutocompleteBox autocompleteBox) {
			this.parent = autocompleteBox;
		}
		
		@Override
		public void keyPressed(KeyEvent arg0) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER && parent.getList().getSelectedIndex() != -1) {
				parent.insert_selection();
			}
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}
	
	}
	
}
