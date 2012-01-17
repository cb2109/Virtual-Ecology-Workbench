package VEW.XMLCompiler.DependencyChecker;

import java.util.Collection;

import VEW.Planktonica2.Model.Function;
import VEW.XMLCompiler.ASTNodes.BACONCompilerException;

public class FunctionReorderingException extends BACONCompilerException {

	private static final long serialVersionUID = -1176042345923861835L;
	private Collection<Function> loop;
	
	
	
	public FunctionReorderingException(Collection<Function> loop) {
		super();
		this.loop = loop;
		
		
	}
	
	public FunctionReorderingException(Collection<Function> loop, String message) {
		super(message);
		this.loop = loop;
		
	}
	
	public Collection<Function> getLoop() {
		return this.loop;
	}
	
	@Override
	public String getError() {
		String s = "";
		if (message != null) {
			s += message + "\n";
		}
		s += "There is a read write loop of variables inside the following functions:\n";
		for (Function f : loop) {
			s += f.getName() + "\n";
		}
		s += "\n This can probably be solved by using the historical value of a local variable.";
		
		return s;
	}
}
