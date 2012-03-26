package org.easysoa.validation;

import java.util.ArrayList;

public class ValidationResult extends ArrayList<ValidatorResult> {
	
	private static final long serialVersionUID = 1L;

	private String serviceName;
	
	public ValidationResult(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public void add(String validatorName, boolean isValidated, String validationLog) {
		this.add(new ValidatorResult(validatorName, isValidated, validationLog));
	}
	
	public boolean isValidationPassed() {
		for (ValidatorResult item : this) {
			if (!item.isValidated()) {
				return false;
			}
		}
		return true;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
}