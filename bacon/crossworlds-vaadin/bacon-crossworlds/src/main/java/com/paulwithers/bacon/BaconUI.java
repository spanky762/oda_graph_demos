package com.paulwithers.bacon;

import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paulwithers.bacon.model.Actor;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("bacontheme")
public class BaconUI extends UI {
	static final Logger LOG = LoggerFactory.getLogger(BaconUI.class);

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		Responsive.makeResponsive(this);
		setLocale(vaadinRequest.getLocale());

		getPage().setTitle("Six Degrees of Kevin Bacon");
		final CssLayout layout = new CssLayout();
		layout.setSizeFull();

		final VerticalLayout topBar = new VerticalLayout();
		topBar.setSpacing(true);
		topBar.setStyleName(ValoTheme.MENU_TITLE);

		final VerticalLayout mainPanel = new VerticalLayout();
		mainPanel.setMargin(true);
		mainPanel.setStyleName("introPanel");
		Label heading = new Label("Six Degrees of Kevin Bacon");
		heading.setStyleName(ValoTheme.LABEL_H2);

		VerticalLayout introPanel = new VerticalLayout();
		introPanel.setWidth("70%");
		Label intro = new Label("Bacon is the subject of the trivia game titled Six Degrees of Kevin Bacon, "
				+ "based on the idea that, due to his prolific screen career covering a diverse range of genres, "
				+ "any Hollywood actor can be linked to another in a handful of steps based on their association with Bacon.");
		Label intro2 = new Label(
				"The Bacon number of an actor or actress is the number of degrees of separation he or she has from Bacon, "
						+ "as defined by the game. This is an application of the Erdős number concept to the Hollywood movie industry. "
						+ "The higher the Bacon number, the farther away from Kevin Bacon the actor is.");
		Label mini = new Label("Source: Wikipedia");
		mini.setStyleName("mini");

		HorizontalLayout selector = new HorizontalLayout();
		selector.setSizeFull();
		final ComboBox name = new ComboBox("Choose an actor:");
		name.setInputPrompt("No actor selected");
		name.setWidth(75.0f, Unit.PERCENTAGE);
		name.setFilteringMode(FilteringMode.CONTAINS);
		name.setImmediate(true);
		name.setNullSelectionAllowed(false);
		name.setNewItemsAllowed(false);
		Iterable<Actor> actors = GraphBootstrapper.getInstance().getActors();
		for (Actor actor : actors) {
			name.addItem(actor.getName());
		}

		Button button = new Button("Find Number");
		button.addClickListener(e -> {
			layout.addComponent(new Label("Thanks " + name.getValue() + ", it works!"));
		});

		selector.addComponents(name, button);
		selector.setComponentAlignment(button, Alignment.BOTTOM_RIGHT);
		introPanel.addComponents(intro, intro2, mini, selector);
		mainPanel.addComponents(heading, introPanel);
		layout.addComponents(topBar, mainPanel);

		setContent(layout);
	}

	@WebServlet(urlPatterns = "/*", name = "UIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = BaconUI.class, productionMode = false)
	public static class UIServlet extends VaadinServlet {

	}
}
