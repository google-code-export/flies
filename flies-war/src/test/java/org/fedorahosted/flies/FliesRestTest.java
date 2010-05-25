package org.fedorahosted.flies;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ext.ExceptionMapper;

import org.fedorahosted.flies.rest.AuthorizationExceptionMapper;
import org.fedorahosted.flies.rest.HibernateExceptionMapper;
import org.fedorahosted.flies.rest.InvalidStateExceptionMapper;
import org.fedorahosted.flies.rest.NoSuchEntityExceptionMapper;
import org.fedorahosted.flies.rest.NotLoggedInExceptionMapper;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.core.executors.InMemoryClientExecutor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.spi.ResourceFactory;
import org.testng.annotations.BeforeMethod;

public abstract class FliesRestTest extends FliesDbunitJpaTest {
	
	protected static final URI MOCK_BASE_URI = URI.create("http://mockhost");
	private ClientRequestFactory clientRequestFactory;
	protected Set<Class<? extends ExceptionMapper<? extends Throwable>>> exceptionMappers = new HashSet<Class<? extends ExceptionMapper<? extends Throwable>>>();
	protected Set<Object> resources = new HashSet<Object>();
	
	@BeforeMethod
	public void prepareRestEasyClientFramework() {
		
		Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
		prepareResources();
		prepareExceptionMappers();
		
		for(Object obj : resources ) {
			ResourceFactory factory = new MockResourceFactory(obj); 
			dispatcher.getRegistry().addResourceFactory(factory);
		}
		
		// register Exception Mappers
		for(Class<? extends ExceptionMapper<? extends Throwable>> mapper : exceptionMappers ) {
			dispatcher.getProviderFactory().addExceptionMapper(mapper);
		}
		InMemoryClientExecutor executor = new InMemoryClientExecutor(dispatcher); 
		executor.setBaseUri(MOCK_BASE_URI);
		clientRequestFactory = 
			new ClientRequestFactory(
					executor, MOCK_BASE_URI);
		
	}

	protected abstract void prepareResources();

	protected void prepareExceptionMappers(){
		exceptionMappers.add(AuthorizationExceptionMapper.class);
		exceptionMappers.add(HibernateExceptionMapper.class);
		exceptionMappers.add(InvalidStateExceptionMapper.class);
		exceptionMappers.add(NoSuchEntityExceptionMapper.class);
		exceptionMappers.add(NotLoggedInExceptionMapper.class);
	}
	
	protected ClientRequestFactory getClientRequestFactory() {
		return clientRequestFactory;
	}
	
	protected URI createBaseURI(String resourcePath) {
		return MOCK_BASE_URI.resolve(resourcePath);		
	}
}
