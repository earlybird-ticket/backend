package com.earlybird.ticket.admin.application;

import com.earlybird.ticket.admin.application.dto.DeleteCouponCommand;
import com.earlybird.ticket.admin.application.dto.DeleteSeatInstanceCommand;
import com.earlybird.ticket.admin.application.dto.DeleteVenueCommand;
import com.earlybird.ticket.admin.application.dto.IssueCouponCommand;
import com.earlybird.ticket.admin.application.dto.RegisterCouponCommand;
import com.earlybird.ticket.admin.application.dto.RegisterSeatCommand;
import com.earlybird.ticket.admin.application.dto.RegisterVenueCommand;
import com.earlybird.ticket.admin.application.dto.UpdateCouponCommand;
import com.earlybird.ticket.admin.application.dto.UpdateSeatInstanceCommand;
import com.earlybird.ticket.admin.application.dto.UpdateVenueCommand;

public interface AdminService {

    void issueCoupon(String passport, IssueCouponCommand command);

    void registerCoupon(String passport, RegisterCouponCommand command);

    void modifyCoupon(String passport, UpdateCouponCommand updateCouponCommand);

    void withdrawCoupon(String passport, DeleteCouponCommand deleteCouponCommand);

    void registerVenue(String passport, RegisterVenueCommand registerVenueCommand);

    void modifyVenue(String passport, UpdateVenueCommand updateVenueCommand);

    void withdrawVenue(String passport, DeleteVenueCommand deleteVenueCommand);

    void modifySeatInstance(String passport, UpdateSeatInstanceCommand updateSeatInstanceCommand);

    void registerSeat(String passport, RegisterSeatCommand registerSeatCommand);

    void withdrawSeatInstance(String passport, DeleteSeatInstanceCommand deleteSeatInstanceCommand);
}
