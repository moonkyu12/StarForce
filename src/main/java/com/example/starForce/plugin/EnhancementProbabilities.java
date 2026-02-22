package com.example.starForce.plugin;

public class EnhancementProbabilities {
    private final double success;
    private final double failure;
    private final double demotion;
    private final double destruction;

    public EnhancementProbabilities(double success, double failure, double demotion, double destruction) {
        this.success = success;
        this.failure = failure;
        this.demotion = demotion;
        this.destruction = destruction;
    }

    public double getSuccess() {
        return success;
    }

    public double getFailure() {
        return failure;
    }

    public double getDemotion() {
        return demotion;
    }

    public double getDestruction() {
        return destruction;
    }
}