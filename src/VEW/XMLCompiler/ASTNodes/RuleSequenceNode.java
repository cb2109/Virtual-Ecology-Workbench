package VEW.XMLCompiler.ASTNodes;

import java.util.ArrayList;
import VEW.Planktonica2.DisplayOptions;
import VEW.Planktonica2.Model.Catagory;

public class RuleSequenceNode extends ASTree {
	
	private String ruleName;
	private RuleNode rule;
	private RuleSequenceNode seq;

	public RuleSequenceNode(RuleNode rNode) {
		this.rule = rNode;
		this.seq = null;
		this.ruleName = null;
	}
	
	public RuleSequenceNode(String ruleName, RuleNode rNode) {
		this.ruleName = ruleName;
		this.rule = rNode;
	}
	
	public RuleSequenceNode(RuleNode rNode, RuleSequenceNode seqNode) {
		this.rule = rNode;
		this.seq = seqNode;
		this.ruleName = null;
	}
	
	public RuleSequenceNode(String ruleName, RuleNode rNode, RuleSequenceNode seqNode) {
		this.ruleName = ruleName;
		this.rule = rNode;
		this.seq = seqNode;
	}
	
	public void setRuleSequence(RuleSequenceNode seq) {
		this.seq = seq;
	}
	
	@Override
	public void check(Catagory enclosingCategory, ConstructedASTree enclosingTree) {
		rule.check(enclosingCategory, enclosingTree);
		if (seq != null) {
			seq.check(enclosingCategory, enclosingTree);
		}
	}

	@Override
	public String generateXML() {
		String name = "Rule";
		if (ruleName != null) {
			name = ruleName;
		}
		if (seq != null) {
			return name + ":" + rule.generateXML() 
				+ ";" + seq.generateXML();
		} else {
			return name + ":" + rule.generateXML();
		}
	}
	
	public String generateLatex() {
		if (seq != null) {
			if (rule != null) {
				String ruleString = "\\\\ \\\\ \\;";
				if (DisplayOptions.getOptions().PREVIEW_RULE_NAMES && this.ruleName != null) {
					ruleString +=  format_name() + "\\;:\\;";
				}
				ruleString += rule.generateLatex();
				return ruleString + seq.generateLatex();
			}
			return "\\\\ \\\\ \\;???" + seq.generateLatex();
		} else {
			if (rule != null) {
				String ruleString = "\\\\ \\\\ \\;";
				if (DisplayOptions.getOptions().PREVIEW_RULE_NAMES && this.ruleName != null) {
					ruleString +=  format_name() + "\\;:\\;";
				}
				ruleString += rule.generateLatex();
				return ruleString;
			}
			return "\\\\ \\\\ \\;???";
		}
	}

	private String format_name() {
		// Remove all double qoutes and replace spaces with 'LaTeX spaces'
		String name = "";
		for (int i = 0; i < this.ruleName.length(); i++) {
			if (this.ruleName.charAt(i) == ' ')
				name += "\\:";
			else if (!(this.ruleName.charAt(i) == '\"'))
				name += this.ruleName.charAt(i);
		}
		return name;
	}
	
	
	@Override
	public void acceptDependencyCheckVisitor(ASTreeVisitor visitor) {
		
		rule.acceptDependencyCheckVisitor(visitor);
		if(seq != null) {
			seq.acceptDependencyCheckVisitor(visitor);
		}
		visitor.visit(this);
		
	}

	@Override
	public ASTree rearrangeRules(ArrayList<RuleNode> order) {
		
		ArrayList<RuleSequenceNode> nodes = new ArrayList<RuleSequenceNode> (order.size());
		for (int i = 0; i < order.size(); i++) {
			nodes.add(null);
		}
		
		RuleSequenceNode current = this;
		while(current != null) {
			int pos = order.indexOf(current.getRule());
			nodes.set(pos, current);
			current = current.getNext();
		}
		
		for (int i = 0; i < nodes.size(); i++) {
			RuleSequenceNode cur = nodes.get(i);
			if (i+1 < nodes.size()) {
				cur.setRuleSequence(nodes.get(i+1));
			} else {
				cur.setRuleSequence(null);
			}
			
		}
		
		return nodes.get(0);
		
		
	}
	
	
	public RuleSequenceNode getNext() {
		return this.seq;
	}
	
	public RuleNode getRule() {
		return this.rule;
	}
	
}
