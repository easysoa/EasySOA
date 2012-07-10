/**
 * EasySOA Proxy
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

package com.openwide.easysoa.run;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;

/**
 * Run is a set of recorded messages
 * @author jguillemotte
 *
 */
public class Run {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(Run.class.getName());

	//
	public enum RunStatus {
		RUNNING, STOPPED, SAVED
	}
	
	//
	private RunStatus status;
	
	/**
	 * The messages collection
	 */
	private ArrayDeque<ExchangeRecord> exchangeRecordList;
	
	/**
	 * Start run date
	 */
	private Date startDate;
	
	/**
	 * Stop run date
	 */
	private Date stopDate;
	
	/**
	 * Run name
	 */
	private String name;
	
	// Contains a collection of messages & informations like startDate, stopDate...
	
	/**
	 * Initialize a new <code>Run</code> instance 
	 */
	// TODO :  check that the run name does not contains exotic characters because the name will be used to create the run folder for persistence. 
	public Run(String name) throws IllegalArgumentException {
		if(name == null){
			throw new IllegalArgumentException("name parameter must not be null");
		}
		this.name = name;
		exchangeRecordList = new ArrayDeque<ExchangeRecord>();
		startDate = null;
		stopDate = null;
	}

	/**
	 * Add an exchange record
	 * @param exchangeRecord The exchangeRecord to add
	 * @throws IllegalArgumentException If the exchange record is null
	 */
	public void addExchange(ExchangeRecord exchangeRecord) throws IllegalArgumentException {
		if(exchangeRecord == null){
			throw new IllegalArgumentException("The parameter message must not be null !");
		}
		logger.debug("Adding exchange record in list : " + exchangeRecord);
		this.exchangeRecordList.add(exchangeRecord);
	}
	
	/**
	 * Returns the exchange record list
	 * @return The exchange record list in a <code>Deque</code>
	 */
	public Deque<ExchangeRecord> getExchangeRecordList(){
		return this.exchangeRecordList;
	}
	
	/**
	 * The date the run was started
	 * @param startDate
	 */
	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}

	/**
	 * The date the run was stopped
	 * @param stopDate
	 */
	public void setStopDate(Date stopDate){
		this.stopDate = stopDate;
	}

	/**
	 * Returns the run start date
	 * @return The run start <code>Date</code> or null if the start date has not been set before
	 */
	public Date getStartDate(){
		return this.startDate;
	}

	/**
	 * Get the run stop date
	 * @return The run stop <code>Date</code> or null if the stop date has not been set before.
	 */
	public Date getStopDate(){
		return this.stopDate;
	}

	/**
	 * Returns the run name
	 * @return A<code>String</code> for the run name
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * The run name
	 * @param name
	 */
	public void setName(String name) throws IllegalArgumentException {
		if(name == null){
			throw new IllegalArgumentException();
		}
		this.name = name;
	}

	/**
	 * Returns the status
	 * @return The run status
	 */
	public RunStatus getStatus() {
		return status;
	}

	/**
	 * Set the run status
	 * @param status Run status
	 */
	public void setStatus(RunStatus status) {
		this.status = status;
	}
	
}
