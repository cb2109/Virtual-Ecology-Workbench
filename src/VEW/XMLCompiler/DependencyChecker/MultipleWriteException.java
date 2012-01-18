package VEW.XMLCompiler.DependencyChecker;

import java.util.ArrayList;
import java.util.Collection;

import VEW.Planktonica2.Model.Function;
import VEW.Planktonica2.Model.VariableType;
import VEW.XMLCompiler.ASTNodes.BACONCompilerException;
import VEW.XMLCompiler.ASTNodes.RuleNode;

/**
 * More than one write has occured in an ASTree
 * 
 * @author Chris Bates
 *
 */
public class MultipleWriteException extends BACONCompilerException {

	private static final long serialVersionUID = 2492870850363362822L;
	
	private Collection<DependantMetaData<RuleNode>> rules;
	private VariableType affectedVar;
	
	public MultipleWriteException(DependantMetaData<RuleNode> firstWriteRule, DependantMetaData<RuleNode> secondWriteRule, VariableType affectedVar, String message) {
		super();
		this.rules = new ArrayList<DependantMetaData<RuleNode>> ();
		rules.add(firstWriteRule);
		rules.add(secondWriteRule);
		this.affectedVar = affectedVar;
	}
	
	public MultipleWriteException(VariableType affectedVar, Function f1, RuleNode v1, Function f2, RuleNode v2) {
		this.rules = new ArrayList<DependantMetaData<RuleNode>> ();
		rules.add(new DependantMetaData<RuleNode> (v1, f1));
		rules.add(new DependantMetaData<RuleNode> (v2, f2));
		this.affectedVar = affectedVar;
	}
	
	
	
	public VariableType getAffectedVariable () {
		return this.affectedVar;
	}
	
	public void addRuleToException (Function func, RuleNode node) {
		rules.add(new DependantMetaData<RuleNode> (node, func));
	}

	@Override
	public String getError() {
		String s = "You have written to the variable " + affectedVar.getName() + " multiple times on ";
		
		for (DependantMetaData<RuleNode> rule : this.rules) {
			s += "line: " + rule.getNode().getLine() + " in " + rule.getParent().getName() + "\n";
		}
		
		return s;
	}
	
}
