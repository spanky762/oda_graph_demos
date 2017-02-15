package com.bacon.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openntf.domino.graph2.DVertex;
import org.openntf.domino.graph2.annotations.AdjacencyUnique;
import org.openntf.domino.graph2.annotations.IncidenceUnique;
import org.openntf.domino.graph2.annotations.TypedProperty;
import org.openntf.domino.utils.Strings;

import com.azlighthouse.util.ServletUtils;
import com.azlighthouse.util.StringUtils;
import com.bacon.model.Movie.StarsIn;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerClass;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeField;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@JavaHandlerClass(com.bacon.model.Actor.ActorImpl.class)
@TypeField("form")
@TypeValue("Actor")
public interface Actor extends VertexFrame {
	@TypedProperty("Name")
	public String getName();

	@TypedProperty("Name")
	public void setName(String name);

	@AdjacencyUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public Iterable<Movie> getMovies();

	@AdjacencyUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public StarsIn addMovie(Movie movie);

	@AdjacencyUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public void removeMovie(Movie movie);

	@IncidenceUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public Iterable<StarsIn> getStarsIns();

	@IncidenceUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public int countStarsIns();

	@IncidenceUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public void removeStarsIn(StarsIn starsIn);

	@JavaHandler
	public Iterable<Actor> getCostars();

	@JavaHandler
	@TypedProperty("CostarCount")
	public int getCostarCount();

	@JavaHandler
	public int getDistanceTo(String actorName, int targetDepth, int curDepth);

	@JavaHandler
	public int getDistanceTo(String actorName, int maxDepth);

	@JavaHandler
	@TypedProperty("DistanceToKevinBacon")
	public int getDistanceToKevinBacon();

	@JavaHandler
	@TypedProperty("PathToKevinBacon")
	public ArrayList<String> getPathToKevinBacon();

	public static abstract class ActorImpl implements Actor, JavaHandlerContext<Vertex> {
		ArrayList<String>	path_	= new ArrayList<String>();

		private DVertex dv() {
			return (DVertex) this.asVertex();
		}


		@Override
		public String toString() {
			return "Actor: ".concat(this.getName());
		}

		@SuppressWarnings("unchecked")
		public List<Actor> getCostars() {

			String method = "getCostars()";
			List<Actor> result = (List) this.dv().getFrameImplObject("costars");
			if ((result == null) || result.isEmpty()) {
				result = new ArrayList<Actor>();
				this.console(method, "Searching for Costars");
				for (Movie movie : this.getMovies()) {
					System.out.println("\t Movie: " + movie.getTitle());
					for (Actor actor : movie.getActors()) {
						if (!this.getName().equals(actor.getName())) {
							result.add(actor);
						}
					}
				}

				result.remove(this);
				this.dv().setFrameImplObject("costars", result);
			}

			this.console(method, "" + result);
			return result;
		}

		@Override
		public int getCostarCount() {
			return getCostars().size();
		}

		@Override
		public int getDistanceToKevinBacon() {
			int result = ("Kevin Bacon".equalsIgnoreCase(this.getName())) ? 0 : this.getDistanceTo("Kevin Bacon", 12);
			this.console("getDistanceToKevinBacon()", "" + result);
			return result;
		}

		@Override
		public int getDistanceTo(String actorName, int targetDepth, int curDepth) {
			String method = "getDistanceTo()";
			this.console(method, actorName);
			this.console(method, "targetDepth: " + targetDepth);
			this.console(method, "curDepth: " + curDepth);


			List<Actor> costars = getCostars();
			if (targetDepth > curDepth) {
				for (Actor costar : costars) {
					System.out.println("\t Processing " + costar.getName());
					int chk = costar.getDistanceTo(actorName, targetDepth, curDepth + 1);
					if (chk < Integer.MAX_VALUE) {
						this.console(method, "" + chk);
						return chk;
					}
				}
			} else if (targetDepth == curDepth) {
				for (Actor costar : costars) {
					System.out.println("\t Processing " + costar.getName());
					if (costar.getName().equals(actorName)) {
						this.console(method, "" + curDepth);
						return curDepth;
					}
				}
			}
			this.console(method, "" + Integer.MAX_VALUE);
			return Integer.MAX_VALUE;
		}

		@Override
		public int getDistanceTo(String actorName, int maxDepth) {
			String method = "getDistanceTo()";
			this.console(method, actorName);
			this.console(method, "maxDepth: " + maxDepth);

			List<Actor> curActors = new ArrayList<Actor>();
			List<String> checkedActors = new ArrayList<String>();
			Map<Integer, List<Actor>> levelBreakdown = new HashMap<Integer, List<Actor>>();
			int chk2 = Integer.MAX_VALUE;
			for (int i = 1; i <= maxDepth; i++) {
				// If first loop, process this actor's costars
				if (curActors.isEmpty()) {
					curActors.add(this);
				}
				List<Actor> nextActors = new ArrayList<Actor>();
				for (Actor intActor : curActors) {
					List<Actor> costars = (List<Actor>) intActor.getCostars();
					for (Actor costar : costars) {
						if (!checkedActors.contains(costar.getName())) {
							if (costar.getName().equals(actorName)) {
								System.out.println("\t We have bacon!");
								// We have a route to Kevin Bacon!! Now go
								// backwards to get the path
								path_ = new ArrayList<String>();
								path_.add(actorName);
								System.out.println(i + ": " + actorName);
								for (Movie movie : intActor.getMovies()) {
									for (Actor checks : movie.getActors()) {
										if (costar.equals(checks)) {
											path_.add(movie.getTitle());
											path_.add(intActor.getName());
											System.out.println(i - 1 + ": " + intActor.getName() + " also in " + movie.getTitle());
											break;
										}
									}
								}

								Actor nextLevelActor = intActor;
								if (i > 1) {
									for (int x = (i - 2); x > 0; x--) {
										String checkActorName = nextLevelActor.getName();
										nextLevelActor = outputPath(levelBreakdown, nextLevelActor, x);
										if (nextLevelActor == null) {
											System.out.println("\t NOT FOUND: No actor found at level " + x + " from " + checkActorName);
											x = -1;
										}
									}
								}
								for (Movie movie : this.getMovies()) {
									for (Actor checks : movie.getActors()) {
										if (nextLevelActor.equals(checks)) {
											path_.add(movie.getTitle());
											path_.add(getName());
											System.out.println("\t " + 0 + ": " + getName() + " also in " + movie.getTitle());
											break;
										}
									}
								}

								Collections.reverse(path_);

								this.dv().setFrameImplObject("path", path_);

								this.console(method, "" + i);
								return i;
							}

							checkedActors.add(costar.getName());
							nextActors.add(costar);
						}
					}
					// System.out.println("Actors added " + nextActors.size());
				}

				if (chk2 < Integer.MAX_VALUE) {
					this.console(method, "" + chk2);
					return chk2;
				} else {
					curActors = new ArrayList<Actor>();
					curActors.addAll(nextActors);
					levelBreakdown.put(i, nextActors);
				}
			}

			this.console(method, "Not found.  Returning -1");
			return -1;
		}

		@Override
		public ArrayList<String> getPathToKevinBacon() {
			ArrayList<String> result = null;
			try {
				result = (ArrayList<String>) this.dv().getFrameImplObject("path");
				if (null == result) { throw new RuntimeException("FrameImplObject 'path' is null"); }

			} catch (Exception e) {
				// this.handleException(e, false);
				result = new ArrayList<String>();
				this.dv().setFrameImplObject("path", result);
			}

			return result;
		}

		private Actor outputPath(Map<Integer, List<Actor>> levelBreakdown, Actor nextLevelActor, int x) {
			List<Actor> prevLevel = levelBreakdown.get(x);
			for (Actor proc : prevLevel) {
				for (Movie movie : proc.getMovies()) {
					for (Actor checks : movie.getActors()) {
						if (checks.equals(nextLevelActor)) {
							path_.add(movie.getTitle());
							path_.add(proc.getName());
							System.out.println(x + ": " + proc.getName() + " also in " + movie.getTitle());
							return proc;
						}
					}
				}
			}
			return null;
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
}
