package com.bacon.graph;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.faces.context.FacesContext;

import org.openntf.domino.utils.Strings;

import au.com.bytecode.opencsv.CSVReader;

import com.azlighthouse.util.StringUtils;
import com.bacon.model.Actor;
import com.bacon.model.Movie;
import com.bacon.model.Movie.StarsIn;
import com.ibm.xsp.component.UIViewRootEx2;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.paulwithers.util.GraphUtil;

public class GraphUI implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private String				selectedActor;
	public ArrayList<String>	actors;
	private Actor				actor;


	@SuppressWarnings("unchecked")
	public String getActorsWithKey(final String value) {
		System.out.println("Getting actors");
		final ArrayList<String> keys = new ArrayList();
		keys.add("Name");
		final ArrayList<String> vals = new ArrayList();
		vals.add(value);
		final Iterable<Actor> actorIterable = GraphUtil.getGraphInstance().getFilteredElementsPartial("com.bacon.model.Actor", keys, vals);
		final TreeSet<String> names = new TreeSet<String>();
		int count = 0;
		for (final Actor actor : actorIterable) {
			names.add(actor.getName());
			count++;
			if (count > 500) {
				System.out.println("Loaded another 500 actors...");
				count = 0;
			}
		}

		System.out.println("Loaded actors");
		return StringUtils.join(",", names.iterator());
	}


	public void setActors() {
		final Iterable<Actor> actorIterable = GraphUtil.getGraphInstance().getVertices("Name", this.getSelectedActor(), Actor.class);
		final ArrayList<String> names = new ArrayList<String>();
		int count = 0;
		for (final Actor actor : actorIterable) {
			names.add(actor.getName());
			count++;
			if (count > 500) {
				System.out.println("Loaded another 500 actors...");
				count = 0;
			}
		}
		System.out.println("Loaded actors");
		this.actors = names;
	}


	public Actor getActor() {
		// if (StringUtils.isEmpty(getSelectedActor())) {
		if (Strings.isBlankString(this.getSelectedActor())) {
			return null;
		} else {
			return (Actor) GraphUtil.getGraphInstance().getVertex(this.getSelectedActor(), Actor.class);
		}
	}


	public void setSelectedActor(final String selectedActor) {
		this.console("setSelectedActor()", selectedActor);
		this.selectedActor = selectedActor;
	}


	public String getSelectedActor() {
		return this.selectedActor;
	}


	public String getActorPath() {
		final UIViewRootEx2 view = (UIViewRootEx2) ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), "view");
		if (!view.isRenderingPhase()) {
			return "";
		} else {
			if (null == this.getActor()) { return ""; }
			final StringBuilder sb = new StringBuilder();
			boolean isActor = true;
			int level = 0;
			final ArrayList<String> path = this.getActor().getPathToKevinBacon();
			if (path.isEmpty()) {
				this.getActor().getDistanceToKevinBacon();
			}
			for (final String elem : path) {
				if (level > 0) {
					sb.append("<br/>");
					for (int x = 0; x < level; x++) {
						sb.append("&nbsp;&nbsp;");
					}
					sb.append("->");
				}
				sb.append(elem);
				if (isActor) {
					sb.append(" <b>Actor</b>");
				} else {
					sb.append(" <b>Movie</b>");
				}
				isActor = !isActor;
				level++;
			}
			return sb.toString();
		}
	}


	public void loadBigData() {
		try {
			GraphUtil.nukeData();
			final InputStream actorsLarge = GraphUI.class.getResourceAsStream("actors_larger");
			final CSVReader reader = new CSVReader(new InputStreamReader(actorsLarge));
			final List<String[]> myEntries = reader.readAll(); // All data
			int count = 0;

			long longCount = 0;
			System.out.println(" ");
			System.out.println(this.getClass().getName().concat(".loadBigData(): started"));

			for (final String[] docData : myEntries) {
				final String performer = docData[1];
				final String character = docData[2];
				final String movieTitle = docData[3];
				final String year = docData[4];
				final Actor actor = (Actor) GraphUtil.getGraphInstance().addVertex(performer, Actor.class);
				actor.setName(performer);
				final Movie movie = (Movie) GraphUtil.getGraphInstance().addVertex(movieTitle, Movie.class);
				movie.setTitle(movieTitle);
				movie.setYear(year);
				final StarsIn stars = movie.addActor(actor);
				stars.setCharacter(character);
				count++;
				longCount++;
				if (count > 1000) {
					GraphUtil.getGraphInstance().commit();
					count = 0;
					System.out.println("Loaded " + longCount + " entries.");
				}
			}
			GraphUtil.getGraphInstance().commit();

			System.out.println(this.getClass().getName().concat(".loadBigData(): finished"));
			System.out.println("Loaded " + longCount + " TOTAL entries.");
			System.out.println(" ");
		} catch (final Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Writes output to the console. Includes the fully qualified name of
	 * object.
	 * 
	 * Calls CzarDebug to write output to the console.
	 * 
	 * @param method
	 *            Method to append to the object name.
	 * 
	 * @param consoleText
	 *            Text to write to the console.
	 */
	protected void console(final String method, final String consoleText) {
		final StringBuilder sb = new StringBuilder(this.getClass().getName());
		if (!Strings.isBlankString(method)) {
			sb.append(".");
			sb.append(method);
		}

		sb.append("\t ");
		sb.append(consoleText);
		System.out.println(sb.toString());
	}

}
