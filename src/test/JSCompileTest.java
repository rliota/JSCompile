package test;

import jscompile.JSCompile;
import jscompile.exceptions.CircularDependencyException;
import jscompile.exceptions.UnknownImportException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class JSCompileTest {

    public String COMPILE_SRC = "";
    public String COMPILE_TARGET = "./test-resources/mockCompileTargetDirectory/";

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        File target = new File(COMPILE_TARGET+"mockproject.js");
        File target2 = new File(COMPILE_TARGET+"I-have-dashes-yo.js");
        if(target.exists()){
            target.delete();
        }
        if(target2.exists()){
            target2.delete();
        }
    }


    @Test
    public void testProjectCompilationFromNonLocalDirectory() throws CircularDependencyException, UnknownImportException, IOException {
        String[] args = {"./test-resources/mockproject", COMPILE_TARGET+"mockproject.js"};
        JSCompile.main(args);
        File expectedFile = new File(COMPILE_TARGET+"mockproject.js");
        assert(expectedFile.exists());
    }


    @Test
    public void testPackageNameOutputDoesNotAffectPackageFunctionName() throws CircularDependencyException, UnknownImportException, IOException {
        String[] args = {"./test-resources/mockproject", COMPILE_TARGET+"I-have-dashes-yo.js"};
        JSCompile.main(args);
        File expectedFile = new File(COMPILE_TARGET+"I-have-dashes-yo.js");
        assert(expectedFile.exists());
        FileReader fr = new FileReader(expectedFile);
        Scanner s = new Scanner(expectedFile);
        assert(s.findWithinHorizon(Pattern.quote("function mockproject(){"), 200) != null);
    }

}
