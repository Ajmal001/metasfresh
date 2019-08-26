package de.metas.handlingunits.reservation.interceptor;

import org.adempiere.ad.modelvalidator.annotations.Interceptor;
import org.adempiere.ad.modelvalidator.annotations.ModelChange;
import org.compiere.model.I_C_Order;
import org.compiere.model.ModelValidator;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;

import de.metas.handlingunits.reservation.HUReservationService;
import lombok.NonNull;

/*
 * #%L
 * de.metas.handlingunits.base
 * %%
 * Copyright (C) 2018 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@Component
@Interceptor(I_C_Order.class)
public class C_Order
{
	private HUReservationService huReservationService;

	public C_Order(HUReservationService huReservationService)
	{
		this.huReservationService = huReservationService;
	}

	@ModelChange(timings = ModelValidator.TYPE_AFTER_CHANGE, ifColumnsChanged = I_C_Order.COLUMNNAME_DocStatus)
	public void deleteReservations(@NonNull final I_C_Order orderRecord)
	{

		final ImmutableSet<String> docstatusesThatAllowReservation = huReservationService.getDocstatusesThatAllowReservation();
		if (docstatusesThatAllowReservation.contains(orderRecord.getDocStatus()))
		{
			return;
		}

		// TODO
	}
}