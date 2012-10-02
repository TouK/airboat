package airboat

/**
 * An exception to throw when the (now) impossible happens
 */
class ThingThatShouldNotBeException extends RuntimeException {

    ThingThatShouldNotBeException(String message) {
        super(message)
    }
}
