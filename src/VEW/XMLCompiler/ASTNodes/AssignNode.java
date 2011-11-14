package VEW.XMLCompiler.ASTNodes;

import VEW.Planktonica2.ControllerStructure.*;

public class AssignNode extends RuleNode {

	private IdNode identifier;
	private ExprNode expr;
	private VariableType assignVar;
	
	public AssignNode(IdNode identifier, ExprNode expr) {
		this.identifier = identifier;
		this.expr = expr;
	}
	
	@Override
	public void check() throws SemanticCheckException {
		String idName = identifier.getName();
		VariableType var = getCatagory().checkAssignableVariableTables(identifier.getName());
		if (var == null) {
			throw new SemanticCheckException(idName + " is not a assignable variable");
		}
		if ((var instanceof VarietyLocal || var instanceof Local) && var.isAssignedTo()) {
			throw new SemanticCheckException(idName + " has already been assigned to in a previous rule");
		}
		expr.check();
		checkTypeCompatibility(var.getVarType());
		assignVar = var;
		assignVar.setAssigned(true);
		
	}
	
	private void checkTypeCompatibility(Type varType) throws SemanticCheckException{
		Type exprType = expr.getExprType();
		if (varType instanceof VarietyType && !(exprType instanceof VarietyType)) {
			throw new SemanticCheckException("Cannot assign a variety value to a scalar value");
		}
	}

	@Override
	public String generateXML() {
		return "\\assign{" + identifier.generateXML() + "," + expr.generateXML() + "}";
	}

	public VariableType getAssignVar() {
		return assignVar;
	}

}
