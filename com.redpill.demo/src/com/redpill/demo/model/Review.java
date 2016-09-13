package com.redpill.demo.model;

import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import org.openntf.domino.graph2.annotations.TypedProperty;
import org.openntf.domino.graph2.builtin.DVertexFrame;
import org.openntf.domino.graph2.builtin.social.Commentable;
import org.openntf.domino.graph2.builtin.social.Likeable;
import org.openntf.domino.graph2.builtin.social.Rateable;

@TypeValue("Book Review")
public interface Review extends Commentable, DVertexFrame, Likeable, Rateable {

	@TypedProperty("BookTitle")
	public String getTitle();

	@TypedProperty("BookTitle")
	public void setTitle(String title);

	@TypedProperty("BookAuthor")
	public String getAuthor();

	@TypedProperty("BookAuthor")
	public void setAuthor(String author);

	@TypedProperty("Genre")
	public String getGenre();

	@TypedProperty("Genre")
	public void setGenre(String genre);

}
