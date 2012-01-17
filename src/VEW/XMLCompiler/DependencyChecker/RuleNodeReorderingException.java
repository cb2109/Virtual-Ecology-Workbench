package VEW.XMLCompiler.DependencyChecker;

import java.util.Collection;

import VEW.XMLCompiler.ASTNodes.BACONCompilerException;
import VEW.XMLCompiler.ASTNodes.RuleNode;



public class RuleNodeReorderingException extends BACONCompilerException {

	private static final long serialVersionUID = -3181724951811956085L;
	private Collection<DependantMetaData<RuleNode>> loop;
	
	
	
	public RuleNodeReorderingException(Collection<DependantMetaData<RuleNode>> loop) {
		super();
		this.loop = loop;
		
		
	}
	
	public RuleNodeReorderingException(Collection<DependantMetaData<RuleNode>> loop, String message) {
		super(message);
		this.loop = loop;
		
	}
	
	public Collection<DependantMetaData<RuleNode>> getLoop() {
		return this.loop;
	}
	
	@Override
	public String getError() {
		String s = "";
		if (message != null) {
			s += message + "\n";
		}
		s += "There is a read write loop of variables on the following lines of Functions:\n";
		for (DependantMetaData<RuleNode> f : loop) {
			s += f.getNode().getLine() + " in " +  f.getParent().getName() + "\n";
		}
		s += "\n This can probably be solved by using the historical value of a local variable.";
		
		return s;
	}

}
