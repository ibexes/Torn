package com.torn.assistant.persistence.converters;

import com.torn.api.model.faction.OrganisedCrimeType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OrganisedCrimeTypeConverter implements AttributeConverter<OrganisedCrimeType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OrganisedCrimeType attribute) {
        return attribute.getId();
    }

    @Override
    public OrganisedCrimeType convertToEntityAttribute(Integer dbData) {
        return OrganisedCrimeType.convertToOrganisedCrimeType(dbData);
    }
}
