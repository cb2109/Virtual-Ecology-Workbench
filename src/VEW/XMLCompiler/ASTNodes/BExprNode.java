package VEW.XMLCompiler.ASTNodes;

import java.util.ArrayList;

import VEW.Planktonica2.Model.Type;


public abstract class BExprNode extends ASTree{
	
	protected Type bExprType;
	
	public void setBExprType(Type _bExprType) {
		bExprType = _bExprType;
	}
	
	public Type getBExprType() {
		return bExprType;
	}

	
	@Override
	public ASTree rearrangeRules(ArrayList<RuleNode> order) {
		return null;
	}

	
	
}
