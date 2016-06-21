// Copyright (c) Yugabyte, Inc.

package models.yb;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJson;
import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class Instance extends Model {

	public enum ProvisioningState {
		@EnumValue("Pending")
		Pending,

		@EnumValue("Processing")
		Processing,

		@EnumValue("Completed")
		Completed,

		@EnumValue("Failed")
		Failed,
	}

	@EmbeddedId
	private CustomerInstanceKey key;
	public UUID getInstanceId() { return this.key.instanceId; }
	public UUID getCustomerId() { return this.key.customerId; }

	@Constraints.Required
	@Column(nullable = false)
	public String name;

	@Constraints.Required
	@ManyToOne
	@JsonBackReference
	public Customer customer;

	@Constraints.Required
	@Column(nullable = false)
	@DbJson
	JsonNode placementInfo;
	public JsonNode getPlacementInfo() { return this.placementInfo; }

	@Constraints.Required
	@Column(nullable = false)
	public ProvisioningState provisioningState;

	@Constraints.Required
	@Column(nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	public Date creationDate;

	/**
	 * Query Helper for Instance with primary key
	 */
	public static final Find<Integer, Instance> find = new Find<Integer, Instance>(){};

	/**
	 * Create a new Yuga Instance
	 *
	 * @param customer
	 * @param name
	 * @param state
	 * @param placementInfo
	 * @return Yuga Instance
	 */
	public static Instance create(Customer customer, String name, ProvisioningState state, JsonNode placementInfo) {
		Instance instance = new Instance();
		instance.key = CustomerInstanceKey.create(UUID.randomUUID(), customer.uuid);
		instance.customer = customer;
		instance.name = name;
		instance.provisioningState = state;
		instance.placementInfo = placementInfo;
		instance.creationDate = new Date();
		instance.save();
		return instance;
	}
}

