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
	
	@Source("org/fedorahosted/flies/webtrans/images/x.png")
	ImageResource xButton();
	
	@Source("org/fedorahosted/flies/webtrans/images/silk/user.png")
	ImageResource userOnline();

	@Source("org/fedorahosted/flies/webtrans/images/silk/page_white_text.png")
	ImageResource documentImage();

	@Source("org/fedorahosted/flies/webtrans/images/silk/folder.png")
	ImageResource folderImage();
	
	@Source("org/fedorahosted/flies/webtrans/images/next_entry.png")
	ImageResource nextEntry();

	@Source("org/fedorahosted/flies/webtrans/images/prev_entry.png")
	ImageResource prevEntry();

	@Source("org/fedorahosted/flies/webtrans/images/next_fuzzy.png")
	ImageResource nextFuzzy();
	
	@Source("org/fedorahosted/flies/webtrans/images/prev_fuzzy.png")
	ImageResource prevFuzzy();

	@Source("org/fedorahosted/flies/webtrans/images/next_untranslated.png")
	ImageResource nextUntranslated();
	
	@Source("org/fedorahosted/flies/webtrans/images/prev_untranslated.png")
	ImageResource prevUntranslated();

	@Source("org/fedorahosted/flies/webtrans/images/next_approved.png")
	ImageResource nextApproved();

	@Source("org/fedorahosted/flies/webtrans/images/prev_approved.png")
	ImageResource prevApproved();

}