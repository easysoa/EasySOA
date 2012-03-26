package org.easysoa.validation;

public class ValidatorResult {

	String validatorName;
	
	boolean isValidated;
	
	String validationLog;

	public ValidatorResult(String validatorName, boolean isValidated, String validationLog) {
		this.validatorName = validatorName;
		this.isValidated = isValidated;
		this.validationLog = validationLog;
	}

	public String getValidatorName() {
		return validatorName;
	}

	public boolean isValidated() {
		return isValidated;
	}

	public String getValidationLog() {
		return validationLog;
	}
	
}
