package de.metas.phonecall.model.interceptor;

import java.time.LocalDate;

import org.adempiere.ad.modelvalidator.IModelValidationEngine;
import org.adempiere.ad.modelvalidator.annotations.Init;
import org.adempiere.ad.modelvalidator.annotations.Interceptor;
import org.adempiere.ad.modelvalidator.annotations.ModelChange;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.CopyRecordFactory;
import org.compiere.model.I_C_Phonecall_Schema_Version;
import org.compiere.model.ModelValidator;
import org.compiere.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.metas.i18n.IMsgBL;
import de.metas.i18n.ITranslatableString;
import de.metas.phonecall.PhonecallSchema;
import de.metas.phonecall.PhonecallSchemaId;
import de.metas.phonecall.PhonecallSchemaVersion;
import de.metas.phonecall.PhonecallSchemaVersionId;
import de.metas.phonecall.PhonecallSchemaVersionPOCopyRecordSupport;
import de.metas.phonecall.service.PhonecallSchemaRepository;
import de.metas.util.Services;

/*
 * #%L
 * de.metas.business
 * %%
 * Copyright (C) 2019 metas GmbH
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

@Interceptor(I_C_Phonecall_Schema_Version.class)
@Component("de.metas.phonecall.C_Phonecall_Schema_Version")
public class C_Phonecall_Schema_Version
{

	private static final String MSG_Existing_Phonecall_Schema_Version_Same_ValidFrom = "C_Phonecall_Schema_Version_ExistingVersionSameValidFrom";

	@Autowired
	private PhonecallSchemaRepository phonecallSchemaRepo;

	@Init
	public void init(final IModelValidationEngine engine)
	{
		CopyRecordFactory.enableForTableName(I_C_Phonecall_Schema_Version.Table_Name);

		CopyRecordFactory.registerCopyRecordSupport(I_C_Phonecall_Schema_Version.Table_Name, PhonecallSchemaVersionPOCopyRecordSupport.class);
	}

	@ModelChange(timings = { ModelValidator.TYPE_BEFORE_NEW, ModelValidator.TYPE_BEFORE_CHANGE }, ifColumnsChanged = { I_C_Phonecall_Schema_Version.COLUMNNAME_ValidFrom })
	public void forbidNewVersionWithSameValidFromDate(final I_C_Phonecall_Schema_Version phonecallSchemaVersion)
	{
		final IMsgBL msgBL = Services.get(IMsgBL.class);

		final PhonecallSchemaId schemaId = PhonecallSchemaId.ofRepoId(phonecallSchemaVersion.getC_Phonecall_Schema_ID());
		final PhonecallSchemaVersionId versionId = PhonecallSchemaVersionId.ofRepoIdOrNull(schemaId, phonecallSchemaVersion.getC_Phonecall_Schema_Version_ID());
		final LocalDate validFrom = TimeUtil.asLocalDate(phonecallSchemaVersion.getValidFrom());

		final PhonecallSchema phonecallSchema = phonecallSchemaRepo.getById(schemaId);
		final PhonecallSchemaVersion existingVersion = phonecallSchema.getVersionByValidFrom(validFrom).orElse(null);

		if (existingVersion != null
				&& (versionId == null || !versionId.equals(existingVersion.getId())))
		{
			final ITranslatableString noPermissionMessage = msgBL.getTranslatableMsgText(MSG_Existing_Phonecall_Schema_Version_Same_ValidFrom,
					phonecallSchemaVersion.getName(),
					validFrom);

			throw new AdempiereException(noPermissionMessage);
		}
	}
}
