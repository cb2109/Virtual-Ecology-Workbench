package VEW.XMLCompiler.DependencyChecker;

import java.util.ArrayList;
import java.util.Collection;

import VEW.Common.Pair;
import VEW.Planktonica2.Model.Function;
import VEW.XMLCompiler.ASTNodes.BACONCompilerException;
import VEW.XMLCompiler.ASTNodes.ChangeNode;

public class MultipleChangeException extends BACONCompilerException {

	private static final long serialVersionUID = 889737864218863650L;
	private Collection<Pair<ChangeNode, Function>> changeNodes;

	public MultipleChangeException () {
		changeNodes = new ArrayList<Pair<ChangeNode, Function>> ();
	}
	
	@Override
	public String getError() {
		
		String s = "You have made more than one change statement in functions in the same group in: \n";
		
		for (Pair<ChangeNode, Function> change : changeNodes) {
			s += "line: " + change.getFirst().getLine() + " in " + change.getSecond().getName() + "\n";
		}
		
		return s;
	}


	public void addChangeNode(ChangeNode changeNode, Function currentFunction) {
		
		this.changeNodes.add(new Pair<ChangeNode, Function> (changeNode, currentFunction));
		
	}
}
