package VEW.XMLCompiler.ASTNodes;

public class Variety extends Type {
	
	private Type elementType;
	
	public Variety(String _name, Type _elementType) {
		super(_name);
		elementType = _elementType;
		
	}
	
	public Type getElementType() {
		return elementType;
	}

}