package uk.gov.digital.ho.hocs.notify.domain;

import lombok.Getter;

public enum NotifyType {

    INITIAL_DRAFT_REJECT("3a47cd64-2ebb-411d-8194-fd384f377ccc"),
    QA_REJECT("f604a555-9a1a-40ec-8a60-61fb495630e9"),
    PRIVATE_OFFICE_REJECT("f432a529-1b1b-49af-976d-ce23e745e474"),
    ALLOCATE_PRIVATE_OFFICE("1a889326-986d-460e-a01e-23b10793a81c"),
    MINISTER_REJECT("78df9bff-9cb0-414a-bb82-32e26847cc5a"),
    DISPATCH_REJECT("0912ba97-1043-41c0-b583-68d6e65f3de7"),
    ALLOCATE_TEAM("f9716987-395e-453f-9a59-9a8a47f23152"),
    ALLOCATE_INDIVIDUAL("3dfbd276-2bcc-4b08-81b1-d4f0583cdf39"),
    NRN_REJECT("8d4f8da4-a646-468b-8e91-3063c12ae812"),
    TRANSFER_OGD_REJECT("d860dd8a-6873-4b07-be85-022aa505a9e2"),
    UNALLOCATE_INDIVIDUAL("6c76fa5b-9bf4-4e39-8ac3-452d49f919b2"),
    OFFLINE_QA_USER("cd16d1d6-d1f1-41fd-8f14-e05b1f221443"),
    TEAM_RENAME("b2933fa2-cd13-4ddc-9b93-771420dee807"),
    TEAM_ACTIVE("e7610b1a-3308-4853-97ce-3108800b49e9");

    @Getter
    private String emailTemplateId;

    NotifyType(String value) {
        emailTemplateId = value;
    }
}
