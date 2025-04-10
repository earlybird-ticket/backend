package com.earlybird.ticket.admin.application;

import com.earlybird.ticket.admin.application.dto.DeleteCouponCommand;
import com.earlybird.ticket.admin.application.dto.DeleteVenueCommand;
import com.earlybird.ticket.admin.application.dto.IssueCouponCommand;
import com.earlybird.ticket.admin.application.dto.RegisterVenueCommand;
import com.earlybird.ticket.admin.application.dto.UpdateCouponCommand;
import com.earlybird.ticket.admin.application.dto.UpdateVenueCommand;
import com.earlybird.ticket.admin.domain.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Override
    public void issueCoupon(IssueCouponCommand command) {

    }

    @Override
    public void modifyCoupon(UpdateCouponCommand updateCouponCommand) {

    }

    @Override
    public void withdrawCoupon(DeleteCouponCommand deleteCouponCommand) {

    }

    @Override
    public void registerVenue(RegisterVenueCommand registerVenueCommand) {

    }

    @Override
    public void modifyVenue(UpdateVenueCommand updateVenueCommand) {

    }

    @Override
    public void withdrawVenue(DeleteVenueCommand deleteVenueCommand) {

    }
}
