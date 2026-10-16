package io.github.selenap.purrfectmatch.cat;

public enum CatStatus {
    /** Listed and adoptable. */
    AVAILABLE,
    /** An adoption request was approved and is awaiting completion. */
    RESERVED,
    /** Adopted: no further requests allowed. */
    ADOPTED
}
