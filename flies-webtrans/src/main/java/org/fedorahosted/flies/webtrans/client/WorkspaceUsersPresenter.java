package org.fedorahosted.flies.webtrans.client;

import java.util.Arrays;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import org.fedorahosted.flies.gwt.model.Person;
import org.fedorahosted.flies.gwt.model.PersonId;
import org.fedorahosted.flies.webtrans.client.ui.HasFilter;
import org.fedorahosted.flies.webtrans.client.ui.HasTreeNodes;
import org.fedorahosted.flies.webtrans.client.ui.TreeNode;

import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.inject.Inject;

public class WorkspaceUsersPresenter extends WidgetPresenter<WorkspaceUsersPresenter.Display>{

	public static final Place PLACE = new Place("WorkspaceUsersPresenter");
	
	public interface Display extends WidgetDisplay{
		HasTreeNodes<Person> getTree();
		HasFilter<Person> getFilter();
		HasMouseOverHandlers getTreeMouseOver();
	}
	
	@Inject
	public WorkspaceUsersPresenter(final Display display, final EventBus eventBus) {
		super(display, eventBus);
	}
	
	
	@Override
	public Place getPlace() {
		return PLACE;
	}

	@Override
	protected void onBind() {
		Person [] translators = new Person[]{
			new Person( new PersonId("bob"), "Bob"),
			new Person( new PersonId("jane"), "Jane"),
			new Person( new PersonId("bill"), "Bill")
			};	
		getDisplay().getFilter().setList(Arrays.asList(translators));
//		getDisplay().getTreeMouseOver().addMouseOverHandler(new MouseOverHandler() {
//			
//			@Override
//			public void onMouseOver(MouseOverEvent event) {
//				// TODO Auto-generated method stub
//				TreeNode<Person> source = (TreeNode<Person>) event.getSource();	
//			}
//		});
	}

	@Override
	protected void onPlaceRequest(PlaceRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onUnbind() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void revealDisplay() {
		// TODO Auto-generated method stub
		
	}
	
}
