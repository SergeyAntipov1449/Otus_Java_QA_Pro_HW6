package commons;

import com.microsoft.playwright.Page;

public abstract class AbsCommon {

  protected Page page;

  public AbsCommon(Page page) {
    this.page = page;
  }
}
