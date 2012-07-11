package org.easysoa.registry.query.eclipse.browser;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class EasySOAServicePicker extends Composite {

	private Browser browser;

	public EasySOAServicePicker(Composite parent, int style) {
		super(parent, style);
		browser = new Browser(this, SWT.NONE);
		browser.setText("<html><head></head><body><button type=\"button\" onclick=\"myfunction(['YAYE',1,null,'!'])\">Click Me!</button></body></html>"); //$NON-NLS-1$

		new BrowserFunction(browser, "myfunction") { //$NON-NLS-1$
			@Override
			public Object function(Object[] arguments) {
				EasySOAServicePicker.this.getChildren()[0].dispose();
				Text text = new Text(EasySOAServicePicker.this, SWT.NONE);
				text.setText(Arrays.toString((Object[])arguments[0]));
				text.pack();
				EasySOAServicePicker.this.layout(true, true);
				return null;
			};
		};
		
		browser.pack();
	}

}
