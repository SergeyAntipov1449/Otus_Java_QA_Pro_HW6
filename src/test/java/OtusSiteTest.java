import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.inject.Inject;
import com.microsoft.playwright.Page;
import extensions.PlaywrightUIExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.*;

@ExtendWith(PlaywrightUIExtension.class)
public class OtusSiteTest {

  @Inject
  private ClickhousePage clickhousePage;


  @Inject
  private CoursePage coursePage;

  @Inject
  private SubscriptionPage subscriptionPage;

  @Inject
  private Page page;


  @Test
  void teachersBlockBehavior() {
    clickhousePage.open();
    clickhousePage.checkTeachersBlockIsVisible();

    String previousTeacherIndex = clickhousePage.currentTeacherIndex();
    clickhousePage.scrollTeachersSlider();
    clickhousePage.checkSliderMovedFrom(previousTeacherIndex);

    String expectedTeacherName = clickhousePage.openActiveTeacherCard();
    clickhousePage.checkOpenedTeacherIs(expectedTeacherName);

    String previousTeacherName = clickhousePage.openNextTeacherInPopup();
    clickhousePage.returnToPreviousTeacherInPopup(previousTeacherName);

    clickhousePage.closePopup();
  }

  @Test
  void filterCatalogBehavior() {
    coursePage.open();

    coursePage.checkDefaultFilters();

    coursePage.applyDurationFilter(3, 10);
    coursePage.checkCoursesDurationInRange(3, 10);

    coursePage.applyArchitectureFilter();
    coursePage.checkCatalogChanged();

    coursePage.resetFilters();
    coursePage.checkDefaultFilters();
    coursePage.checkCatalogChanged();
  }

  @Test
  void shouldCheckSubscriptionFlow() {
    subscriptionPage.open();

    subscriptionPage.checkSubscriptionsPresence();

    subscriptionPage.expandSubscriptionDetails();
    subscriptionPage.checkExpandedDetails();

    subscriptionPage.collapseSubscriptionDetails();
    subscriptionPage.checkCollapsedDetails();
  }
}