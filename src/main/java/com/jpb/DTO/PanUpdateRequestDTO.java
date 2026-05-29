package com.jpb.DTO;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PanUpdateRequestDTO {

	private String applicationNumber;

	private String apiVersion;
	private String applicationType;
	private String applicationSubType;
	private String initiatingEntityId;
	private String app;

	private ActionDTO action;
	private List<PersonPanDTO> persons;
}
