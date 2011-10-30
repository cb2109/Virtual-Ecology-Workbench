package VEW.XMLCompiler.ANTLR.test;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import VEW.XMLCompiler.ANTLR.TreeWalkerException;


/**
 * Test that passes when the File given does not parse correctly.
 * @author Chris Bates
 *
 */
public abstract class FileFailsParserTest extends GenericParserTest {

	/**
	 * Runs a test that parses when the file fails to parse i.e. throws a Recognition exception.
	 * 
	 * @throws RecognitionException test succeeds on 
	 * @throws TreeWalkerException
	 */
	@Test(expected=RecognitionException.class)
	public void testParserFail() throws RecognitionException, TreeWalkerException {
		
		p.getAST();
	}

}
