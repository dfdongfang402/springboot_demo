package com.example.game.core;

public interface StopperMXBean {	
	void setStopTime(long time);
	long getStopTime();
	void stop(int seconds);
}
