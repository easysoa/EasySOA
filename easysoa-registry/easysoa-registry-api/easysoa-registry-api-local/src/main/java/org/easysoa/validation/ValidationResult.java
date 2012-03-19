package org.easysoa.validation;

import java.util.ArrayList;

public class ValidationResult extends ArrayList<ValidationResultItem> {
	
	private static final long serialVersionUID = 1L;

	public void add(String validatorName, boolean isValidated, String validationLog) {
		this.add(new ValidationResultItem(validatorName, isValidated, validationLog));
	}
	
	public boolean isValidationPassed() {
		for (ValidationResultItem item : this) {
			if (!item.isValidated()) {
				return false;
			}
		}
		return true;
	}
	
}