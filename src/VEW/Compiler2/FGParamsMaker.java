package VEW.Compiler2;

import java.io.PrintWriter;

import VEW.Common.StringTools;
import VEW.Common.XML.XMLTag;


public class FGParamsMaker {
  public static void writeFGParamsJava(String fileName, XMLTag model) {
    try {
      PrintWriter PW = StringTools.OpenOutputFile(fileName);
      PW.println("package VEW.Sim;");
      PW.println("public abstract class FGParams {");
      PW.println("  int _type;");
      PW.println("  String _name;");
      PW.println("  int _speciesOfFG;");
      PW.println("}");
      PW.flush();
      PW.close();
    } catch (Exception e) { e.printStackTrace(); }

  }

}

