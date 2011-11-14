package VEW.XMLCompiler.ASTNodes;

public abstract class ExprNode extends ASTree{
	protected Type exprType;
	
	public void setExprType(Type _exprType) {
		exprType = _exprType;
	}
	
	public Type getExprType() {
		return exprType;
	}
}