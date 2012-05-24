package org.nuxeo.frascati.factory;

import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.event.impl.EventImpl;

public class NuxeoFraSCAtiStartedEvent extends EventImpl {

	public static final String ID = "nuxeoFraSCAtiStartedEvent";
	
	private static final long serialVersionUID = 1L;
	
	public NuxeoFraSCAtiStartedEvent(FraSCAtiServiceItf fraSCAtiService) {
		super(ID, new EventContextImpl(fraSCAtiService));
		if (fraSCAtiService == null) {
			throw new NullPointerException("fraSCAtiService must not be null");
		}
	}
	
	public FraSCAtiServiceItf getFraSCAtiService() {
		Object[] arguments = getContext().getArguments();
		if (arguments.length > 0 && arguments[0] instanceof FraSCAtiServiceItf) {
			return (FraSCAtiServiceItf) getContext().getArguments()[0];
		}
		else {
			return null;
		}
	}

}
