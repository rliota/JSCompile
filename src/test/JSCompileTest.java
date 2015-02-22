package test;

import jscompile.JSCompile;
import jscompile.exceptions.CircularDependencyException;
import jscompile.exceptions.UnknownImportException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class JSCompileTest {

    public String ROOT_DIR = "";

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testProjectCompilationFromNonLocalDirectory() throws CircularDependencyException, UnknownImportException, IOException {
        String[] args = {"./test-resources/mockproject", "./test-resources/mockCompileTargetDirectory/mockproject.js"};
        JSCompile.main(args);
        File expectedFile = new File("./test-resources/mockCompileTargetDirectory/mockproject.js");
        assert(expectedFile.exists());
    }
}
