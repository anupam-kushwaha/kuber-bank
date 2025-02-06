package com.anx.kuber_bank.constants;

import lombok.Getter;

public class AccountConstants {

    @Getter
    public enum AccountStatus {
        ACTIVE,
        INACTIVE,
        DORMANT,
        CLOSED
    }
}
