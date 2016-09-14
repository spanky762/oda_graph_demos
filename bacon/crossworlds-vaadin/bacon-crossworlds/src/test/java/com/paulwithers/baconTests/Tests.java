package com.paulwithers.baconTests;

import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

import com.paulwithers.bacon.GraphBootstrapper;
import com.paulwithers.bacon.model.Actor;

import lotus.domino.NotesThread;

public class Tests {

	public static void main(String[] args) {
		Factory.startup();
		Factory.initThread(Factory.STRICT_THREAD_CONFIG);

		// Override the default session factory.
		Factory.setSessionFactory(Factory.getSessionFactory(SessionType.NATIVE), SessionType.CURRENT);

		NotesThread.sinitThread();
		Actor actor = GraphBootstrapper.getInstance().getActor("Adam Sandler");
		System.out.println("Distance is " + actor.getDistanceToKevinBacon());
		NotesThread.stermThread();
		Factory.termThread();
		Factory.shutdown();
	}

}
