package VEW.XMLCompiler.ASTNodes;

import VEW.Planktonica2.Model.Catagory;

public class VarHistNode extends ExprNode {

	private IdNode identifier;
	private ExprNode expression;

	public VarHistNode (IdNode identifier, ExprNode expression, int line) {
		this.identifier = identifier;
		this.expression = expression;
		this.line_number = line;
		
	}
	
	@Override
	public void check(Catagory enclosingCategory, ConstructedASTree enclosingTree) {
		identifier.check(enclosingCategory, enclosingTree);
		expression.check(enclosingCategory, enclosingTree);
		setExprType(expression.getExprType());
		identifier.set_units(enclosingCategory);
		units = identifier.getUnits();
	}

	@Override
	public String generateXML() {
		return "\\varhist{" + identifier.generateXML() + "," + expression.generateXML() + "}";
	}
	
	@Override
	public String generateLatex() {
		String id = "???";
		if (identifier != null)
			id = identifier.generateLatex();
		String exp = "???";
		if (expression != null)
			exp = expression.generateLatex();
		return "varhist( " + id + " , " + exp + " )";
	}

	
	/**
	 * Does not visit the variable (identifier), as the variable is referencing the previous time step
	 */
	@Override
	public void acceptDependencyCheckVisitor(ASTreeVisitor visitor) {
		
		//identifier.acceptDependencyCheckVisitor(visitor);
		expression.acceptDependencyCheckVisitor(visitor);
		visitor.visit(this);
		
	}

	public IdNode getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IdNode identifier) {
		this.identifier = identifier;
	}

	public ExprNode getExpression() {
		return expression;
	}

	public void setExpression(ExprNode expression) {
		this.expression = expression;
	}

	
}
