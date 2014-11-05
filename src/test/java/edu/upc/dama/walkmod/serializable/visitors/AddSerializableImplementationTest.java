package edu.upc.dama.walkmod.serializable.visitors;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.walkers.VisitorContext;

import edu.upc.dama.walkmod.serializable.visitors.AddSerializableImplementation;

public class AddSerializableImplementationTest {
	
	private static String COMPILATION_DIR = "./src/test/resources/classes";

	@Test
	public void testParsing() throws Exception {
		
		AddSerializableImplementation visitor = new AddSerializableImplementation();
		
		
		File f = new File("src/test/resources/classes/edu/upc/tests/HelloWorldBasic.java");
		if (compile(f)) {
			File aux = new File(COMPILATION_DIR);
			ClassLoader cl = new URLClassLoader(new URL[] { aux.toURI()
					.toURL() });
			visitor.setClassLoader(cl);
		
			CompilationUnit cu = ASTManager.parse(f);
			Assert.assertNotNull(cu);
		
		
			visitor.visit(cu, new VisitorContext());
			
			String importName = cu.getImports().get(0).getName().getName();
			assertEquals(importName,"java.io.Serializable");
			
			ClassOrInterfaceDeclaration type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
			String implementsName = type.getImplements().get(0).getName();
			assertEquals(implementsName,"Serializable");
		}
		
		f = new File("src/test/resources/classes/edu/upc/tests/HelloWorldConflict1.java");
		if (compile(f)) {
			File aux = new File(COMPILATION_DIR);
			ClassLoader cl = new URLClassLoader(new URL[] { aux.toURI()
					.toURL() });
			visitor.setClassLoader(cl);
		
			CompilationUnit cu = ASTManager.parse(f);
			Assert.assertNotNull(cu);
			
			visitor = new AddSerializableImplementation();
			visitor.visit(cu, new VisitorContext());
			
			int numImports = cu.getImports().size();
			assertEquals(numImports,1);
			
			ClassOrInterfaceDeclaration type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
			String implementsName = type.getImplements().get(1).getName();
			assertEquals(implementsName,"java.io.Serializable");
		}
		
		/*f = new File("src/test/resources/sourceConflict2Example.txt");
		cu = ASTManager.parse(f);
		Assert.assertNotNull(cu);
		
		visitor = new AddSerializableImplementation();
		visitor.visit(cu, new VisitorContext());
		
		numImports = cu.getImports().size();
		assertEquals(numImports,1);
		
		type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
		int numImplements = type.getImplements().size();
		assertEquals(numImplements,1);
		
		type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
		ClassOrInterfaceType coi = type.getImplements().get(0);
		assertEquals(coi.toString(),"java.io.Serializable");
		

		f = new File("src/test/resources/sourceConflict3Example.txt");
		cu = ASTManager.parse(f);
		Assert.assertNotNull(cu);
		
		visitor = new AddSerializableImplementation();
		visitor.visit(cu, new VisitorContext());
		
		numImports = cu.getImports().size();
		assertEquals(numImports,0);
		
		type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
		numImplements = type.getImplements().size();
		assertEquals(numImplements,1);
		
		type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
		coi = type.getImplements().get(0);
		assertEquals(coi.toString(),"java.io.Serializable");
		
		f = new File("src/test/resources/sourceConflict4Example.txt");
		cu = ASTManager.parse(f);
		Assert.assertNotNull(cu);
		
		visitor = new AddSerializableImplementation();
		visitor.visit(cu, new VisitorContext());
		
		numImports = cu.getImports().size();
		assertEquals(numImports,1);
		
		type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
		numImplements = type.getImplements().size();
		assertEquals(numImplements,1);
		
		type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
		coi = type.getImplements().get(0);
		assertEquals(coi.toString(),"java.io.Serializable");
		
		f = new File("src/test/resources/sourceConflict5Example.txt");
		cu = ASTManager.parse(f);
		Assert.assertNotNull(cu);
		
		visitor = new AddSerializableImplementation();
		visitor.visit(cu, new VisitorContext());
		
		numImports = cu.getImports().size();
		assertEquals(numImports,1);
		
		type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
		numImplements = type.getImplements().size();
		assertEquals(numImplements,1);
		
		type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
		coi = type.getImplements().get(0);
		assertEquals(coi.toString(),"Serializable");
		
		f = new File("src/test/resources/sourceConflict6Example.txt");
		cu = ASTManager.parse(f);
		Assert.assertNotNull(cu);

		visitor = new AddSerializableImplementation();
		visitor.visit(cu, new VisitorContext());
		
		numImports = cu.getImports().size();
		assertEquals(numImports,5);
		
		type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
		numImplements = type.getImplements().size();
		assertEquals(numImplements,2);
		
		type = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
		coi = type.getImplements().get(1);
		assertEquals(coi.toString(),"Serializable");*/
		
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean compile(File... files) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		List<String> optionList = new ArrayList<String>();
		File tmp = new File(COMPILATION_DIR);
		boolean result = (tmp.mkdirs() || tmp.exists()) && tmp.canWrite();
		if (result) {
			optionList.addAll(Arrays.asList("-d", tmp.getAbsolutePath()));
			StandardJavaFileManager sjfm = compiler.getStandardFileManager(
					null, null, null);
			Iterable fileObjects = sjfm.getJavaFileObjects(files);
			JavaCompiler.CompilationTask task = compiler.getTask(null, null,
					null, optionList, null, fileObjects);
			task.call();
			sjfm.close();
		}
		return result;
	}
}
