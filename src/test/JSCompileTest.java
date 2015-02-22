package test;

import jscompileRENAME.JSCompile;
import jscompileRENAME.exceptions.CircularDependencyException;
import jscompileRENAME.exceptions.UnknownImportException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class JSCompileTest {

    public String COMPILE_SRC = "";
    public String COMPILE_TARGET = "./test-resources/mockCompileTargetDirectory/mockproject.js";

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        File target = new File(COMPILE_TARGET);
        if(target.exists()){
            target.delete();
        }
    }


    @Test
    public void testProjectCompilationFromNonLocalDirectory() throws CircularDependencyException, UnknownImportException, IOException {
        String[] args = {"./test-resources/mockproject", COMPILE_TARGET};
        JSCompile.main(args);
        File expectedFile = new File(COMPILE_TARGET);
        assert(expectedFile.exists());
    }
}
