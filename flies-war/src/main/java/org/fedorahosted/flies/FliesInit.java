package org.fedorahosted.flies;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Proxy;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.LinkRef;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.fedorahosted.flies.rest.service.FliesRestSecurityInterceptor;
import org.fedorahosted.flies.util.DBUnitImporter;
import org.hibernate.jmx.StatisticsService;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;
import org.jboss.seam.resteasy.SeamResteasyProviderFactory;

/**
 * Doesn't do much useful stuff except printing a log message and firing the
 * "Flies.startup" event.
 * 
 * @author Christian Bauer
 */
@Name("fliesInit")
@Scope(ScopeType.APPLICATION)
public class FliesInit {

	public static final String EVENT_Flies_Startup = "Flies.startup";
	
	@Logger
	static Log log;

	private String adminContact;
	private boolean debug;
	private boolean hibernateStatistics = false;
	private int authenticatedSessionTimeoutMinutes = 0;
	private String version;
	
	private String serverPath;

	@In(required = false)
	DBUnitImporter dbunitImporter;

	private ObjectName hibernateMBeanName;

	@Observer("org.jboss.seam.postInitialization")
	public void initFlies() throws Exception {
		log.info(">>>>>>>>>>>> Starting Flies...");

		if (dbunitImporter != null) {
			log.info("Importing development test data");
			dbunitImporter.importDatasets();
		}

		if (hibernateStatistics) {
			log.info("registering Hibernate statistics MBean");
			try{
				hibernateMBeanName = new ObjectName(
						"Hibernate:type=statistics,application=flies");
				StatisticsService mBean = new StatisticsService();
				mBean.setSessionFactoryJNDIName("SessionFactories/fliesSF");
				ManagementFactory.getPlatformMBeanServer().registerMBean(mBean,
						hibernateMBeanName);
			}
			catch(InstanceAlreadyExistsException e){
				log.info("Hibernate statistics MBean is already started");
			}
			catch(Exception e){
				log.error("Hibernate statistics MBean failed to start", e);
			}
		}

		Events.instance().raiseEvent(EVENT_Flies_Startup);
	
		log.info("Started Flies...");

		// System.out.println(listJNDITree("java:"));
	}

	@Destroy
	public void shutdown() throws Exception {
		log.info("<<<<<<<<<<<<< Stopping Flies...");

		if (hibernateStatistics) {
			log.info("unregistering Hibernate statistics MBean");
			try{
			ManagementFactory.getPlatformMBeanServer().unregisterMBean(
					hibernateMBeanName);
			}
			catch(Exception e){
				log.error("Failed to unregister Hibernate statistics MBean", e);
			}
		}

		log.info("Stopped Flies...");
	}

	public String getAdminContact() {
		return adminContact;
	}

	public void setAdminContact(String adminContact) {
		this.adminContact = adminContact;
	}

	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getServerPath() {
		return serverPath;
	}
	
	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}
	
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isHibernateStatistics() {
		return hibernateStatistics;
	}

	public void setHibernateStatistics(boolean hibernateStatistics) {
		this.hibernateStatistics = hibernateStatistics;
	}

	public int getAuthenticatedSessionTimeoutMinutes() {
		return authenticatedSessionTimeoutMinutes;
	}

	public void setAuthenticatedSessionTimeoutMinutes(
			int authenticatedSessionTimeoutMinutes) {
		this.authenticatedSessionTimeoutMinutes = authenticatedSessionTimeoutMinutes;
	}

	/** Utility to debug JBoss JNDI problems */
	public static String listJNDITree(String namespace) {
		StringBuffer buffer = new StringBuffer(4096);
		try {
			Properties props = new Properties();
			Context context = new InitialContext(props); // From jndi.properties
			if (namespace != null)
				context = (Context) context.lookup(namespace);
			buffer.append("Namespace: " + namespace + "\n");
			buffer.append("#####################################\n");
			list(context, " ", buffer, true);
			buffer.append("#####################################\n");
		} catch (NamingException e) {
			buffer.append("Failed to get InitialContext, " + e.toString(true));
		}
		return buffer.toString();
	}

	private static void list(Context ctx, String indent, StringBuffer buffer,
			boolean verbose) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			NamingEnumeration ne = ctx.list("");
			while (ne.hasMore()) {
				NameClassPair pair = (NameClassPair) ne.next();

				String name = pair.getName();
				String className = pair.getClassName();
				boolean recursive = false;
				boolean isLinkRef = false;
				boolean isProxy = false;
				Class c = null;
				try {
					c = loader.loadClass(className);

					if (Context.class.isAssignableFrom(c))
						recursive = true;
					if (LinkRef.class.isAssignableFrom(c))
						isLinkRef = true;

					isProxy = Proxy.isProxyClass(c);
				} catch (ClassNotFoundException cnfe) {
					// If this is a $Proxy* class its a proxy
					if (className.startsWith("$Proxy")) {
						isProxy = true;
						// We have to get the class from the binding
						try {
							Object p = ctx.lookup(name);
							c = p.getClass();
						} catch (NamingException e) {
							Throwable t = e.getRootCause();
							if (t instanceof ClassNotFoundException) {
								// Get the class name from the exception msg
								String msg = t.getMessage();
								if (msg != null) {
									// Reset the class name to the CNFE class
									className = msg;
								}
							}
						}
					}
				}

				buffer.append(indent + " +- " + name);

				// Display reference targets
				if (isLinkRef) {
					// Get the
					try {
						Object obj = ctx.lookupLink(name);

						LinkRef link = (LinkRef) obj;
						buffer.append("[link -> ");
						buffer.append(link.getLinkName());
						buffer.append(']');
					} catch (Throwable t) {
						buffer.append("invalid]");
					}
				}

				// Display proxy interfaces
				if (isProxy) {
					buffer.append(" (proxy: " + pair.getClassName());
					if (c != null) {
						Class[] ifaces = c.getInterfaces();
						buffer.append(" implements ");
						for (int i = 0; i < ifaces.length; i++) {
							buffer.append(ifaces[i]);
							buffer.append(',');
						}
						buffer.setCharAt(buffer.length() - 1, ')');
					} else {
						buffer.append(" implements " + className + ")");
					}
				} else if (verbose) {
					buffer.append(" (class: " + pair.getClassName() + ")");
				}

				buffer.append('\n');
				if (recursive) {
					try {
						Object value = ctx.lookup(name);
						if (value instanceof Context) {
							Context subctx = (Context) value;
							list(subctx, indent + " |  ", buffer, verbose);
						} else {
							buffer.append(indent + " |   NonContext: " + value);
							buffer.append('\n');
						}
					} catch (Throwable t) {
						buffer.append("Failed to lookup: " + name + ", errmsg="
								+ t.getMessage());
						buffer.append('\n');
					}
				}
			}
			ne.close();
		} catch (NamingException ne) {
			buffer.append("error while listing context " + ctx.toString()
					+ ": " + ne.toString(true));
		}
	}

}
