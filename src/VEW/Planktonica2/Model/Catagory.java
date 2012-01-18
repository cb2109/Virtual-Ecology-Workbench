package VEW.Planktonica2.Model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.RecognitionException;

import VEW.Common.XML.XMLTag;
import VEW.Planktonica2.ControllerStructure.SelectableItem;
import VEW.XMLCompiler.ANTLR.ANTLRParser;
import VEW.XMLCompiler.ANTLR.CompilerException;
import VEW.XMLCompiler.ASTNodes.AmbientVariableTables;
import VEW.XMLCompiler.ASTNodes.ConstructedASTree;
import VEW.XMLCompiler.ASTNodes.SymbolTable;
import VEW.XMLCompiler.DependencyChecker.MultipleWriteAndChangeVisitor;
import VEW.XMLCompiler.DependencyChecker.OrderingAgent;

public abstract class Catagory implements SelectableItem, BuildFromXML, BuildToXML {
	
	protected String name;
	protected String file_path;
	
	protected ArrayList <Function> functions;
	
	protected SymbolTable<StateVariable> stateVarTable;
	protected SymbolTable<Parameter> paramTable;
	protected SymbolTable<Local> localVarTable;
	protected SymbolTable<VarietyVariable> varietyStateTable;
	protected SymbolTable<VarietyParameter> varietyParamTable;
	protected SymbolTable<VarietyConcentration> varietyConcTable;
	protected SymbolTable<VarietyLocal> varietyLocalTable;
	
	protected ArrayList<String> warnings;
	
	protected XMLTag baseTag;
	
	public Catagory() {
		
		functions = new ArrayList<Function>();
		warnings = new ArrayList<String>();
		initialiseTables();
	}

	private void initialiseTables() {
		stateVarTable = new SymbolTable<StateVariable>();
		paramTable = new SymbolTable<Parameter>();
		localVarTable = new SymbolTable<Local>();
		varietyStateTable = new SymbolTable<VarietyVariable>();
		varietyParamTable = new SymbolTable<VarietyParameter>();
		varietyConcTable = new SymbolTable<VarietyConcentration>();
		varietyLocalTable = new SymbolTable<VarietyLocal>();
		
	}
	
	public VariableType checkAssignableVariableTables(String variableName) {
		StateVariable sVar = checkStateVariableTable(variableName);
		if (sVar != null) return sVar;
		Local localVar = checkLocalVarTable(variableName);
		if (localVar != null) return localVar;
		VarietyLocal varLocal = checkVarietyLocalTable(variableName);
		if (varLocal != null) return varLocal;
		VarietyVariable varState = checkVarietyStateTable(variableName);
		return varState;
	}
	
	public VariableType checkAccessableVariableTable(String variableName) {
		VariableType var = checkAssignableVariableTables(variableName);
		if (var != null) return var;
		VarietyConcentration conc = checkVarietyConcTable(variableName);
		if (conc != null) return conc;
		AmbientVariableTables tables = AmbientVariableTables.getTables();
		var = tables.checkGlobalVariableTables(variableName);
		if (var != null) return var;
		var = checkParameterTable(variableName);
		if (var != null) return var;
		var = checkVarietyParamTable(variableName);
		return var;
	}
	
	public VariableType checkAllVariableTables(String variableName) {
		VariableType var = checkAccessableVariableTable(variableName);	
		return var;
	}
	
	public VariableType removeFromTables(String variableName) {
		StateVariable sVar = checkStateVariableTable(variableName);
		if (sVar != null) return stateVarTable.remove(variableName);
		Local localVar = checkLocalVarTable(variableName);
		if (localVar != null) return localVarTable.remove(variableName);
		VarietyLocal varLocal = checkVarietyLocalTable(variableName);
		if (varLocal != null) return varietyLocalTable.remove(variableName);
		VarietyVariable varState = checkVarietyStateTable(variableName);
		if (varState != null) return varietyStateTable.remove(variableName);
		VarietyConcentration conc = checkVarietyConcTable(variableName);
		if (conc != null) return varietyConcTable.remove(variableName);
		VariableType var = checkParameterTable(variableName);
		if (var != null) return paramTable.remove(variableName);
		var = checkVarietyParamTable(variableName);
		return varietyParamTable.remove(variableName);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addToVarietyConcTable(VarietyConcentration conc) {
		varietyConcTable.put(conc.getName(), conc);
	}
	
	public VarietyConcentration checkVarietyConcTable(String conc) {
		return varietyConcTable.get(conc);
	}
	

	/**
	 * Moves a given function by the offset in the functions list
	 * @param func the func to move
	 * @param offset (+ = down/ - = up (the list))
	 */
	public void moveFunctionIndex(Function func, int offset) {

		if (func != null) {
			int oldIndex = functions.indexOf(func) + offset;
			if (oldIndex >= 0 && oldIndex < functions.size()) {
				functions.remove(func);
				functions.add(oldIndex, func);
			}
		} else {
			System.err.println("Could not move func");
		}


	}
	
	
	public void addToStateVarTable(StateVariable var) {
		stateVarTable.put(var.getName(), var);
	}
	
	public void addToStateVarTable(String name, StateVariable var) {
		stateVarTable.put(name, var);
	}
	
	public StateVariable checkStateVariableTable(String varName) {
		return stateVarTable.get(varName);
	}
	
	
	
	public void addToParamTable(Parameter param) {
		paramTable.put(param.getName(), param);
	}
	
	public Parameter checkParameterTable(String paramName) {
		return paramTable.get(paramName);
	}
	
	
	
	public void addToLocalTable(Local local) {
		localVarTable.put(local.getName(), local);
	}
	
	public Local checkLocalVarTable(String localName) {
		return localVarTable.get(localName);
	}
	
	
	
	public void addToVarietyStateTable(VarietyVariable var) {
		varietyStateTable.put(var.getName(), var);
	}
	
	public VarietyVariable checkVarietyStateTable(String varietyName) {
		return varietyStateTable.get(varietyName);
	}
	
	
	
	public void addToVarietyParamTable(VarietyParameter varParam) {
		varietyParamTable.put(varParam.getName(), varParam);
	}
	
	public VarietyParameter checkVarietyParamTable(String paramName) {
		return varietyParamTable.get(paramName);
	}
	
	
	
	public void addToVarietyLocalTable(VarietyLocal varLocal) {
		varietyLocalTable.put(varLocal.getName(), varLocal);
	}
	
	public VarietyLocal checkVarietyLocalTable(String localName) {
		return varietyLocalTable.get(localName);
	}

	public void setFunctions(ArrayList<Function> functions) {
		this.functions = functions;
	}
	public ArrayList<Function> getFunctions() {
		return functions;
	}
	@Override
	public int getNoFunctions() {
		return functions.size();
	}
	@Override
	public Function getFunctionAtIndex(int functionNo) {
		return this.functions.get(functionNo);
	}
	
	public Function addFunction(String filepath, String name) {
		Function f = new Function(filepath,name,this);
		this.functions.add(f);
		return f;
	}
	
	public void removeFunction(Function f) {
		this.functions.remove(f);
	}
	
	public String[] get_state_vars() {
		Object[] vars = stateVarTable.keySet().toArray();
		String [] all_vars = new String[vars.length];
		for (int i = 0; i < all_vars.length; i++) {
			all_vars[i] = (String) vars[i];
		}
		return all_vars;
	}
		
	
 	public String[] get_params() {
		Object[] vars = paramTable.keySet().toArray();
		String [] all_vars = new String[vars.length];
		for (int i = 0; i < all_vars.length; i++) {
			all_vars[i] = (String) vars[i];
		}
		return all_vars;
	}
 	
 	
	public String[] get_local_vars() {
		Object[] vars = localVarTable.keySet().toArray();
		String [] all_vars = new String[vars.length];
		for (int i = 0; i < all_vars.length; i++) {
			all_vars[i] = (String) vars[i];
		}
		return all_vars;
	}

	public String[] get_variety_states() {
		Object[] vars = varietyStateTable.keySet().toArray();
		String [] all_vars = new String[vars.length];
		for (int i = 0; i < all_vars.length; i++) {
			all_vars[i] = (String) vars[i];
		}
		return all_vars;
	}
	
	
	public String[] get_variety_params() {
		Object[] vars = varietyParamTable.keySet().toArray();
		String [] all_vars = new String[vars.length];
		for (int i = 0; i < all_vars.length; i++) {
			all_vars[i] = (String) vars[i];
		}
		return all_vars;
	}

	public String[] get_variety_concs() {
		Object[] vars = varietyConcTable.keySet().toArray();
		String [] all_vars = new String[vars.length];
		for (int i = 0; i < all_vars.length; i++) {
			all_vars[i] = (String) vars[i];
		}
		return all_vars;
	}

	public String[] get_variety_locals() {
		Object[] vars = varietyLocalTable.keySet().toArray();
		String [] all_vars = new String[vars.length];
		for (int i = 0; i < all_vars.length; i++) {
			all_vars[i] = (String) vars[i];
		}
		return all_vars;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	public XMLTag buildToXML() throws XMLWriteBackException{
		
		XMLWriteBackException collectedExceptions = new XMLWriteBackException();
		
		OrderingAgent o = new OrderingAgent();
		if (orderFunctions(o, collectedExceptions)) {
			if (o.getOrdering().containsAll(functions)) {
				functions = o.getOrdering();
			}
		} else if (Function.COMPILEFULLY) {
			collectedExceptions.addCompilerException(new CompilerException(o.extractErrors()));
		}
		
		
		XMLTag newTag = new XMLTag("placeholder");
		if (baseTag != null) {
			newTag.addTags(baseTag.getTags());
		}
		newTag.addTag(new XMLTag("name", name));
		
		Collection<Collection<Function>> groups = groupTreesByStage(functions);
		for(Collection<Function> group: groups) {
			MultipleWriteAndChangeVisitor vis = new MultipleWriteAndChangeVisitor();
			for (Function f : group) {
				vis.setCurrentFunction(f);
				f.setVisitor(vis);
				try {
					newTag.addTag(f.buildToXML());
				}
				catch (XMLWriteBackException ex) {
					collectedExceptions.addCompilerException(ex.getCompilerExceptions(),this.name);
				}

				f.setVisitor(null);
			}
			if (vis.hasExceptions()) {
				collectedExceptions.addCompilerException(new CompilerException(vis.getExceptions()));
			}
		}
		
		
		if (collectedExceptions.hasExceptions()) {
			throw collectedExceptions;
		}
		
		buildVariableTableToXML(newTag, stateVarTable);
		buildVariableTableToXML(newTag, paramTable);
		buildVariableTableToXML(newTag, localVarTable);
		buildVariableTableToXML(newTag, varietyStateTable);
		buildVariableTableToXML(newTag, varietyParamTable);
		buildVariableTableToXML(newTag, varietyConcTable);
		buildVariableTableToXML(newTag, varietyLocalTable);
		return newTag;
	}
	
	private Collection<Collection<Function>> groupTreesByStage(Collection<Function> functions) {
		
		HashMap<Stage, Collection<Function>> stageGroups =
				new HashMap<Stage, Collection<Function>> ();
		
		for (Function data : functions) {
			
			Collection<Stage> stagesCalledIn = data.getCalledIn();
			for (Stage s : stagesCalledIn) {
				Collection<Function> group = stageGroups.get(s);
				if (group == null) {
					group = new ArrayList<Function> ();
				}
				
				group.add(data);
				stageGroups.put(s, group);
				
			}
			
		}
		
		return stageGroups.values();
	}
	
	private boolean orderFunctions(OrderingAgent o, XMLWriteBackException collectedExceptions) {
		
		Map<Function, ConstructedASTree> trees = new HashMap<Function, ConstructedASTree> ();
		for (Function f : functions) {
			ANTLRParser comp = null;
			try {
				comp = new ANTLRParser(new File(f.getSource_code() + getName() + "\\" + f.getName() + ".bacon"), f);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			ConstructedASTree t = null;
			try {
				t = comp.getAST();
			} catch (RecognitionException e) {
				
			}
			if (t != null) {
				if (t.hasExceptions()) {
					return false;
				} else {
					trees.put(f, t);
				}
			}
			
		}
		
		o.setTrees(trees);
		
		if (trees.isEmpty()) {
			return false;
		}
		
		return o.reorderFunctions();
		
	}

	private <V extends VariableType> void buildVariableTableToXML(XMLTag tag, SymbolTable<V> table) throws XMLWriteBackException {
		Collection<V> vals = table.values();
		Iterator<V> iter = vals.iterator();
		while(iter.hasNext()) {
			V var = iter.next();
			if (var.includedInXML())
				tag.addTag(var.buildToXML());
		}
		
	}
	
	public String getFilePath() {
		return this.file_path;
	}
	
	public void addWarnings(List<String> warnings) {
		this.warnings.addAll(warnings);
	}

	public List<String> getWarnings() {
		return warnings;
	}
	
	public void clearWarnings() {
		warnings = new ArrayList<String>();
	}
	
}
