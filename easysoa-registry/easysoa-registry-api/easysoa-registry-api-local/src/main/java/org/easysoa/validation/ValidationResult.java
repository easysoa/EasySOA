/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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