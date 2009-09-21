package org.fedorahosted.flies.rest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.resteasy.ResteasyBootstrap;

@Name("org.jboss.seam.resteasy.bootstrap")
@Scope(ScopeType.APPLICATION)
@Startup
@AutoCreate
@Install(classDependencies = "org.jboss.resteasy.spi.ResteasyProviderFactory", precedence=Install.DEPLOYMENT)
public class FliesResteasyBootstrap extends ResteasyBootstrap {

	@Override
	protected void initDispatcher() {
		super.initDispatcher();
		getDispatcher().getProviderFactory().getServerPreProcessInterceptorRegistry().register(FliesRestSecurityInterceptor.class);
	}
}