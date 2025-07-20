package me.drex.fafpatch.impl.entity.model.emuvanilla.model;

public class UVPair {
    private final float u;
    private final float v;

    public UVPair(float f, float g) {
        this.u = f;
        this.v = g;
    }

    public float u() {
        return this.u;
    }

    public float v() {
        return this.v;
    }

    public String toString() {
        return "(" + this.u + "," + this.v + ")";
    }
}
