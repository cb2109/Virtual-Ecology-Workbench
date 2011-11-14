package VEW.XMLCompiler.ASTNodes;

import VEW.Planktonica2.ControllerStructure.Type;

public class NumNode extends ExprNode {

	private float value;
	
	public NumNode(float value) {
		this.value = value;
	}
	
	@Override
	public void check() throws SemanticCheckException {
		AmbientVariableTables tables = AmbientVariableTables.getTables();
		setExprType((Type)tables.checkTypeTable("$float"));
	}

	@Override
	public String generateXML() {
		return "\\val{\\sival{" + value + ",0},\\unit{0,0,0}}";
	}

}
