/**
 * EasySOA Samples - Smart Travel
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
 * Contact : easysoa-dev@groups.google.com
 */

package fr.inria.galaxy.j1.scenario1;

/**
 * A simple client used to validate the architecture of this demo.
 * 
 * @author Christophe Demarey.
 */
public class SimpleClient implements Runnable {
  /** The reference to the trip service */
  private Trip tripService;

  public void run() {
    tripService.process("ART", "I would like a bier!", 0.5);
  }

  /** Set the reference of the service. */
  public void setTrip(Trip service) {
    this.tripService = service;
  }
}
