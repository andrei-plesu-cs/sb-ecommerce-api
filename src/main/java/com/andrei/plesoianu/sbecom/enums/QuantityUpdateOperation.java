package com.andrei.plesoianu.sbecom.enums;

import lombok.Getter;

@Getter
public enum QuantityUpdateOperation {
    DELETE(-1),
    INCREASE(1);

    private final int numRepr;

    QuantityUpdateOperation(int numRepr) {
        this.numRepr = numRepr;
    }
}
