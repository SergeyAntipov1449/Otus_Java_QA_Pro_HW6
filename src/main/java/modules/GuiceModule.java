package modules;

import com.google.inject.AbstractModule;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;

public class GuiceModule extends AbstractModule {

  private final Page page;
  private final Browser browser;

  public GuiceModule(Page page, Browser browser) {
    this.page = page;
    this.browser = browser;
  }

  @Override
  protected void configure() {
    bind(Page.class).toInstance(page);
    bind(Browser.class).toInstance(browser);
  }
}