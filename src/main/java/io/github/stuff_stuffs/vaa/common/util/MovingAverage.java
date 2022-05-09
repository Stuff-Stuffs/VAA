package io.github.stuff_stuffs.vaa.common.util;

public final class MovingAverage {
    private final double[] samples;
    private int index = 0;
    private boolean initialized;

    public MovingAverage(final int size) {
        assert size > 0;
        samples = new double[size];
    }

    public void addSample(final double sample) {
        samples[index] = sample;
        if (index == samples.length - 1) {
            initialized = true;
        }
        index = (index + 1) % samples.length;
    }

    public double avg() {
        double avg = 0;
        if (initialized) {
            for (final double sample : samples) {
                avg += sample;
            }
        } else {
            for (int i = 0; i <= index; i++) {
                avg += samples[i];
            }
        }
        return avg / samples.length;
    }

    public void reset() {
        index = 0;
        initialized = false;
    }
}
