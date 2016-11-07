package com.lakeel.altla.vision.builder.data.repository.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class RealmImageReference extends RealmObject {

    public static final String FIELD_CREATE_DATE = "createDate";

    @PrimaryKey
    public String uri;

    @Required
    public Date createDate;
}
