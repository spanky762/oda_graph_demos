package com.paulwithers.bacon.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openntf.domino.graph2.DVertex;
import org.openntf.domino.graph2.annotations.AdjacencyUnique;
import org.openntf.domino.graph2.annotations.IncidenceUnique;
import org.openntf.domino.graph2.annotations.TypedProperty;

import com.paulwithers.bacon.model.Movie.StarsIn;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerClass;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeField;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@JavaHandlerClass(Actor.ActorImpl.class)
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
		ArrayList<String> path_ = new ArrayList<String>();

		@Override
		public List<Actor> getCostars() {
			List<Actor> costars = (List) ((DVertex) asVertex()).getFrameImplObject("costars");
			if (costars == null) {
				costars = new ArrayList<Actor>();
				for (Movie movie : getMovies()) {
					// System.out.println("Checking " + " - " +
					// movie.getTitle());
					for (Actor actor : movie.getActors()) {
						if (!getName().equals(actor.getName())) {
							costars.add(actor);
						}
					}
				}
				costars.remove(this);
				((DVertex) asVertex()).setFrameImplObject("costars", costars);
			}
			return costars;
		}

		@Override
		public int getCostarCount() {
			return getCostars().size();
		}

		@Override
		public int getDistanceToKevinBacon() {
			return getDistanceTo("Kevin Bacon", 12);
		}

		@Override
		public int getDistanceTo(String actorName, int targetDepth, int curDepth) {
			List<Actor> costars = getCostars();
			if (targetDepth > curDepth) {
				for (Actor costar : costars) {
					System.out.println("Processing " + costar.getName());
					int chk = costar.getDistanceTo(actorName, targetDepth, curDepth + 1);
					if (chk < Integer.MAX_VALUE) {
						return chk;
					}
				}
			} else if (targetDepth == curDepth) {
				for (Actor costar : costars) {
					System.out.println("Processing " + costar.getName());
					if (costar.getName().equals(actorName)) {
						return curDepth;
					}
				}
			}
			return Integer.MAX_VALUE;
		}

		@Override
		public int getDistanceTo(String actorName, int maxDepth) {

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
								// We have a route to Kevin Bacon!! Now go
								// backwards to get the path
								path_ = new ArrayList<String>();
								path_.add(actorName);
								System.out.println(i + ": " + actorName);
								for (Movie movie : costar.getMovies()) {
									for (Actor checks : movie.getActors()) {
										if (intActor.equals(checks)) {
											path_.add(movie.getTitle());
											path_.add(intActor.getName());
											System.out.println(
													i + ": " + intActor.getName() + " also in " + movie.getTitle());
										}
									}
								}
								if (i > 1) {
									Actor nextLevelActor = intActor;
									for (int x = (i - 2); x > 0; x--) {
										String checkActorName = nextLevelActor.getName();
										nextLevelActor = outputPath(levelBreakdown, nextLevelActor, x);
										if (nextLevelActor == null) {
											System.out.println(
													"ERROR: No actor found at level " + x + " from " + checkActorName);
											x = -1;
										}
									}
									for (Movie movie : nextLevelActor.getMovies()) {
										for (Actor checks : movie.getActors()) {
											if (getName().equals(checks.getName())) {
												path_.add(movie.getTitle());
												path_.add(getName());
												System.out
														.println(i + ": " + getName() + " also in " + movie.getTitle());
											}
										}
									}
								}

								Collections.reverse(path_);

								((DVertex) asVertex()).setFrameImplObject("path", path_);

								return i;
							}
							checkedActors.add(costar.getName());
							nextActors.add(costar);
						}
					}
					// System.out.println("Actors added " + nextActors.size());
				}

				if (chk2 < Integer.MAX_VALUE) {
					return chk2;
				} else {
					curActors = new ArrayList<Actor>();
					curActors.addAll(nextActors);
					levelBreakdown.put(i, nextActors);
				}
			}

			return -1;
		}

		@Override
		public ArrayList<String> getPathToKevinBacon() {
			ArrayList<String> path = (ArrayList<String>) ((DVertex) asVertex()).getFrameImplObject("path");
			return path;
		}

		private Actor outputPath(Map<Integer, List<Actor>> levelBreakdown, Actor intActor, int x) {
			List<Actor> prevLevel = levelBreakdown.get(x);
			for (Movie movie : intActor.getMovies()) {
				for (Actor checks : movie.getActors()) {
					if (prevLevel.contains(checks)) {
						path_.add(movie.getTitle());
						path_.add(checks.getName());
						System.out.println(x + ": " + checks.getName() + " also in " + movie.getTitle());
						return checks;
					}
				}
			}
			return null;
		}

	}
}
