package com.veritasware.neto.model;

import com.veritasware.neto.annotation.NetoNotNullOrNotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by chacker on 2016-10-28.
 */
@EqualsAndHashCode(of = "userId")
@Getter
@Setter
public class UserInfo {

    @NetoNotNullOrNotEmpty
    private String userId;
    private String connectTime;
    private String currentRoomId;
    private String roomEnterTime;

    private int accessLevel;

}
