package org.easysoa.proxy.core.api.event;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author fntangke
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitoringConfiguration {

	@XmlElement(name = "configurations")
	private List<Configuration> configurations;

	public MonitoringConfiguration() {
		this.configurations = new ArrayList<Configuration>();
	}

	/**
	 * Set the the configuration value to the appropriate proxy configuration
	 * 
	 * @param configuration
	 * @return true if ok
	 */
	public boolean setConfiguration(Configuration configuration) {
		for (Configuration conf : this.configurations) {
			String a = conf.getProxy();
			String b = configuration.getProxy();
			if ((a != null) && (b != null)) {
				if (a.equals(b)) {
					synchronized (this) {
						conf.setSubscriptions(configuration.getSubscriptions());
					}
					return true;
				}
			}
			if (((a == null) || (a == "")) && ((b == "") || (b == null))) {
				synchronized (this) {
					conf = configuration;
				}
				return true;
			}
		}
		synchronized (this) {
			this.configurations.add(configuration);
			return true;
		}
	}

	public List<Configuration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<Configuration> configurations) {
		this.configurations = configurations;
	}
}
