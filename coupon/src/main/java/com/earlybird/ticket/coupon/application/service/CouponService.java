package com.earlybird.ticket.coupon.application.service;

import com.earlybird.ticket.coupon.application.dto.ProcessCouponQuery;
import com.earlybird.ticket.coupon.application.dto.ProcessCouponReserveCommand;
import com.earlybird.ticket.coupon.application.dto.ProcessUserCouponQuery;
import java.util.List;

public interface CouponService {

    List<ProcessCouponQuery> getCouponList(String passport);

    ProcessUserCouponQuery getUserCouponList(String passport);

    void reserveCoupon(String passport, ProcessCouponReserveCommand request);
}
