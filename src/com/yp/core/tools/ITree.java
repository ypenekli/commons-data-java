package com.yp.core.tools;

public interface ITree<T> {

	public T getValue();

	public T getParentValue();

	public String getName();

	public String getDescription();

	public boolean isLeaf();
}
