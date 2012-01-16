package VEW.XMLCompiler.ANTLR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.antlr.runtime.RecognitionException;

import VEW.Common.XML.XMLTag;
import VEW.Planktonica2.Model.Function;
import VEW.XMLCompiler.ASTNodes.BACONCompilerException;
import VEW.XMLCompiler.ASTNodes.ConstructedASTree;
import VEW.XMLCompiler.ASTNodes.RuleNode;
import VEW.XMLCompiler.DependencyChecker.OrderingAgent;

public class BACONCompiler {
	
	private Function function;
	private String code;
	private ConstructedASTree tree;
	
	public BACONCompiler(Function function, String code) {
		this.function = function;
		this.code = code;
		tree = null;
	}
	
	public List<XMLTag> compile() throws CompilerException{
		
		ANTLRParser parser = new ANTLRParser(code);
		try {
			 tree = parser.getAST();
		} catch (RecognitionException e) {
			e.printStackTrace();
		}
		
		if (tree.hasExceptions()) {
			throw new CompilerException(function, tree.getExceptions());
		}
		
		tree.checkTree(function.getParent());
		if (tree.hasExceptions()) {
			throw new CompilerException(function, tree.getExceptions());
		}
		
		if (tree.hasWarnings()) {
			function.addWarnings(tree.getWarnings());
		}
		
		List<BACONCompilerException> exceptions = checkForRWDependency(tree);
		if (!exceptions.isEmpty()) {
			throw new CompilerException(function, exceptions);
		}
		
		OrderingAgent o = new OrderingAgent(this.function, this.tree);
		if (o.reorderNodes()) {
			tree.rearrangeRules(o.getFunctionOrder().get(this.tree));
		} else {
			throw new CompilerException(this.function, extractErrors(o));
		}
		
		
		return tree.compileTree();
	}

	private List<BACONCompilerException> checkForRWDependency(ConstructedASTree tree) {
		
		OrderingAgent o = new OrderingAgent(function, tree);
		
		if (!o.reorder()) {
			return extractErrors(o);
			
		} else {
			HashMap<ConstructedASTree, ArrayList<RuleNode>> trees = o.getFunctionOrder();
			
			for (Entry<ConstructedASTree, ArrayList<RuleNode>> pair : trees.entrySet()) {
				pair.getKey().rearrangeRules(pair.getValue());
			}
		}
		
		
		
		return null;
	}
	
	private List<BACONCompilerException> extractErrors(OrderingAgent o) {
		List<BACONCompilerException> exceptions = new ArrayList<BACONCompilerException> ();
		exceptions.addAll(o.getMultipleWrite());
		exceptions.addAll(o.getFunctionLoops());
		return exceptions;
	}

	public ConstructedASTree getTree() {
		return tree;
	}

	
}
