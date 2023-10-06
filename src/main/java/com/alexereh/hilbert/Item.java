package com.alexereh.hilbert;

import lombok.Getter;

public final class Item<T> {

	private final MBR env;
	@Getter
	private T item;

	public Item(MBR env, T item) {
		this.env = env;
		this.item = item;
	}

	public MBR getMBR() {
		return env;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[Env: %s, Item: %s]".formatted(env.toString(), item.toString()));
		return sb.toString();
	}
}
