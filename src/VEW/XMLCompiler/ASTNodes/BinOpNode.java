package VEW.XMLCompiler.ASTNodes;

public class BinOpNode extends ExprNode {
	
	private MathematicalOperator operator;
	private ExprNode lExpr;
	private ExprNode rExpr;
	
	public BinOpNode (MathematicalOperator operator, ExprNode lExpr, ExprNode rExpr) {
		this.operator = operator;
		this.lExpr = lExpr;
		this.rExpr = rExpr;
	}

	@Override
	public void check() throws SemanticCheckException {
		// TODO Auto-generated method stub

	}

	@Override
	public String generateXML() {
		String op = "";
		switch (operator) {
		case PLUS     : op = "add"; break; 
		case MINUS    : op = "sub"; break; 
		case MULTIPLY : op = "mul"; break; 
		case DIVIDE   : op = "div"; break; 
		case POWER    : op = "pow"; break; 
		}
		return "\\" + op + "{" + lExpr.generateXML() + "," + rExpr.generateXML() + "}";
	}

	public String generateLatex() {
		String func = "";
		switch (operator) {
		case PLUS     : func  = "+";
		case MINUS    : func  = "-";
		case MULTIPLY : func  = "*";
		case DIVIDE   : return "\frac {" + leftExpr.generateLatex() + "} {" + rightExpr.generateLatex() + "}";
		case POWER    : func  = "^";
		}
		return leftExpr.generateLatex() + func + rightExpr.generateLatex();
	}
}
