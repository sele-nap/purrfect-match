package io.github.selenap.purrfectmatch.adoption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import io.github.selenap.purrfectmatch.cat.Cat;
import io.github.selenap.purrfectmatch.cat.CatStatus;
import io.github.selenap.purrfectmatch.cat.Sex;
import io.github.selenap.purrfectmatch.common.error.BusinessRuleException;

/** Plain unit tests of the business rules, no Spring involved. */
class AdoptionRequestTest {

    private final Cat miso = new Cat("Miso", Sex.FEMALE, null, null);

    private AdoptionRequest newRequest() {
        return new AdoptionRequest(miso, "Alex", "alex@example.com", null);
    }

    @Test
    void approveReservesTheCat() {
        AdoptionRequest request = newRequest();

        request.approve();

        assertThat(request.getStatus()).isEqualTo(AdoptionStatus.APPROVED);
        assertThat(miso.getStatus()).isEqualTo(CatStatus.RESERVED);
    }

    @Test
    void cannotApproveTwoRequestsForTheSameCat() {
        newRequest().approve();

        assertThatThrownBy(() -> newRequest().approve())
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void rejectingAnApprovedRequestReleasesTheCat() {
        AdoptionRequest request = newRequest();
        request.approve();

        request.reject();

        assertThat(miso.getStatus()).isEqualTo(CatStatus.AVAILABLE);
    }

    @Test
    void completeRequiresApprovalFirst() {
        assertThatThrownBy(() -> newRequest().complete())
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cannotSubmitForAnAdoptedCat() {
        AdoptionRequest request = newRequest();
        request.approve();
        request.complete();

        assertThatThrownBy(this::newRequest)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already been adopted");
    }
}
