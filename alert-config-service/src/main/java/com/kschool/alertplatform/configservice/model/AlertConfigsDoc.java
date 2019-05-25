package com.kschool.alertplatform.configservice.model;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import com.kschool.alertplatform.common.model.alert.AlertConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.couchbase.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class AlertConfigsDoc {

    @Id
    public String id;

    @Field
    public List<AlertConfig> alertConfigs;
}
