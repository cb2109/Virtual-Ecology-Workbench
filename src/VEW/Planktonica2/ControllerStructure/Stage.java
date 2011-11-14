package VEW.Planktonica2.ControllerStructure;

import VEW.Common.XML.XMLTag;

public class Stage implements BuildFromXML {
	
	private boolean log;
	private boolean closure;
	private String name;
	private String comment;
	
	@Override
	public BuildFromXML build(XMLTag tag) {
		
		String logValue = tag.getAttribute("log");
		if (logValue != null) {
			this.log = Boolean.valueOf(logValue);
		}
		
		String closureValue = tag.getAttribute("closure");
		if (closureValue != null) {
			this.closure = Boolean.valueOf(closureValue);
		}
		
		XMLTag nameTag = tag.getTag("name"); 
		if (nameTag != null) {
			this.name = nameTag.getValue();
		}
		
		XMLTag commentTag = tag.getTag("comment");
		if (commentTag != null) {
			this.comment = commentTag.getValue();
		}
		
		return this;
	}

	
	
	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public boolean isClosure() {
		return closure;
	}

	public void setClosure(boolean closure) {
		this.closure = closure;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	
	
	
}