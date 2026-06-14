package exceptions;

public class PathNotFoundException extends RuntimeException {
  public PathNotFoundException() {
    super("Annotation not found");
  }
}
