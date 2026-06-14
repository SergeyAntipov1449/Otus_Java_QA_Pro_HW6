package pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import annotations.PathTemplate;
import com.google.inject.Inject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.concurrent.ThreadLocalRandom;

@PathTemplate("/subscription")
public class SubscriptionPage extends AbsBasePage {

  private int selectedCardIndex = -1;

  @Inject
  public SubscriptionPage(Page page) {
    super(page);
  }

  private Locator packagesSection() {
    return page.locator("#packages");
  }

  private Locator subscriptionCards() {
    return packagesSection().locator("div.sc-1a5myy-0");
  }

  private Locator selectedCard() {
    if (selectedCardIndex < 0) {
      throw new IllegalStateException("Случайная карточка еще не выбрана");
    }
    return subscriptionCards().nth(selectedCardIndex);
  }

  private Locator selectedCardDetailsButton() {
    return selectedCard().getByText("Подробнее");
  }

  private Locator selectedCardCollapseButton() {
    return selectedCard().getByText("Свернуть");
  }

  public void checkSubscriptionsPresence() {
    assertThat(packagesSection()).isVisible();
    assertThat(packagesSection().getByText("Варианты подписки")).isVisible();

    int cardsCount = subscriptionCards().count();
    assertTrue(cardsCount > 0, "Плитки подписок не отображаются");

    selectedCardIndex = ThreadLocalRandom.current().nextInt(cardsCount);

    assertThat(selectedCardDetailsButton()).isVisible();
  }

  public void expandSubscriptionDetails() {
    assertThat(selectedCardDetailsButton()).isVisible();
    selectedCardDetailsButton().click();
  }

  public void checkExpandedDetails() {
    assertThat(selectedCardCollapseButton()).isVisible();
  }

  public void collapseSubscriptionDetails() {
    assertThat(selectedCardCollapseButton()).isVisible();
    selectedCardCollapseButton().click();
  }

  public void checkCollapsedDetails() {
    assertThat(selectedCardDetailsButton()).isVisible();
  }
}