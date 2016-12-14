package com.bacon.graph;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.bacon.model.Actor;
import com.bacon.model.Movie;
import com.bacon.model.Movie.StarsIn;
import com.ibm.xsp.component.UIViewRootEx2;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.paulwithers.util.GraphUtil;

public class GraphUI implements Serializable {
	private static final long serialVersionUID = 1L;
	private String selectedActor;
	public ArrayList<String> actors;
	private Actor actor;

	@SuppressWarnings("unchecked")
	public String getActorsWithKey(String value) {
		System.out.println("Getting actors");
		ArrayList<String> keys = new ArrayList();
		keys.add("Name");
		ArrayList<String> vals = new ArrayList();
		vals.add(value);
		Iterable<Actor> actorIterable = GraphUtil.getGraphInstance().getFilteredElementsPartial(
				"com.bacon.model.Actor", keys, vals);
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

		System.out.println("Loaded actors");
		return StringUtils.join(names.iterator(), ",");
	}

	public void setActors() {
		Iterable<Actor> actorIterable = GraphUtil.getGraphInstance().getVertices("Name", getSelectedActor(),
				Actor.class);
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
		if (StringUtils.isEmpty(getSelectedActor())) {
			return null;
		} else {
			return (Actor) GraphUtil.getGraphInstance().getVertex(getSelectedActor(), Actor.class);
		}
	}

	public void setSelectedActor(String selectedActor) {
		this.selectedActor = selectedActor;
	}

	public String getSelectedActor() {
		return selectedActor;
	}

	public String getActorPath() {
		UIViewRootEx2 view = (UIViewRootEx2) ExtLibUtil.resolveVariable("view");
		if (!view.isRenderingPhase()) {
			return "";
		} else {
			if (null == getActor()) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			boolean isActor = true;
			int level = 0;
			ArrayList<String> path = getActor().getPathToKevinBacon();
			if (path.isEmpty()) {
				getActor().getDistanceToKevinBacon();
			}
			for (String elem : path) {
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
			InputStream actorsLarge = GraphUI.class.getResourceAsStream("actors_larger");
			CSVReader reader = new CSVReader(new InputStreamReader(actorsLarge));
			List<String[]> myEntries = reader.readAll(); // All data
			int count = 0;

			for (String[] docData : myEntries) {
				String performer = docData[1];
				String character = docData[2];
				String movieTitle = docData[3];
				String year = docData[4];
				Actor actor = (Actor) GraphUtil.getGraphInstance().addVertex(performer, Actor.class);
				actor.setName(performer);
				Movie movie = (Movie) GraphUtil.getGraphInstance().addVertex(movieTitle, Movie.class);
				movie.setTitle(movieTitle);
				movie.setYear(year);
				StarsIn stars = movie.addActor(actor);
				stars.setCharacter(character);
				count++;
				if (count > 1000) {
					GraphUtil.getGraphInstance().commit();
					count = 0;
				}
			}
			GraphUtil.getGraphInstance().commit();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
