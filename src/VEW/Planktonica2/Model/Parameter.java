package VEW.Planktonica2.Model;

import VEW.Common.XML.XMLTag;

public class Parameter extends VariableType {

	public Parameter(Catagory catagory) {
		super(catagory);
	}
	
	public XMLTag buildToXML() throws XMLWriteBackException {
		XMLTag varTag = super.buildToXML();
		varTag.setName("parameter");
		return varTag;
	}

	
}