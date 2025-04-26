package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class SeatPreemptionMismatchException extends AbstractVenueException {
  public SeatPreemptionMismatchException() {
    super("좌석 확정 과정에서 문제가 발생했습니다.", Code.SEAT_CONFIRM_FAIL);
  }
}
