package BACONCompiler.CycleChecker;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import VEW.XMLCompiler.ASTNodes.AssignNode;
import VEW.XMLCompiler.ASTNodes.RuleNode;
import VEW.XMLCompiler.ASTNodes.RuleSequenceNode;

public class ReorderRuleSequenceNodes {

	RuleSequenceNode top;
	ArrayList<RuleNode> order;
	
	@Before
	public void setUp() throws Exception {
		
		order = new ArrayList<RuleNode> ();
		order.add(new AssignNode(null, null, 1));
		order.add(new AssignNode(null, null, 2));
		order.add(new AssignNode(null, null, 3));
		order.add(new AssignNode(null, null, 4));
		order.add(new AssignNode(null, null, 5));
		
		top = new RuleSequenceNode(order.get(3));
		RuleSequenceNode cur = new RuleSequenceNode(order.get(0));
		RuleSequenceNode next = new RuleSequenceNode(order.get(4));
		top.setRuleSequence(cur);
		cur.setRuleSequence(next);
		
		cur = new RuleSequenceNode(order.get(1));
		next.setRuleSequence(cur);
		
		next = new RuleSequenceNode(order.get(2));
		cur.setRuleSequence(next);
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		top.rearrangeRules(order);
	}

}
