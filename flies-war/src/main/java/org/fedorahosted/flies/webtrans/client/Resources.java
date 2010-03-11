package org.fedorahosted.flies.webtrans.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {

	// @Source("Style.css")
	// Style style();

	@Source("org/fedorahosted/flies/webtrans/images/flies_logo_small.png")
	ImageResource logo();
	
	@Source("org/fedorahosted/flies/webtrans/images/silk/user.png")
	ImageResource userOnline();

}