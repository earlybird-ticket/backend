package com.earlybird.ticket.admin.application;

import com.earlybird.ticket.admin.application.dto.DeleteCouponCommand;
import com.earlybird.ticket.admin.application.dto.DeleteSeatCommand;
import com.earlybird.ticket.admin.application.dto.DeleteVenueCommand;
import com.earlybird.ticket.admin.application.dto.IssueCouponCommand;
import com.earlybird.ticket.admin.application.dto.RegisterSeatCommand;
import com.earlybird.ticket.admin.application.dto.RegisterVenueCommand;
import com.earlybird.ticket.admin.application.dto.UpdateCouponCommand;
import com.earlybird.ticket.admin.application.dto.UpdateSeatCommand;
import com.earlybird.ticket.admin.application.dto.UpdateVenueCommand;

public interface AdminService {

    void issueCoupon(IssueCouponCommand command);

    void modifyCoupon(UpdateCouponCommand updateCouponCommand);

    void withdrawCoupon(DeleteCouponCommand deleteCouponCommand);

    void registerVenue(RegisterVenueCommand registerVenueCommand);

    void modifyVenue(UpdateVenueCommand updateVenueCommand);

    void withdrawVenue(DeleteVenueCommand deleteVenueCommand);

    void modifiedSeat(UpdateSeatCommand updateSeatCommand);

    void registerSeat(RegisterSeatCommand registerSeatCommand);

    void withdrawSeat(DeleteSeatCommand deleteSeatCommand);
}
