package VEW.XMLCompiler.ASTNodes;

public class CreateNode extends RuleNode {

	private IdNode identifier;
	private ExprNode expression;
	private AssignListNode assignList;

	public CreateNode (IdNode identifier, ExprNode expression) {
		this.identifier = identifier;
		this.expression = expression;
		this.assignList = null;
	}
	
	public CreateNode (IdNode identifier, ExprNode expression, AssignListNode assignList) {
		this.identifier = identifier;
		this.expression = expression;
		this.assignList = assignList;
	}
	
	@Override
	public void check() throws SemanticCheckException {
		// TODO Auto-generated method stub

	}

	@Override
	public String generateXML() {
		if (assignList != null) {
			return "\\create{" + identifier.generateXML() + "," 
			 + expression.generateXML() + "," + assignList.generateXML() + "}";
		} else {
			return "\\create{" + identifier.generateXML() + "," 
			 + expression.generateXML() + "}";
		}
	}
	
	public String generateLatex() {
		String id = "???";
		if (identifier != null)
			id = identifier.generateLatex();
		String exp = "???";
		if (expression != null)
			exp = expression.generateLatex();
		if (assignList != null) {
			return "create(" + id + "," 
			 + exp + ")\\;with\\;[" + assignList.generateLatex() + "]";
		} else {
			return "create(" + id + "," 
			 + exp + ")";
		}
	}

}