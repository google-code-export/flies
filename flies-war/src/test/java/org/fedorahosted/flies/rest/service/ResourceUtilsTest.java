package org.fedorahosted.flies.rest.service;

import java.util.ArrayList;
import java.util.List;

import org.fedorahosted.flies.common.LocaleId;
import org.fedorahosted.flies.model.HDocument;
import org.fedorahosted.flies.rest.dto.resource.TextFlow;
import org.fedorahosted.flies.rest.dto.resource.TextFlow;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class ResourceUtilsTest {

	private static ResourceUtils documentUtils = new ResourceUtils();

	@Test
	public void mergeNoTextFlows() {
		List<TextFlow> from = new ArrayList<TextFlow>();
		HDocument to = new HDocument();
		boolean changed = documentUtils.mergeTextFlows(from, to);

		assertThat(changed, is(false));
	}

	@Test
	public void mergeTextFlowWithOneFromChange() {
		List<TextFlow> from = new ArrayList<TextFlow>();

		TextFlow tf1 = new TextFlow("id", LocaleId.EN, "text1");
		from.add(tf1);

		HDocument to = new HDocument();
		boolean changed = documentUtils.mergeTextFlows(from, to);

		assertThat(changed, is(true));
	}

	private static final String[][] urlPatterns = new String[][] { 
		new String[] { ",my,doc,id", "/my/doc/id" },
		new String[] { ",my,,doc,id", "/my//doc/id" },
		new String[] { "x+y", "x y" },
	}; 
	
	@DataProvider(name = "urlpatterns")
	public String[][] createUrlPatterns() {
		return urlPatterns;
	}

	@Test(dataProvider = "urlpatterns")
	public void decodeDocIds(String encoded, String decoded) {
		assertThat("Decoding "+ encoded, documentUtils.decodeDocId(encoded), is(decoded));
	}

	@Test(dataProvider = "urlpatterns")
	public void encodeDocIds(String encoded, String decoded) {
		assertThat("Encoding "+ decoded, documentUtils.encodeDocId(decoded), is(encoded));
	}

}
