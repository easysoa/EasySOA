package org.easysoa.sca.visitors;

public abstract class AbstractBindingVisitorFactoryBase implements BindingVisitorFactory {

	protected Object documentManager;
	
	public AbstractBindingVisitorFactoryBase(Object documentManager){
		this.documentManager = documentManager;
	}
	
	@Override
	public Object getDocumentManager() {
		return this.documentManager;
	}

}
