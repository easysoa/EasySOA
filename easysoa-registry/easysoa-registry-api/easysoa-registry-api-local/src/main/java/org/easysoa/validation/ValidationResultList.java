package org.easysoa.validation;

import java.util.ArrayList;

public class ValidationResultList extends ArrayList<ValidationResult> {
	
	private static final long serialVersionUID = 1L;

	public boolean isEveryValidationPassed() {
		for (ValidationResult result : this) {
			if (!result.isValidationPassed()) {
				return false;
			}
		}
		return true;
	}
	
}