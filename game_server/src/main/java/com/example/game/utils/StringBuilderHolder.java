package com.example.game.utils;

public class StringBuilderHolder {

	private final StringBuilder sb;

	public StringBuilderHolder(int capacity) {
		sb = new StringBuilder(capacity);
	}

	public StringBuilder getStringBuilder() {
		sb.setLength(0);
		return sb;
	}
}
