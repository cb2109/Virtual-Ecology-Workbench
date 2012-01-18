package VEW.XMLCompiler.DependencyChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import VEW.Common.Pair;
import VEW.Planktonica2.Model.Function;
import VEW.Planktonica2.Model.VariableType;
import VEW.XMLCompiler.ASTNodes.ASTreeVisitor;
import VEW.XMLCompiler.ASTNodes.AssignListNode;
import VEW.XMLCompiler.ASTNodes.AssignNode;
import VEW.XMLCompiler.ASTNodes.BACONCompilerException;
import VEW.XMLCompiler.ASTNodes.BinOpNode;
import VEW.XMLCompiler.ASTNodes.BinaryFunctionNode;
import VEW.XMLCompiler.ASTNodes.BinaryPrimitiveNode;
import VEW.XMLCompiler.ASTNodes.BooleanBinOpNode;
import VEW.XMLCompiler.ASTNodes.BooleanComparitorNode;
import VEW.XMLCompiler.ASTNodes.BooleanNotOpNode;
import VEW.XMLCompiler.ASTNodes.ChangeNode;
import VEW.XMLCompiler.ASTNodes.CreateNode;
import VEW.XMLCompiler.ASTNodes.IdNode;
import VEW.XMLCompiler.ASTNodes.IfExprNode;
import VEW.XMLCompiler.ASTNodes.IfRuleNode;
import VEW.XMLCompiler.ASTNodes.IngestNode;
import VEW.XMLCompiler.ASTNodes.NegNode;
import VEW.XMLCompiler.ASTNodes.NumNode;
import VEW.XMLCompiler.ASTNodes.RuleNode;
import VEW.XMLCompiler.ASTNodes.RuleSequenceNode;
import VEW.XMLCompiler.ASTNodes.UnaryFunctionExprNode;
import VEW.XMLCompiler.ASTNodes.UnaryPrimNode;
import VEW.XMLCompiler.ASTNodes.VBOpNode;
import VEW.XMLCompiler.ASTNodes.VOpNode;
import VEW.XMLCompiler.ASTNodes.VarHistNode;

/**
 * Looks thorugh an ASTree to determine if more than one write and one change statement in it.
 * 
 * It can be run over multiple ASTrees to collect all the multiple writes from eacch tree.
 * 
 * To do this, change the "currentFunction" with the function setCurrentFunction(Function cur)
 * and then run it over the new tree. This should pick up any multiple writes across any number of trees.
 * 
 * @author Chris Bates
 *
 */
public class MultipleWriteAndChangeVisitor implements ASTreeVisitor {

	private Function currentFunction;
	private Map<VariableType, Pair<RuleNode, Function>> writtenVariables;
	private Pair<ChangeNode, Function> changeStatement;
	private Map<VariableType, MultipleWriteException> multipleWriteExceptions;
	private MultipleChangeException multipleChangeExceptions;
	
	
	public MultipleWriteAndChangeVisitor (Function currentFunction) {
		this.currentFunction = currentFunction;
		this.writtenVariables = new HashMap<VariableType, Pair<RuleNode, Function>> ();
		this.changeStatement = null;
		this.multipleWriteExceptions = new HashMap<VariableType, MultipleWriteException> ();
		this.multipleChangeExceptions = null;
	}
	
	/**
	 * Sets up the multipleWriteAndChange visitor, but you must not use the visitor
	 * without setting the function first.
	 */
	public MultipleWriteAndChangeVisitor () {
		this.writtenVariables = new HashMap<VariableType, Pair<RuleNode, Function>> ();
		this.changeStatement = null;
		this.multipleWriteExceptions = new HashMap<VariableType, MultipleWriteException> ();
		this.multipleChangeExceptions = null;
	}
	
	@Override
	public void visit(AssignNode assignNode) {
		
		VariableType var = assignNode.lookupVariableType(this.currentFunction.getParent());
		
		Pair<RuleNode, Function> r = writtenVariables.get(var);
		if (r != null) {
			MultipleWriteException c = multipleWriteExceptions.get(var);
			if (c == null) {
				multipleWriteExceptions.put(var, new MultipleWriteException(var, currentFunction, assignNode, r.getSecond(), r.getFirst()));
			} else {
				c.addRuleToException(currentFunction, assignNode);
			}

		} else {
			writtenVariables.put(var, new Pair<RuleNode, Function>(assignNode, currentFunction));
		}

	}

	@Override
	public void visit(IdNode idNode) {
		return;

	}

	@Override
	public void visit(ChangeNode changeNode) {
		
		if (this.changeStatement == null) {
			this.changeStatement = new Pair<ChangeNode, Function>(changeNode, currentFunction);
		} else {
			if (multipleChangeExceptions == null) {
				multipleChangeExceptions = new MultipleChangeException ();
				multipleChangeExceptions.addChangeNode(this.changeStatement.getFirst(), changeStatement.getSecond());
			}
			multipleChangeExceptions.addChangeNode(changeNode, currentFunction);
		}
		
		return;

	}

	@Override
	public void visit(AssignListNode assignListNode) {
		return;

	}

	@Override
	public void visit(BinaryFunctionNode binaryFunctionNode) {
		return;

	}

	@Override
	public void visit(BinaryPrimitiveNode binaryPrimitiveNode) {
		return;

	}

	@Override
	public void visit(BinOpNode binOpNode) {
		return;

	}

	@Override
	public void visit(BooleanBinOpNode booleanBinOpNode) {
		return;

	}

	@Override
	public void visit(BooleanComparitorNode booleanComparitorNode) {
		return;

	}

	@Override
	public void visit(BooleanNotOpNode booleanNotOpNode) {
		return;

	}

	@Override
	public void visit(CreateNode createNode) {
		return;

	}

	@Override
	public void visit(IfExprNode ifExprNode) {
		return;

	}

	@Override
	public void visit(IfRuleNode ifRuleNode) {
		return;

	}

	@Override
	public void visit(IngestNode ingestNode) {
		return;

	}

	@Override
	public void visit(NegNode negNode) {
		return;

	}

	@Override
	public void visit(NumNode numNode) {
		return;

	}

	@Override
	public void visit(RuleSequenceNode ruleSequenceNode) {
		return;

	}

	@Override
	public void visit(UnaryFunctionExprNode unaryFunctionExprNode) {
		return;

	}

	@Override
	public void visit(UnaryPrimNode unaryPrimNode) {
		return;

	}

	@Override
	public void visit(VarHistNode varHistNode) {
		return;

	}

	@Override
	public void visit(VBOpNode vbOpNode) {
		return;

	}

	@Override
	public void visit(VOpNode vOpNode) {
		return;

	}

	@Override
	public void visit(RuleNode ruleNode) {
		return;

	}

	
	public Function getCurrentFunction() {
		return currentFunction;
	}

	public void setCurrentFunction(Function currentFunction) {
		this.currentFunction = currentFunction;
	}

	public Map<VariableType, Pair<RuleNode, Function>> getWrittenVariables() {
		return writtenVariables;
	}

	public Pair<ChangeNode, Function> getChangeStatement() {
		return changeStatement;
	}

	public Collection<MultipleWriteException> getMultipleWriteExceptions() {
		return multipleWriteExceptions.values();
	}

	public MultipleChangeException getMultipleChangeExceptions() {
		return multipleChangeExceptions;
	}

	public boolean hasExceptions() {
		return multipleChangeExceptions != null || !multipleWriteExceptions.isEmpty();
	}
	
	
	public List<BACONCompilerException> getExceptions() {
		List<BACONCompilerException> result = new ArrayList<BACONCompilerException> ();
		
		for (BACONCompilerException exp : multipleWriteExceptions.values()) {
			result.add(exp);
		}
		
		result.add(multipleChangeExceptions);
		
		
		return result;
	}
	
	

}
