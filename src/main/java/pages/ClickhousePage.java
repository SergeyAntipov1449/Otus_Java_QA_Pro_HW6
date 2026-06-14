package pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import annotations.PathTemplate;
import com.google.inject.Inject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.BoundingBox;

@PathTemplate("/lessons/clickhouse/")
public class ClickhousePage extends AbsBasePage {

  @Inject
  public ClickhousePage(Page page) {
    super(page);
  }

  private Locator teachersSection() {
    return page.locator("section")
        .filter(new Locator.FilterOptions().setHas(
            page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Преподаватели"))
        ))
        .first();
  }

  private Locator activeSliderCard() {
    return teachersSection().locator(".swiper-slide-active").first();
  }

  private Locator popup() {
    return page.locator("#__PORTAL__ .sc-13monb3-1").first();
  }

  private Locator activePopupCard() {
    return popup().locator(".swiper-slide.swiper-slide-active").first();
  }

  private String activeSliderTeacherName() {
    return activeSliderCard().locator("p.sc-1s527z5-1").first().innerText().trim();
  }

  private String activePopupTeacherName() {
    assertThat(popup()).isVisible();
    return activePopupCard().locator("h3").first().innerText().trim();
  }

  private Locator popupButton(int index) {
    return popup().locator("button").nth(index);
  }

  public void checkTeachersBlockIsVisible() {
    Locator section = teachersSection();

    assertThat(section).isVisible();
    assertThat(section.getByRole(
        AriaRole.HEADING,
        new Locator.GetByRoleOptions().setName("Преподаватели")
    )).isVisible();
    assertThat(section.locator(".swiper").first()).isVisible();

    Locator cards = section.locator(".swiper-slide");
    assertTrue(cards.count() > 0, "Карточки преподавателей не отображаются");
    assertThat(cards.first()).isVisible();
  }

  public String currentTeacherIndex() {
    return activeSliderCard().getAttribute("data-swiper-slide-index");
  }

  public void scrollTeachersSlider() {
    Locator card = activeSliderCard();
    Locator dragArea = card.locator("xpath=.//*[contains(@class,'sc-jotj87-1')]").first();
    Locator target = dragArea.count() > 0 ? dragArea : card;

    assertThat(target).isVisible();
    target.scrollIntoViewIfNeeded();

    BoundingBox box = target.boundingBox();

    double startX = box.x + box.width * 0.85;
    double endX = box.x - box.width * 0.50;
    double y = box.y + box.height * 0.50;

    page.mouse().move(startX, y);
    page.mouse().down();

    page.mouse().move(startX - 80, y, new Mouse.MoveOptions().setSteps(10));
    page.mouse().move(startX - 180, y, new Mouse.MoveOptions().setSteps(12));
    page.mouse().move(endX, y, new Mouse.MoveOptions().setSteps(18));

    page.mouse().up();
    page.waitForTimeout(1000);
  }

  public void checkSliderMovedFrom(String previousTeacherIndex) {
    assertThat(activeSliderCard()).not().hasAttribute("data-swiper-slide-index", previousTeacherIndex);
  }

  public String openActiveTeacherCard() {
    String expectedTeacherName = activeSliderTeacherName();

    activeSliderCard().click();

    assertThat(popup()).isVisible();
    assertThat(activePopupCard()).isVisible();

    return expectedTeacherName;
  }

  public void checkOpenedTeacherIs(String expectedTeacherName) {
    assertEquals(
        expectedTeacherName,
        activePopupTeacherName(),
        "Popup не соответствует выбранному преподавателю"
    );
  }

  public String openNextTeacherInPopup() {
    String previousTeacherName = activePopupTeacherName();

    Locator nextButton = popupButton(2);
    assertThat(nextButton).isVisible();
    nextButton.click();

    page.waitForTimeout(800);

    String currentTeacherName = activePopupTeacherName();
    assertNotEquals(
        previousTeacherName,
        currentTeacherName,
        "Карточка преподавателя не изменилась после перехода"
    );

    return previousTeacherName;
  }

  public void returnToPreviousTeacherInPopup(String expectedTeacherName) {
    Locator previousButton = popupButton(1);
    assertThat(previousButton).isVisible();
    previousButton.click();

    page.waitForTimeout(800);

    assertEquals(
        expectedTeacherName,
        activePopupTeacherName(),
        "Карточка преподавателя не вернулась к исходной"
    );
  }

  public void closePopup() {
    Locator closeButton = popupButton(0);

    if (closeButton.count() > 0) {
      closeButton.click();
    } else {
      page.keyboard().press("Escape");
    }
    page.waitForTimeout(500);
  }
}