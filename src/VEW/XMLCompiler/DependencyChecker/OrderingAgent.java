package VEW.XMLCompiler.DependencyChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import VEW.Planktonica2.Model.Function;
import VEW.Planktonica2.Model.Stage;
import VEW.XMLCompiler.ASTNodes.BACONCompilerException;
import VEW.XMLCompiler.ASTNodes.ConstructedASTree;
import VEW.XMLCompiler.ASTNodes.RuleNode;

/**
 * Finds a rearangement of functions in all the catagories of a model.
 * 
 * Use DependencyCheckerWrapper to access this functionality.
 * 
 * @author Chris Bates
 *
 */
public class OrderingAgent {

	private Collection<DependantMetaData<ConstructedASTree>> trees;
	private Collection<BACONCompilerException> loops;
	private HashMap<ConstructedASTree, ArrayList<RuleNode>> functionOrder;
	private ArrayList<Function> ordering;

	public OrderingAgent() {
		this.trees = new ArrayList<DependantMetaData<ConstructedASTree>> ();
		this.functionOrder = new HashMap<ConstructedASTree, ArrayList<RuleNode>> ();
		this.ordering = new ArrayList<Function> ();
		this.loops = new ArrayList<BACONCompilerException> ();
	}
	
	/**
	 * 
	 * @param m the model to be checked for consistency.
	 * @param quitOnMultipleWrite whether or not the code should try to give a reordering of the trees if there has been a multiple write.
	 */
	public OrderingAgent(Collection<DependantMetaData<ConstructedASTree>> trees) {
		this.trees = trees;
		this.functionOrder = new HashMap<ConstructedASTree, ArrayList<RuleNode>> ();
		this.ordering = new ArrayList<Function> ();
		this.loops = new ArrayList<BACONCompilerException> ();
	}

	public OrderingAgent(Map<Function, ConstructedASTree> trees) {
		
		this.trees = new ArrayList<DependantMetaData<ConstructedASTree>> ();
		this.functionOrder = new HashMap<ConstructedASTree, ArrayList<RuleNode>> ();
		this.ordering = new ArrayList<Function> ();
		this.loops = new ArrayList<BACONCompilerException> ();
		
		for (Entry<Function, ConstructedASTree> tree : trees.entrySet()) {
			
			this.trees.add(new DependantMetaData<ConstructedASTree> (tree.getValue(), tree.getKey()));
			
		}
	}
	
	public OrderingAgent(Function function, ConstructedASTree tree) {
		
		this.trees = new ArrayList<DependantMetaData<ConstructedASTree>> ();
		this.functionOrder = new HashMap<ConstructedASTree, ArrayList<RuleNode>> ();
		this.ordering = new ArrayList<Function> ();
		this.loops = new ArrayList<BACONCompilerException> ();
		
		
		trees.add(new DependantMetaData<ConstructedASTree> (tree, function));
	}

	

	public boolean reorder () {
		
		return (reorderNodes() ? reorderFunctions() : false);
	}
	
	public boolean reorderNodes() {
		
		// reorder each Function
		for (DependantMetaData<ConstructedASTree> data : trees) {
			if (!fillFunctionOrder(data.getNode(), data.getParent(), calculateDependencies(data))) {
				return false;
			}			
		}
		
		return true;
	}
	
	public Collection<Dependency<DependantMetaData<RuleNode>>> calculateDependencies(DependantMetaData<ConstructedASTree> tree) {
		
		ASTreeDependencyVisitor visitor = new ASTreeDependencyVisitor(tree.getParent());
		tree.getNode().checkASTree(visitor);
		
		return visitor.getInTreeDependencies();
		
	}
	
	private boolean fillFunctionOrder(ConstructedASTree parent, Function parentFunc, Collection<Dependency<DependantMetaData<RuleNode>>> tree) {
		
		// generate graph
		DependencyGraphGenerator<DependantMetaData<RuleNode>> gen = new DependencyGraphGenerator<DependantMetaData<RuleNode>> ();
		Collection<Representative<DependantMetaData<RuleNode>>> graph = gen.createRepresentatives(tree);

		// check for cycles
		DependencyCheck<DependantMetaData<RuleNode>> cycleChecker = new LoopDependencyCheck<DependantMetaData<RuleNode>>();

		(new DependencyChecker<DependantMetaData<RuleNode>> (tree, cycleChecker)).checkDependencies();


		if (!cycleChecker.getResults().isEmpty()) {
			Collection<Collection<Dependency<DependantMetaData<RuleNode>>>> loops = cycleChecker.getResults();
			for (Collection<Dependency<DependantMetaData<RuleNode>>> loop : loops) {
				Collection<DependantMetaData<RuleNode>> funcLoop = new ArrayList<DependantMetaData<RuleNode>> (loop.size());
				
				Iterator<Dependency<DependantMetaData<RuleNode>>> it = loop.iterator();
				Dependency<DependantMetaData<RuleNode>> dep = null;
				while (it.hasNext()) {
					dep = it.next();
					funcLoop.add(dep.getDependent1());
				}
				if (dep != null && dep.getDependent2() != null) {
					funcLoop.add(dep.getDependent2());
				}

				this.loops.add(new RuleNodeReorderingException(funcLoop));
			}
			
			return false;

		}
		
		// traverse graph from every other point
		ArrayList<Representative<DependantMetaData<RuleNode>>> order = new ArrayList<Representative<DependantMetaData<RuleNode>>> ();
		// add all to order
		for (Representative<DependantMetaData<RuleNode>> node : graph) {
			order.add(node);
		}

		boolean changed = true;
		int retries = order.size()*order.size();
		
		while (changed && retries > 0) {
			changed = false;
			for (int nodeIndex = 0; nodeIndex < order.size(); nodeIndex++) {
				int currentIndex = nodeIndex;
				Representative<DependantMetaData<RuleNode>> curNode = order.get(nodeIndex);
				for (Representative<DependantMetaData<RuleNode>> child : curNode.getChildren()) {
					int childIndex = order.indexOf(child);
					if (childIndex < currentIndex) {
						// move parent behind child and set changed to true
						// removes node from current index
						order.remove(currentIndex);
						// adds curNode in at child (which moves child along by one)
						order.add(childIndex, curNode);
						// updates position of currentNode!
						currentIndex = childIndex;
						changed = true;
					}
				}
			}

			if (retries <= 0) {
				return false;
			}
			retries--;
		}

		// remove representatives and metaData
		ArrayList<RuleNode> output = new ArrayList<RuleNode> (order.size());
		for (Representative<DependantMetaData<RuleNode>> node : order) {
			output.add(node.getRepresentedObject().getNode());
		}
		
		// gets the remaining nodes
		ASTreeDependencyVisitor visitor = new ASTreeDependencyVisitor(parentFunc);
		parent.checkASTree(visitor);
		
		// add the rest of rule nodes onto the end.
		for (DependantMetaData<RuleNode> rule : visitor.getAllRuleNodes()) {
			if (!output.contains(rule.getNode())) {
				output.add(rule.getNode());
			}
		}
		
		this.functionOrder.put(parent, output);

		return true;

	}

	/*
	 * Reorder Functions in group
	 */
	
	public boolean reorderFunctions() {
		// group by stage called In and work out dependencies in between stage related things
		Collection<Collection<DependantMetaData<ConstructedASTree>>> groups = groupTreesByStage();
		
		
		Collection<Dependency<Function>> groupWiseDependencies 
			= new ArrayList<Dependency<Function>> ();
		
		for (Collection<DependantMetaData<ConstructedASTree>> group : groups) {
			
			Collection<Dependency<Function>> dependencies = getDependeciesInGroup(group);
			if (dependencies != null) {
				groupWiseDependencies.addAll(dependencies);
			} else {
				return false;
			}
		}
		
		return orderFunctions(groupWiseDependencies);
	}
	
	



	/**
	 * Uses the stages that the functions are called in to group them into calledIn groups.
	 * 
	 * It gets the functions from the given collections of Meta Data.
	 * 
	 * @return a collection of ConstructedASTrees (w. data) that are in a certain stage group.
	 */
	private Collection<Collection<DependantMetaData<ConstructedASTree>>> groupTreesByStage() {
		
		HashMap<Stage, Collection<DependantMetaData<ConstructedASTree>>> stageGroups =
				new HashMap<Stage, Collection<DependantMetaData<ConstructedASTree>>> ();
		
		for (DependantMetaData<ConstructedASTree> data : trees) {
			
			Collection<Stage> stagesCalledIn = data.getParent().getCalledIn();
			for (Stage s : stagesCalledIn) {
				Collection<DependantMetaData<ConstructedASTree>> group = stageGroups.get(s);
				if (group == null) {
					group = new ArrayList<DependantMetaData<ConstructedASTree>> ();
				}
				
				group.add(data);
				stageGroups.put(s, group);
				
			}
			
		}
		
		return stageGroups.values();
	}
	
	private Collection<Dependency<Function>> getDependeciesInGroup (Collection<DependantMetaData<ConstructedASTree>> group) {
		
		ASTreeDependencyVisitor visitor = new ASTreeDependencyVisitor(null);
		for (DependantMetaData<ConstructedASTree> tree : group) {
			visitor.setParentFunction(tree.getParent());
			tree.getNode().checkASTree(visitor);
		}
		
		// figure out the dependencies between on each function.
		Collection<Dependency<Function>> funcDepen 
			= new ArrayList<Dependency<Function>> ();
		for (Dependency<DependantMetaData<RuleNode>> dependancy : visitor.getInTreeDependencies()) {
			Function f1 = dependancy.getDependent1().getParent();
			Function f2 = dependancy.getDependent2().getParent();
			if (f1 != f2) {
				funcDepen.add(new Dependency<Function>(f1, f2));
			}
		}
		
		
		
		return funcDepen;
		
	}
	
	private boolean orderFunctions(Collection<Dependency<Function>> groupWiseDependencies) {
		
		// check for cycles
		DependencyCheck<Function> cycleChecker = new LoopDependencyCheck<Function>();
				
		(new DependencyChecker<Function> (groupWiseDependencies, cycleChecker)).checkDependencies();
		
		
		if (!cycleChecker.getResults().isEmpty()) {
			Collection<Collection<Dependency<Function>>> loops = cycleChecker.getResults();
			for (Collection<Dependency<Function>> loop : loops) {
				Collection<Function> funcLoop = new ArrayList<Function> (loop.size());
				Iterator<Dependency<Function>> it = loop.iterator();
				Dependency<Function> current = null;
				while (it.hasNext()) {
					current = it.next();
					funcLoop.add(current.getDependent1());
				}
				
				if (current != null) {
					funcLoop.add(current.getDependent2());
				}
				
				
				this.loops.add(new FunctionReorderingException(funcLoop));
			}
			
			return false;
			
		}

		// generate graph
		DependencyGraphGenerator<Function> gen = new DependencyGraphGenerator<Function> ();
		Collection<Representative<Function>> graph = gen.createRepresentatives(groupWiseDependencies);
		
		
		
		
		// traverse graph from every other point
		ArrayList<Representative<Function>> order = new ArrayList<Representative<Function>> ();
		// add all to order
		for (Representative<Function> node : graph) {
			order.add(node);
		}
		
		boolean changed = true;
		int retries = order.size()*order.size();
		if (retries == 0) {
			return true;
		}
		while (changed && retries > 0) {
			changed = false;
			for (int nodeIndex = 0; nodeIndex < order.size(); nodeIndex++) {
				int currentIndex = nodeIndex;
				Representative<Function> curNode = order.get(nodeIndex);
				for (Representative<Function> child : curNode.getChildren()) {
					int childIndex = order.indexOf(child);
					if (childIndex < currentIndex) {
						// move parent behind child and set changed to true
						// removes node from current index
						order.remove(currentIndex);
						// adds curNode in at child (which moves child along by one)
						order.add(childIndex, curNode);
						// updates position of currentNode!
						currentIndex = childIndex;
						changed = true;
					}
				}
			}

			if (retries <= 0) {
				return false;
			}
			retries--;
		}

		// remove representatives and metaData
		ArrayList<Function> output = new ArrayList<Function> (order.size());
		for (Representative<Function> node : order) {
			output.add(node.getRepresentedObject());
		}
		
		for (DependantMetaData<ConstructedASTree> dep : this.trees) {
			if (!output.contains(dep.getParent())) {
				output.add(dep.getParent());
			}
		}
		
		this.ordering = output;

		return true;
		
	}

	
	/*
	 * Getters and setters
	 */
	public Collection<DependantMetaData<ConstructedASTree>> getTrees() {
		return trees;
	}

	public void setTrees(Collection<DependantMetaData<ConstructedASTree>> trees) {
		this.trees = trees;
	}
	
	public void setTrees(Map<Function, ConstructedASTree> trees) {
		
		for (Entry<Function, ConstructedASTree> tree : trees.entrySet()) {
			
			this.trees.add(new DependantMetaData<ConstructedASTree> (tree.getValue(), tree.getKey()));
			
		}
		
	}


	public HashMap<ConstructedASTree, ArrayList<RuleNode>> getFunctionOrder() {
		return functionOrder;
	}

	public ArrayList<Function> getOrdering() {
		return ordering;
	}
	
	public Collection<BACONCompilerException> getFunctionLoops() {
		return loops;
	}

	/**
	 * Extracts all exceptions from the ordering agent and makes a List of compiler exceptions out of it. 
	 * 
	 * @param o the ordering agent to extract
	 * @return the loops and multiple write exception (on local variables) for the orderingAgent
	 */
	public List<BACONCompilerException> extractErrors() {
		List<BACONCompilerException> exceptions = new ArrayList<BACONCompilerException> ();
		exceptions.addAll(getFunctionLoops());
		return exceptions;
	}

	
}
