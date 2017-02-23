package com.bacon.graph;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.faces.context.FacesContext;

import org.openntf.domino.graph2.impl.DFramedTransactionalGraph;
import org.openntf.domino.utils.Strings;

import au.com.bytecode.opencsv.CSVReader;

import com.azlighthouse.util.ServletUtils;
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


	private DFramedTransactionalGraph g() {
		return GraphUtil.getGraphInstance();
	}


	public String getActorsWithKey(String value) {
		String result = "";
		String method = "getActorsWithKey()";
		try {

			this.console(method, "key: " + value);
			ArrayList<String> keys = new ArrayList();
			keys.add("Name");
			ArrayList<String> vals = new ArrayList();
			vals.add(value);
			Iterable<Actor> actorIterable = this.g().getFilteredElementsPartial("com.bacon.model.Actor", keys, vals);
			TreeSet<String> names = new TreeSet<String>();
			int count = 0;
			for (Actor actor : actorIterable) {
				names.add(actor.getName());
				count++;
				if (count > 500) {
					System.out.println("Loaded another 500 actors...");
					count = 0;
				}
			}


			result = StringUtils.join(",", names.iterator());

		} catch (Exception e) {
			this.handleException(e, true, "value: " + value);
		}


		this.console(method, "result");
		System.out.println("\t " + result);
		System.out.println(" ");
		return result;
	}

	public void setActors() {
		Iterable<Actor> actorIterable = this.g().getVertices("Name", getSelectedActor(), Actor.class);
		ArrayList<String> names = new ArrayList<String>();
		int count = 0;
		for (Actor actor : actorIterable) {
			names.add(actor.getName());
			count++;
			if (count > 500) {
				System.out.println("Loaded another 500 actors...");
				count = 0;
			}
		}
		System.out.println("Loaded actors");
		actors = names;
	}

	public Actor getActor() {
		try {
			String actorname = this.getSelectedActor();
			// return (Strings.isBlankString(actorname)) ? null : (Actor)
			// this.g().getVertex(actorname, Actor.class);
			Actor result = (Strings.isBlankString(actorname)) ? null : (Actor) this.g().getVertex(actorname, Actor.class);
			this.console("getActor()", (null == result) ? "" : result.getName());
			return result;

		} catch (Exception e) {
			this.handleException(e, true);
		}

		return null;
	}

	public void setSelectedActor(String selectedActor) {
		this.console("setSelectedActor()", selectedActor);
		this.selectedActor = selectedActor;
	}

	public String getSelectedActor() {
		return selectedActor;
	}

	public String getActorPath() {
		String method = "getActorPath()";
		String result = "";
		try {
			UIViewRootEx2 view = (UIViewRootEx2) ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), "view");
			if (view.isRenderingPhase()) {
				Actor actor = this.getActor();
				if (null != actor) {
					StringBuilder sb = new StringBuilder();
					boolean isActor = true;
					int level = 0;
					ArrayList<String> path = actor.getPathToKevinBacon();
					if (path.isEmpty()) actor.getDistanceToKevinBacon();

					for (String elem : path) {
						if (level > 0) {
							sb.append("<br/>");
							for (int x = 0; x < level; x++) {
								sb.append("&nbsp;&nbsp;");
							}

							sb.append("->");
						}

						sb.append(elem);
						sb.append((isActor) ? " <b>Actor</b>" : " <b>Movie</b>");

						isActor = !isActor;
						level++;
					}

					result = sb.toString();
				}
			}

		} catch (Exception e) {
			this.handleException(e, true);
		}

		this.console(method, result);
		return result;
	}

	public void loadBigData() {
		try {
			GraphUtil.nukeData();
			InputStream actorsLarge = GraphUI.class.getResourceAsStream("actors");
			CSVReader reader = new CSVReader(new InputStreamReader(actorsLarge));
			List<String[]> myEntries = reader.readAll(); // All data
			int count = 0;

			for (String[] docData : myEntries) {
				String performer = docData[0];
				String character = docData[1];
				String movieTitle = docData[2];
				Actor actor = (Actor) this.g().addVertex(performer, Actor.class);
				actor.setName(performer);
				Movie movie = (Movie) this.g().addVertex(movieTitle, Movie.class);
				movie.setTitle(movieTitle);
				StarsIn stars = movie.addActor(actor);
				count++;
				if (count > 1000) {
					this.g().commit();
					count = 0;
				}
			}
			this.g().commit();
		} catch (Throwable t) {
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


	protected void handleException(final Exception e, final boolean includeStackTrace, final Object... args) {
		if (null == e) { return; }
		try {

			final List<String> strings = new ArrayList<String>();
			if (null != args) {
				for (final Object arg : args) {
					if (null != arg) {
						strings.add(StringUtils.getString(arg, ", "));
					}
				}
			}

			ServletUtils.handleException(e, includeStackTrace, strings);

		} catch (final Exception e1) {
			ServletUtils.handleException(e, includeStackTrace, args);
		}
	}
}
