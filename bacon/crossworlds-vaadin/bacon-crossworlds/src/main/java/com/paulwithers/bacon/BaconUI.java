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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
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
		layout.setSizeUndefined();

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

		VerticalLayout mainBody = new VerticalLayout();
		mainBody.setStyleName("mainBody");
		mainBody.setMargin(true);
		mainBody.setSpacing(true);
		HorizontalLayout row = new HorizontalLayout();
		row.setSizeFull();
		row.setSpacing(true);
		VerticalLayout numberCell = new VerticalLayout();
		numberCell.setStyleName("bodyCell numberCell");
		numberCell.setMargin(true);
		numberCell.setSpacing(true);
		Label numberHeading = new Label("Bacon Number");
		numberHeading.setStyleName(ValoTheme.LABEL_H3);
		Label numberDetail = new Label("");
		numberDetail.setStyleName(ValoTheme.LABEL_H1);
		numberDetail.setStyleName(ValoTheme.LABEL_BOLD);
		numberCell.addComponents(numberHeading, numberDetail);
		VerticalLayout traversalCell = new VerticalLayout();
		traversalCell.setStyleName("bodyCell");
		traversalCell.setHeight(100.0f, Unit.PERCENTAGE);
		traversalCell.setMargin(true);
		traversalCell.setSpacing(true);
		Label detailsHeader = new Label("Best Route");
		detailsHeader.setStyleName(ValoTheme.LABEL_H4);
		Label details = new Label("");
		details.setContentMode(ContentMode.HTML);
		details.setStyleName(ValoTheme.LABEL_H4);
		details.setStyleName(ValoTheme.LABEL_COLORED);
		details.setHeightUndefined();
		traversalCell.addComponents(detailsHeader, details);
		row.addComponents(numberCell, traversalCell);
		row.setExpandRatio(numberCell, 1.0f);
		row.setExpandRatio(traversalCell, 2.0f);
		mainBody.addComponent(row);
		mainBody.setVisible(false);

		Button button = new Button("Find Number");
		button.addClickListener(e -> {
			loadDetails((String) name.getValue(), mainBody, numberDetail, details);
		});

		selector.addComponents(name, button);
		selector.setComponentAlignment(button, Alignment.BOTTOM_RIGHT);
		introPanel.addComponents(intro, intro2, mini, selector);
		mainPanel.addComponents(heading, introPanel);
		layout.addComponents(topBar, mainPanel, mainBody);

		setContent(layout);
	}

	public void loadDetails(String name, VerticalLayout mainBody, Label numberDetail, Label details) {
		Actor actor = GraphBootstrapper.getInstance().getActor(name);
		if (null == actor) {
			numberDetail.setValue("");
			details.setValue("");
			mainBody.setVisible(false);
			Notification.show("Actor " + name + "could not be found!");
		} else {
			int value = actor.getDistanceToKevinBacon();
			numberDetail.setValue(Integer.toString(value));
			StringBuilder sb = new StringBuilder();
			if (value > 0) {
				boolean isActor = true;
				int level = 0;
				for (String elem : actor.getPathToKevinBacon()) {
					if (level > 0) {
						sb.append("<br/>");
						for (int x = 0; x < level; x++) {
							sb.append("&nbsp;&nbsp;");
						}
						sb.append("->");
					}
					sb.append(elem);
					if (isActor) {
						sb.append(" (Actor)");
					} else {
						sb.append(" (Movie)");
					}
					isActor = !isActor;
					level++;
				}
			}
			details.setValue(sb.toString());
			mainBody.setVisible(true);
		}
	}

	@WebServlet(urlPatterns = "/*", name = "UIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = BaconUI.class, productionMode = false)
	public static class UIServlet extends VaadinServlet {

	}
}
