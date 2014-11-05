/* 
  Copyright (C) 2013 Raquel Pau and Albert Coroleu.
 
 Walkmod is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 Walkmod is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with Walkmod.  If not, see <http://www.gnu.org/licenses/>.*/
package edu.upc.dama.walkmod.serializable.visitors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.ImportDeclaration;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.javalang.compiler.TypeTable;
import org.walkmod.javalang.visitors.VisitorSupport;
import org.walkmod.walkers.VisitorContext;

public class AddSerializableImplementation extends
		VisitorSupport<VisitorContext> {

	private boolean includeImport = true;
	private boolean includeSimpleImplement = true;
	
	private CompilationUnit cuPrivate = null;
	private ClassLoader classLoader;
	private TypeTable<VisitorContext> tt;

	@Override
	public void visit(CompilationUnit cu, VisitorContext ctx) {
		cuPrivate = cu;
		tt = new TypeTable<VisitorContext>();
		tt.setClassLoader(classLoader);
		tt.visit(cu, ctx);		
		super.visit(cu, ctx);
	}
	
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void visit(ClassOrInterfaceDeclaration type, VisitorContext ctx) {
		if (!type.isInterface()) {
			
			//implements
			List<ClassOrInterfaceType> implementsList = type.getImplements();
			if (implementsList == null) {
				implementsList = new LinkedList<ClassOrInterfaceType>();
				type.setImplements(implementsList);
			}
			
			boolean find = false;
			Iterator<ClassOrInterfaceType> it = implementsList.iterator();
			while(it.hasNext() && !find){
				ClassOrInterfaceType coi = it.next();
				String fullName = tt.getFullName(coi);
				find = fullName.equals("java.io.Serializable");
			}
				
			if(!find){
				
				//import
				Object objectImport = tt.getTypeTable().get("Serializable");
				if(objectImport != null){
					includeImport = false;
					String classImport = objectImport.toString();
					if(!classImport.equals("java.io.Serializable")){
						includeSimpleImplement = false;
					}
				}
				
				String implementToAdd;
				if(includeImport || includeSimpleImplement){
					implementToAdd = "Serializable";
				}
				else {
					implementToAdd = "java.io.Serializable";
				}
				
				implementsList.add(new ClassOrInterfaceType(implementToAdd));
				if(includeImport) {
					List<ImportDeclaration> imports = cuPrivate.getImports();
					if (imports == null) {
						imports = new LinkedList<ImportDeclaration>();
						cuPrivate.setImports(imports);
					}
					ImportDeclaration id = new ImportDeclaration(new NameExpr(
							"java.io.Serializable"), false, false);
					imports.add(id);
				}
			}
		}
	}

}
