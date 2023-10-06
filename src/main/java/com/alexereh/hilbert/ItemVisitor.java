package com.alexereh.hilbert;

public interface ItemVisitor<T> {
	void visitItem(T item);
}
