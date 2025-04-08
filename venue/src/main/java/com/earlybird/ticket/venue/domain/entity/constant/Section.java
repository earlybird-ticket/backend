package com.earlybird.ticket.venue.domain.entity.constant;

import com.earlybird.ticket.venue.common.exception.EnumTypeNotSupportException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Section {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    F("F"),
    G("G"),
    H("H"),
    I("I"),
    J("J"),
    K("K"),
    L("L"),
    M("M"),
    N("N"),
    O("O"),
    P("P"),
    Q("Q"),
    R("R"),
    S("S"),
    T("T"),
    U("U"),
    V("V"),
    W("W"),
    X("X"),
    Y("Y"),
    Z("Z");
    ;
    private final String value;

    public static Section getByValue(String value) {
        for (Section section : Section.values()) {
            if (section.getValue().equals(value)) {
                return section;
            }
        }
        throw new EnumTypeNotSupportException();
    }
}
