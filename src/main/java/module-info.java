// Note: We can remove the SuppressWarnings once we have used at least one of
// the checker framework annotations.
@SuppressWarnings({
    "requires-static-automatic", "requires-automatic"
}) module com.rainerhahnekamp.sneakythrow {
    exports com.rainerhahnekamp.sneakythrow.functional;

    // Need to add this for checker framework Java 9+ compatibility, even if I
    // do not use any of the annotations See:
    //
    // https://github.com/kelloggm/checkerframework-gradle-plugin/blob/ea4a7ae6bffaae39b71c02fa1c640d71b0d7c9da/README.md#java-9-compatibility
    requires static org.checkerframework.checker.qual;
}
