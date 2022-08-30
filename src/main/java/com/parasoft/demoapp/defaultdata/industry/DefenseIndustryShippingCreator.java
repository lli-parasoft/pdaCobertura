package com.parasoft.demoapp.defaultdata.industry;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.defaultdata.AbstractDataCreator;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.text.MessageFormat;

@Component
@Slf4j
@Order(13) // The order of default data creation
public class DefenseIndustryShippingCreator extends AbstractDataCreator {

	protected AssetMessages assetMessages = new AssetMessages();

	@Autowired
	@Qualifier("industryDataSource")
	protected DataSource industryDataSource;

	@Value("classpath:sql/defaultdata/defense/tbl_shipping-defaultData.sql")
	protected Resource defenseDefaultShipTypesSql;

	@Override
	public void switchIndustry() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.SWITCH_INDUSTRY_DATABASE_TO), IndustryType.OUTDOOR));

		IndustryRoutingDataSource.currentIndustry = IndustryType.DEFENSE;
	}

	@Override
	public void populateData() {
		log.info(MessageFormat.format(
				messages.getString(DatabaseOperationMessages.WRITE_DEFAULT_LOCATIONS), IndustryType.OUTDOOR));

		// create shipping types
		dataInitialize(industryDataSource, defenseDefaultShipTypesSql);

		log.info(assetMessages.getString(DatabaseOperationMessages.WRITE_DONE));
	}

}
