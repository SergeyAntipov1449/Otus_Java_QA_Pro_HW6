package pages;

import annotations.Path;
import annotations.PathTemplate;
import com.microsoft.playwright.Page;
import exceptions.PathNotFoundException;

public abstract class AbsBasePage {

  protected Page page;

  public AbsBasePage(Page page) {
    this.page = page;
  }

  private String getPath(String... data) {
    Class<?> clazz = getClass();
    if (clazz.isAnnotationPresent(PathTemplate.class)) {
      PathTemplate pathTemplete = clazz.getDeclaredAnnotation(PathTemplate.class);
      String template = pathTemplete.value();
      int i = 0;
      for (String pathData : data) {
        template = template.replace("$" + (i + 1), pathData);
        i++;
      }
      return template;
    }
    if (clazz.isAnnotationPresent(Path.class)) {
      Path path = clazz.getDeclaredAnnotation(Path.class);
      return path.value();
    }
    throw new PathNotFoundException();
  }

  public void open() {
    page.navigate(System.getProperty("base.url") + getPath());
  }
}
