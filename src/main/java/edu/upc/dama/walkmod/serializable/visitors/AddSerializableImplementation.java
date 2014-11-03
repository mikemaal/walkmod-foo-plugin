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
import org.walkmod.javalang.visitors.VisitorSupport;
import org.walkmod.walkers.VisitorContext;

public class AddSerializableImplementation extends
		VisitorSupport<VisitorContext> {

	private boolean includeSerializable = false;

	@Override
	public void visit(CompilationUnit cu, VisitorContext ctx) {

		List<ImportDeclaration> imports = cu.getImports();
		ImportDeclaration id = new ImportDeclaration(new NameExpr(
				"java.io.Serializable"), false, false);
		if (imports == null) {
			imports = new LinkedList<ImportDeclaration>();
			cu.setImports(imports);
		} else {
			Iterator<ImportDeclaration> it = imports.iterator();
			while (it.hasNext() && !includeSerializable) {
				ImportDeclaration importD = it.next();
				includeSerializable = importD.getName().getName()
						.equals("Serializable");
			}
		}
		if (!includeSerializable) {
			imports.add(id);
		}
		super.visit(cu, ctx);
	}

	public void visit(ClassOrInterfaceDeclaration type, VisitorContext ctx) {
		if (!type.isInterface()) {
			List<ClassOrInterfaceType> implementsList = type.getImplements();
			if (implementsList == null) {
				implementsList = new LinkedList<ClassOrInterfaceType>();
				type.setImplements(implementsList);
			}
			if(!includeSerializable){
				implementsList.add(new ClassOrInterfaceType("Serializable"));
			}
			else{
				implementsList.add(new ClassOrInterfaceType("java.io.Serializable"));
			}
		}
	}

}
