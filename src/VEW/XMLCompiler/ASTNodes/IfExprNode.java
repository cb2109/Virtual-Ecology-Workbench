package VEW.XMLCompiler.ASTNodes;

public class IfExprNode extends ExprNode {

	private BExprNode conditionExpr;
	private ExprNode thenExpr;
	private ExprNode elseExpr;
	
	public IfExprNode(BExprNode conditionExpr, ExprNode thenExpr, ExprNode elseExpr) {
		this.conditionExpr = conditionExpr;
		this.thenExpr = thenExpr;
		this.elseExpr = elseExpr;
	}
	
	@Override
	public void check() throws SemanticCheckException {
		conditionExpr.check();
		thenExpr.check();
		elseExpr.check();

	}

	@Override
	public String generateXML() {
		return "\\conditional{" + conditionExpr.generateXML() + "," + thenExpr.generateXML()
		 + "," + elseExpr.generateXML() + "}";
	}
	
	public String generateLatex() {
		return "if\\;(" + conditionExpr.generateLatex() + ")\\;then\\;(" + thenExpr.generateLatex()
		 + ")\\;else\\;(" + elseExpr.generateLatex() + ")";
	}

}
