package pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import annotations.PathTemplate;
import com.google.inject.Inject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PathTemplate("/catalog/courses/")
public class CoursePage extends AbsBasePage {

  private static final Pattern MONTHS_PATTERN =
      Pattern.compile("(\\d+)\\s+(месяц|месяца|месяцев)", Pattern.CASE_INSENSITIVE);

  private List<String> previousTitles = new ArrayList<>();

  @Inject
  public CoursePage(Page page) {
    super(page);
  }

  private Locator courseCards() {
    return page.locator("a[href*='/lessons/']")
        .filter(new Locator.FilterOptions().setHas(page.locator("h6")));
  }

  private List<String> getCourseTitles() {
    List<String> titles = new ArrayList<>();
    int count = courseCards().count();

    for (int i = 0; i < count; i++) {
      titles.add(courseCards().nth(i).locator("h6").first().innerText().trim());
    }

    return titles;
  }

  private void waitForCatalogChanged() {
    page.waitForFunction(
        """
        previous => JSON.stringify(
            Array.from(document.querySelectorAll("a[href*='/lessons/'] h6"))
                .map(e => e.textContent.trim())
        ) !== JSON.stringify(previous)
        """,
        previousTitles
    );
  }

  private void setSliderValue(Locator slider, int targetValue) {
    assertThat(slider).isVisible();

    String value = slider.getAttribute("aria-valuenow");
    if (value == null) {
      return;
    }

    int currentValue = Integer.parseInt(value);
    if (currentValue == targetValue) {
      return;
    }

    slider.click();
    String key = targetValue > currentValue ? "ArrowRight" : "ArrowLeft";

    for (int i = 0; i < Math.abs(targetValue - currentValue); i++) {
      slider.press(key);
    }
  }

  private boolean isChecked(String labelText) {
    return Boolean.parseBoolean(
        page.locator("label:text-is('" + labelText + "')")
            .locator("..")
            .locator("input")
            .first()
            .evaluate("el => el.checked")
            .toString()
    );
  }

  public void checkDefaultFilters() {
    assertThat(page.locator("label:text-is('Все направления')").first()).isVisible();
    assertThat(page.locator("label:text-is('Любой уровень')").first()).isVisible();
  }

  public void applyDurationFilter(int minMonths, int maxMonths) {
    setSliderValue(page.locator("[role='slider']").nth(0), minMonths);
    setSliderValue(page.locator("[role='slider']").nth(1), maxMonths);
    page.waitForTimeout(1500);
  }

  public void checkCoursesDurationInRange(int minMonths, int maxMonths) {
    int count = courseCards().count();
    assertTrue(count > 0, "Каталог курсов пуст");

    for (int i = 0; i < count; i++) {
      Matcher matcher = MONTHS_PATTERN.matcher(courseCards().nth(i).innerText().trim());

      if (!matcher.find()) {
        continue;
      }

      int months = Integer.parseInt(matcher.group(1));
      assertTrue(months >= minMonths && months <= maxMonths,
          "Есть курс вне выбранного диапазона");
    }
  }

  public void applyArchitectureFilter() {
    previousTitles = getCourseTitles();

    Locator architecture = page.locator("label:text-is('Архитектура')").first();
    assertThat(architecture).isVisible();
    architecture.click();

    waitForCatalogChanged();
  }

  public void resetFilters() {
    previousTitles = getCourseTitles();

    Locator resetButton = page.getByRole(
        AriaRole.BUTTON,
        new Page.GetByRoleOptions().setName("Очистить фильтры")
    ).first();

    assertThat(resetButton).isVisible();
    resetButton.click();

    waitForCatalogChanged();
  }

  public void checkCatalogChanged() {
    List<String> currentTitles = getCourseTitles();

    assertFalse(currentTitles.isEmpty(), "Каталог курсов пуст");
    assertNotEquals(previousTitles, currentTitles, "Каталог курсов не изменился");
  }
}