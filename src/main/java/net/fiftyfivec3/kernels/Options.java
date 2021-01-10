package net.fiftyfivec3.kernels;

public enum Options {
    NONE(0), EMBOSS(1), OUTLINE(2), SOBEL(3), SHARPEN(4);

    public final int opt;
    Options(int value) {
        this.opt = value;
    }
}
