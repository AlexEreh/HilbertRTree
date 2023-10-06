package com.alexereh.hilbert;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class ListVisitor<T> implements ItemVisitor<T> {
	/**
	 * -- GETTER --
	 */
	private final List<T> items = new ArrayList<>();

	public ListVisitor() {
	}

	public void visitItem(T item)
	{
		items.add(item);
	}

}
