package VEW.XMLCompiler.ASTNodes;

import java.util.ArrayList;

import VEW.Planktonica2.Model.Catagory;

public abstract class ASTree {

	protected int line_number;
	
	public int getLine() {
		return line_number;
	}
	
	/**
	 * Performs a symantic check on this ASTree.
	 * 
	 * @param enclosingCategory the parent catagory in which this function (ASTree) is supposed to be contained
	 * @param enclosingTree the top level node that represents a constructed tree.
	 */
	public abstract void check(Catagory enclosingCategory, ConstructedASTree enclosingTree);
	
	/**
	 * Generates the equation string representing the tree rooted at that node
	 * @return The equation string
	 */
	public abstract String generateXML();
	
	/**
	 * Generates a latex string representing the tree rooted at that node
	 * @return The latex string
	 */
	public abstract String generateLatex();
	
	public abstract void acceptDependencyCheckVisitor(ASTreeVisitor visitor);

	/**
	 * Rearanges given rules in the order given. 
	 * 
	 * @param order the order in which the rule nodes will now be ordered
	 * @return the top node in the tree
	 */
	public abstract ASTree rearrangeRules(ArrayList<RuleNode> order);
}
