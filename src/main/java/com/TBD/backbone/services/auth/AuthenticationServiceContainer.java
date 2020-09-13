package com.TBD.backbone.services.auth;

import com.TBD.core.email.EmailSupport;
import com.TBD.core.services.remoting.AbstractContainer;
import com.TBD.core.services.remoting.ContainerException;

public class AuthenticationServiceContainer extends AbstractContainer
{
	public static void main(String[] args)
	{
		AuthenticationServiceContainer container = new AuthenticationServiceContainer();
		try
		{
			container.performSteps();
		}
		catch (ContainerException e)
		{
			EmailSupport.notify(e, e.getMessage());
			System.exit(-1);
		}
	}

	public AuthenticationServiceContainer()
	{
		super("AuthenticationService");
	}
	
	@Override
	protected Object createServiceImpl() throws ContainerException
	{
		Object service = null;
		try
		{
			service = new AuthenticationServiceImpl();
		}
		catch (Exception e)
		{
			throw new ContainerException(e);
		}
		return service;
	}
}
