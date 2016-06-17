package com.auto.solution.TestInterpretor;

import com.auto.solution.Common.ResourceManager;

public class CompilerFactory {

	private String strategyCompiler = "";
	private ResourceManager rManager;
public CompilerFactory(String compilerStrategy,ResourceManager rm){
	this.strategyCompiler = compilerStrategy;
	this.rManager = rm;
}

public ICompiler getCompiler(){
	ICompiler compiler = null;
	if(strategyCompiler.equalsIgnoreCase("simple")){
		compiler = new Compiler(this.rManager);
	}
	return compiler;
}
}
